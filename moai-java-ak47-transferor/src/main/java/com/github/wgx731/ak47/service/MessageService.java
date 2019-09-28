package com.github.wgx731.ak47.service;

import com.github.wgx731.ak47.message.FinishTransferMsg;
import com.github.wgx731.ak47.message.MessageQueueConst;
import com.github.wgx731.ak47.message.Receiver;
import com.github.wgx731.ak47.message.TriggerMsg;
import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.repository.PhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MessageService implements Receiver {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @NonNull
    private RabbitTemplate template;

    @NonNull
    private PhotoRepository photoRepository;

    @Value("${vfs.url}")
    private String vfsUrl;

    public void receiveMessage(TriggerMsg msg) {
        log.info(String.format("receive trigger process message %s", msg));
        Optional<Photo> optional = photoRepository.findById(msg.getId());
        if (!optional.isPresent()) {
            log.warn(String.format("no photo found for %s", msg.toString()));
            return;
        }
        Photo photo = optional.get();
        if (Objects.isNull(photo.getData())) {
            photo.setStatus(Photo.ProcessStatus.TRANSFER_FAILED);
            log.error(String.format("fail to transfer %d photo data is null", photo.getId()));
        } else {
            photo.setStatus(Photo.ProcessStatus.RUNNING);
            updatePhoto(photo);
            log.info(String.format("start transferring %d", photo.getId()));
            try {
                String storageUrl = transferFile(photo);
                photo.setStorageUrl(storageUrl);
                photo.setStatus(Photo.ProcessStatus.TRANSFERRED);
                log.info(String.format("finish transferring %d", photo.getId()));
            } catch (IOException e) {
                photo.setStatus(Photo.ProcessStatus.TRANSFER_FAILED);
                log.error(String.format("fail to transfer %d", photo.getId()), e);
            }
        }
        updatePhoto(photo);
        FinishTransferMsg returnMsg = new FinishTransferMsg();
        returnMsg.setId(photo.getId());
        returnMsg.setStorageUrl(photo.getStorageUrl());
        returnMsg.setStatus(photo.getStatus().toString());
        log.info(String.format("send finish transfer message %s", returnMsg));
        template.convertAndSend(
            MessageQueueConst.EXCHANGE_NAME,
            MessageQueueConst.FINISH_TRANSFER_QUEUE,
            returnMsg
        );
    }

    private String transferFile(Photo photo) throws IOException {
        FileSystemManager manager = VFS.getManager();
        String path = String.format(
            "%s/%s_%s_%s.%s",
            vfsUrl,
            photo.getProject().getName(),
            dateFormat.format(photo.getCreationDate()),
            photo.getUploader(),
            photo.getImageType().contains("png") ? "png" : "jpg"
        );
        // NOTE: remove all white spaces
        path = path.replaceAll("\\s+", "-");
        log.info(String.format("remote path: %s", path));
        FileObject remoteFile = manager.resolveFile(path);
        remoteFile.getContent().getOutputStream().write(photo.getData());
        return path;
    }

    @Transactional
    private void updatePhoto(Photo photo) {
        photoRepository.save(photo);
    }

}
