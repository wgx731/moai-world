package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.security.SecurityUtils;
import com.github.wgx731.ak47.service.StorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.dom.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PhotoEditorTest {

    @Mock
    private StorageService storageService;
    @Mock
    private MessageService messageService;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private PhotoEditor.ChangeHandler changeHandler;
    @Mock
    private ComboBox<Project> comboBox;
    @Mock
    private Upload image;
    @Mock
    private Button close;
    @Mock
    private Button delete;
    @Mock
    private PhotoEditor testCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        testCase = new PhotoEditor(
            storageService,
            messageService,
            securityUtils,
            changeHandler,
            comboBox,
            image,
            close,
            delete
        );
    }

    @AfterEach
    void tearDown() {
        storageService = null;
        messageService = null;
        changeHandler = null;
        comboBox = null;
        image = null;
        close = null;
        delete = null;
        testCase = null;
    }

    @Test
    @DisplayName("delete photo")
    public void deletePhotoTest() {
        testCase.delete();
        Mockito.verify(storageService, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(changeHandler, Mockito.times(1)).onChange();
    }

    @Test
    @DisplayName("save photo")
    public void savePhotoTest() {
        Mockito.when(securityUtils.getCurrentUser()).thenReturn("tester");
        testCase.save();
        Mockito.verify(storageService, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(messageService, Mockito.times(1)).sendMessage(Mockito.any());
        Mockito.verify(securityUtils, Mockito.times(1)).getCurrentUser();
        Mockito.verify(changeHandler, Mockito.times(1)).onChange();
    }

    @Test
    @DisplayName("edit photo when photo is null")
    public void photoNullNotShownTest() {
        testCase.editPhoto(null);
        assertThat(testCase.isVisible()).isFalse();
    }

    @Test
    @DisplayName("edit photo when photo not saved")
    public void photoNotSavedTest() {
        Mockito.when(image.getElement()).thenReturn(Mockito.mock(Element.class));
        testCase.editPhoto(new Photo());
        assertThat(testCase.isVisible()).isTrue();
        Mockito.verify(storageService, Mockito.times(0)).getPhotoById(Mockito.anyLong());
        Mockito.verify(storageService, Mockito.times(1)).listAllProjects();
        Mockito.verify(close, Mockito.times(1)).setVisible(true);
        Mockito.verify(delete, Mockito.times(1)).setVisible(false);
    }

    @Test
    @DisplayName("edit photo when photo saved")
    public void photoSavedTest() {
        final Long id = 100L;
        Mockito.when(image.getElement()).thenReturn(Mockito.mock(Element.class));
        Photo photo = new Photo();
        Mockito.when(storageService.getPhotoById(id)).thenReturn(Optional.of(photo));
        photo.setId(id);
        testCase.editPhoto(photo);
        assertThat(testCase.isVisible()).isTrue();
        Mockito.verify(storageService, Mockito.times(1)).getPhotoById(id);
        Mockito.verify(storageService, Mockito.times(1)).listAllProjects();
        Mockito.verify(close, Mockito.times(1)).setVisible(false);
        Mockito.verify(delete, Mockito.times(1)).setVisible(true);
    }

}