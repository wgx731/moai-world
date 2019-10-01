package com.github.wgx731.ak47.service;

import com.github.wgx731.ak47.message.FinishProcessMsg;
import com.github.wgx731.ak47.message.MessageQueueConst;
import com.github.wgx731.ak47.message.TriggerMsg;
import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.repository.PhotoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MessageServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private PhotoRepository photoRepository;

    private MessageService testCase;

    private byte[] data;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        photoRepository = Mockito.mock(PhotoRepository.class);
        testCase = new MessageService(
            rabbitTemplate,
            photoRepository
        );
        BufferedImage originalImage = ImageIO.read(this.getClass()
            .getClassLoader()
            .getResourceAsStream("icon.png")
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "png", baos);
        baos.flush();
        data = baos.toByteArray();
        baos.close();
    }

    @AfterEach
    void tearDown() {
        rabbitTemplate = null;
        photoRepository = null;
        testCase = null;
        data = null;
    }

    @Test
    void receiveMessageIdWrong() {
        Mockito.when(photoRepository.findById(1L)).thenReturn(Optional.empty());
        TriggerMsg msg = new TriggerMsg();
        msg.setId(1L);
        testCase.receiveMessage(msg);
        Mockito.verify(photoRepository, Mockito.times(0)).save(
            Mockito.any(Photo.class)
        );
        Mockito.verify(rabbitTemplate, Mockito.times(0)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.FINISH_PROCESS_QUEUE),
            Mockito.any(FinishProcessMsg.class)
        );
        Mockito.verify(rabbitTemplate, Mockito.times(0)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.TRIGGER_TRANSFER_QUEUE),
            Mockito.any(TriggerMsg.class)
        );
    }

    @Test
    void receiveMessageDataNull() {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setUploader("tester");
        Mockito.when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        TriggerMsg msg = new TriggerMsg();
        msg.setId(1L);
        testCase.receiveMessage(msg);
        Mockito.verify(photoRepository, Mockito.times(1)).save(
            Mockito.eq(photo)
        );
        FinishProcessMsg returnMsg = new FinishProcessMsg();
        returnMsg.setId(1L);
        returnMsg.setUploader(photo.getUploader());
        returnMsg.setStatus(Photo.ProcessStatus.PROCESS_FAILED.toString());
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.FINISH_PROCESS_QUEUE),
            Mockito.eq(returnMsg)
        );
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.TRIGGER_TRANSFER_QUEUE),
            Mockito.eq(msg)
        );
    }

    @Test
    void receiveMessageEmptyImage() {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setData(new byte[] {});
        photo.setImageType("png");
        Mockito.when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        TriggerMsg msg = new TriggerMsg();
        msg.setId(1L);
        testCase.receiveMessage(msg);
        Mockito.verify(photoRepository, Mockito.times(2)).save(
            Mockito.eq(photo)
        );
        FinishProcessMsg returnMsg = new FinishProcessMsg();
        returnMsg.setId(1L);
        returnMsg.setUploader(photo.getUploader());
        returnMsg.setStatus(Photo.ProcessStatus.PROCESS_FAILED.toString());
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.FINISH_PROCESS_QUEUE),
            Mockito.eq(returnMsg)
        );
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.TRIGGER_TRANSFER_QUEUE),
            Mockito.eq(msg)
        );
    }

    @Test
    void receiveMessageNormal() {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setData(data);
        photo.setImageType("png");
        Mockito.when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        TriggerMsg msg = new TriggerMsg();
        msg.setId(1L);
        testCase.receiveMessage(msg);
        Mockito.verify(photoRepository, Mockito.times(2)).save(
            Mockito.eq(photo)
        );
        FinishProcessMsg returnMsg = new FinishProcessMsg();
        returnMsg.setId(1L);
        returnMsg.setUploader(photo.getUploader());
        returnMsg.setStatus(Photo.ProcessStatus.PROCESSED.toString());
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.FINISH_PROCESS_QUEUE),
            Mockito.eq(returnMsg)
        );
        Mockito.verify(rabbitTemplate, Mockito.times(1)).convertAndSend(
            Mockito.eq(MessageQueueConst.EXCHANGE_NAME),
            Mockito.eq(MessageQueueConst.TRIGGER_TRANSFER_QUEUE),
            Mockito.eq(msg)
        );
    }

    @Test
    void appendTextPng() throws IOException {
        byte[] processed = testCase.appendText(data, "png", "test");
        assertThat(processed).isNotEqualTo(data);
    }

    @Test
    void appendTextJpg() throws IOException {
        byte[] processed = testCase.appendText(data, "jpg", "test");
        assertThat(processed).isEqualTo(data);
    }
}