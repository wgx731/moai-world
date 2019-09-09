package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.service.StorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.upload.Upload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class PhotoEditorTest {

    @Mock
    private StorageService service;
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
            service,
            changeHandler,
            comboBox,
            image,
            close,
            delete
        );
    }

    @AfterEach
    void tearDown() {
        service = null;
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
        Mockito.verify(service, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(changeHandler, Mockito.times(1)).onChange();
    }

    @Test
    @DisplayName("save photo")
    public void savePhotoTest() {
        testCase.save();
        Mockito.verify(service, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(changeHandler, Mockito.times(1)).onChange();
    }

    @Test
    @DisplayName("edit photo when photo is null")
    public void photoNullNotShownTest() {
        testCase.editPhoto(null);
        assertThat(testCase.isVisible()).isFalse();
    }

}