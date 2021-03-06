package com.github.wgx731.ak47.service;

import com.github.wgx731.ak47.message.FinishProcessMsg;
import com.github.wgx731.ak47.message.MessageQueueConst;
import com.github.wgx731.ak47.message.Receiver;
import com.github.wgx731.ak47.message.TriggerMsg;
import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.repository.PhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MessageService implements Receiver {

    @NonNull
    private RabbitTemplate template;

    @NonNull
    private PhotoRepository photoRepository;

    public void receiveMessage(TriggerMsg msg) {
        log.info(String.format("receive trigger process message %s", msg));
        Optional<Photo> optional = photoRepository.findById(msg.getId());
        if (!optional.isPresent()) {
            log.warn(String.format("no photo found for %s", msg.toString()));
            return;
        }
        Photo photo = optional.get();
        if (Objects.isNull(photo.getData())) {
            photo.setStatus(Photo.ProcessStatus.PROCESS_FAILED);
            log.error("fail to process %d photo data is null", photo.getId());
        } else {
            photo.setStatus(Photo.ProcessStatus.RUNNING);
            updatePhoto(photo);
            log.info(String.format("start processing %d", photo.getId()));
            try {
                byte[] dataWithText = appendText(
                    photo.getData(),
                    photo.getImageType(),
                    new Timestamp(System.currentTimeMillis()).toString()
                );
                photo.setData(dataWithText);
                photo.setStatus(Photo.ProcessStatus.PROCESSED);
                log.info(String.format("finish processing %d", photo.getId()));
            } catch (IOException e) {
                photo.setStatus(Photo.ProcessStatus.PROCESS_FAILED);
                log.error("fail to process %d", photo.getId());
            }
        }
        updatePhoto(photo);
        FinishProcessMsg returnMsg = new FinishProcessMsg();
        returnMsg.setId(photo.getId());
        returnMsg.setUploader(photo.getUploader());
        returnMsg.setStatus(photo.getStatus().toString());
        log.info(String.format("send finish process message %s", returnMsg));
        template.convertAndSend(
            MessageQueueConst.EXCHANGE_NAME,
            MessageQueueConst.FINISH_PROCESS_QUEUE,
            returnMsg
        );
        TriggerMsg triggerMsg = new TriggerMsg();
        triggerMsg.setId(photo.getId());
        log.info(String.format("send trigger transfer message %s", triggerMsg));
        template.convertAndSend(
            MessageQueueConst.EXCHANGE_NAME,
            MessageQueueConst.TRIGGER_TRANSFER_QUEUE,
            triggerMsg
        );
    }

    byte[] appendText(byte[] data, String imageType, String timestamp) throws IOException {
        int type = imageType.contains("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        if (type == BufferedImage.TYPE_INT_RGB) {
            log.warn("text can only be added on png file.");
            return data;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(bis);
        BufferedImage imageWithText;
        try {
            imageWithText = new BufferedImage(image.getWidth(), image.getHeight(), type);
        } catch (NullPointerException e) {
            throw new IOException("image is null");
        }

        Graphics2D w = (Graphics2D) imageWithText.getGraphics();
        w.drawImage(image, 0, 0, null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        w.setComposite(alphaChannel);
        w.setColor(Color.GRAY);
        w.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        FontMetrics fontMetrics = w.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(timestamp, w);

        int x = image.getWidth() - ((int) rect.getWidth() * 2);
        int y = image.getHeight() - ((int) rect.getHeight() * 2);

        // NOTE: make sure x and y are bigger than 0
        y = y > 0 ? y : 0;
        x = x > 0 ? x : 0;

        w.drawString(timestamp, x, y);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageWithText, "png", baos);
        w.dispose();
        return baos.toByteArray();
    }

    @Transactional
    private void updatePhoto(Photo photo) {
        photoRepository.save(photo);
    }

}
