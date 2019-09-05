package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.service.StorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@Route
@Slf4j
public class MainView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    public static final String SORT_PARAM_NAME = "sort";

    private final StorageService service;
    private int pageNum = 0;
    private int size = 20;
    private String sort = "id";

    private Grid<Photo> photoGrid;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        if (parametersMap.containsKey(PAGE_PARAM_NAME)) {
            try {
                pageNum = Integer.valueOf(parametersMap.get(PAGE_PARAM_NAME).get(0));
            } catch (NumberFormatException e) {
                log.warn(String.format("%s value invalid", PAGE_PARAM_NAME), e);
            }
        }
        if (parametersMap.containsKey(SIZE_PARAM_NAME)) {
            try {
                size = Integer.valueOf(parametersMap.get(SIZE_PARAM_NAME).get(0));
            } catch (NumberFormatException e) {
                log.warn(String.format("%s value invalid", SIZE_PARAM_NAME), e);
            }
        }
        if (parametersMap.containsKey(SORT_PARAM_NAME)) {
            String sortKey = parametersMap.get(SORT_PARAM_NAME).get(0);
            if (Photo.containsSortKey(sortKey)) {
                sort = sortKey;
            }
        }
        PageRequest request = PageRequest.of(pageNum, size, Sort.by(sort));
        this.photoGrid.setItems(this.service.listAllPhotosByPage(
            request
        ).stream());
        this.photoGrid.addColumn(p -> p.getProject().getName()).setHeader("Project Name");
        this.photoGrid.addColumn(p -> p.getImageType()).setHeader("Image Type");
        this.photoGrid.addColumn(p -> p.getStatus()).setHeader("Image Status");
        this.photoGrid.addColumn(p -> p.getUploader()).setHeader("Uploader");
        this.photoGrid.addColumn(new ComponentRenderer<>(p -> new Image(
            new StreamResource(String.format("photo %d", p.getId()), () -> new ByteArrayInputStream(p.getData())),
            "preview photo"
        ))).setHeader("Preview");
        this.photoGrid.setMinHeight("600px");
    }

    public MainView(StorageService service) {
        this.service = service;
        this.photoGrid = new Grid<>();
        this.add(photoGrid);
    }

}

