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
import elemental.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@SpringComponent
@UIScope
@Slf4j
public class PhotoEditor extends VerticalLayout implements KeyNotifier {

    private static final long serialVersionUID = 6716807453282399652L;

    public interface ChangeHandler {
        void onChange();
    }

    private transient ChangeHandler changeHandler;
    private transient StorageService service;
    private transient Photo photo;
    private MemoryBuffer buffer;

    // UI
    private ComboBox<Project> projectComboBox;
    private Upload image;
    private Button save;
    private Button delete;
    private Button close;
    private HorizontalLayout actions;

    @Autowired
    public PhotoEditor(StorageService service) {
        this.service = service;
        this.buffer = new MemoryBuffer();
        this.image = new Upload(buffer);
        this.save = new Button("Save", VaadinIcon.CHECK.create());
        this.delete = new Button("Delete", VaadinIcon.TRASH.create());
        this.close = new Button("Close", VaadinIcon.CLOSE.create());
        this.actions = new HorizontalLayout(save, delete, close);
        this.projectComboBox = new ComboBox<>("Project");

        projectComboBox.setItemLabelGenerator(Project::getName);
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

        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");
        close.getElement().getThemeList().add("secondary");
        addKeyPressListener(Key.ENTER, e -> save());
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        close.addClickListener(e -> setVisible(false));

        add(projectComboBox, image, actions);

        setVisible(false);
    }

    PhotoEditor(
        StorageService service,
        ChangeHandler handler,
        ComboBox<Project> comboBox,
        Upload image,
        Button close,
        Button delete
    ) {
        this.service = service;
        this.changeHandler = handler;
        this.projectComboBox = comboBox;
        this.image = image;
        this.close = close;
        this.delete = delete;
        this.photo = new Photo();
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
        projectComboBox.setItems(this.service.listAllProjects());
        image.getElement().setPropertyJson("files", Json.createArray());
        final boolean persisted = photo.getId() != null;
        if (persisted) {
            this.photo = this.service.getPhotoById(photo.getId()).get();
            projectComboBox.setValue(this.photo.getProject());
            this.close.setVisible(false);
            this.delete.setVisible(true);
        } else {
            this.photo = photo;
            this.close.setVisible(true);
            this.delete.setVisible(false);
        }
        setVisible(true);
        projectComboBox.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

}