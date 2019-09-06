package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.service.StorageService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@SpringComponent
@UIScope
@Slf4j
public class PhotoEditor extends VerticalLayout implements KeyNotifier {

    public interface ChangeHandler {
        void onChange();
    }

    private final StorageService service;

    private Photo photo;
    private MemoryBuffer buffer = new MemoryBuffer();

    ComboBox<Project> projectComboBox = new ComboBox<>("Project");
    Upload image = new Upload(buffer);

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, delete);

    private ChangeHandler changeHandler;

    @Autowired
    public PhotoEditor(StorageService service) {
        this.service = service;

        projectComboBox.setItemLabelGenerator(Project::getName);
        projectComboBox.setItems(this.service.listAllProjects());

        projectComboBox.addValueChangeListener(event -> {
            if (!event.getSource().isEmpty()) {
                this.photo.setProject(event.getValue());
            }
        });

        image.setAcceptedFileTypes(
            "image/png", "image/jpeg", ".png", ".jpg"
        );
        image.addFailedListener(event -> {
            Notification notification = new Notification(
                "Failed to receive upload image.", 3000);
            notification.open();
        });
        image.addSucceededListener(event -> {
            this.photo.setImageType(event.getMIMEType());
            try {
                this.photo.setData(buffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                log.error("error reading upload image.", e);
            }
        });

        add(projectComboBox, image, actions);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        setVisible(false);
    }

    void delete() {
        this.service.delete(photo);
        changeHandler.onChange();
    }

    void save() {
        photo.setStatus(Photo.ProcessStatus.UPLOADED);
        // TODO: change to current log in user
        photo.setUploader("wgx");
        this.service.save(photo);
        changeHandler.onChange();
    }

    public final void editPhoto(Photo photo) {
        if (photo == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = photo.getId() != null;
        if (persisted) {
            this.photo = this.service.getPhotoById(photo.getId()).get();
        } else {
            this.photo = photo;
        }

        projectComboBox.setValue(this.photo.getProject());
        setVisible(true);
        projectComboBox.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

}
