package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.service.StorageService;
import com.github.wgx731.ak47.vaadin.model.PagingParams;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route
@Slf4j
public class MainView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    public static final String SORT_PARAM_NAME = "sort";

    private StorageService service;
    private Map<String, List<String>> parametersMap;

    private VerticalLayout headerLayout;
    private Grid<Photo> photoGrid;
    private PhotoEditor editor;
    private Button newPhotoBtn;
    private HorizontalLayout footerLayout;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        this.parametersMap = event.getLocation().getQueryParameters().getParameters();
        refreshData();
        updateComponents();
    }

    private PagingParams getPagingParams() {
        int pageNum = 0, size = 20;
        String sortBy = "id";
        if (this.parametersMap.containsKey(PAGE_PARAM_NAME)) {
            try {
                pageNum = Integer.valueOf(
                    this.parametersMap.get(PAGE_PARAM_NAME).get(0))
                ;
            } catch (NumberFormatException e) {
                log.warn(String.format("%s value invalid", PAGE_PARAM_NAME), e);
            }
        }
        if (this.parametersMap.containsKey(SIZE_PARAM_NAME)) {
            try {
                size = Integer.valueOf(
                    this.parametersMap.get(SIZE_PARAM_NAME).get(0))
                ;
            } catch (NumberFormatException e) {
                log.warn(String.format("%s value invalid", SIZE_PARAM_NAME), e);
            }
        }
        if (this.parametersMap.containsKey(SORT_PARAM_NAME)) {
            String sortKey = parametersMap.get(SORT_PARAM_NAME).get(0);
            if (Photo.containsSortKey(sortKey)) {
                sortBy = sortKey;
            }
        }
        return PagingParams
            .builder()
            .pageNum(pageNum)
            .pageSize(size)
            .sortByKey(sortBy)
            .build();
    }

    private QueryParameters getQueryParameters(int pageDiff) {
        PagingParams params = this.getPagingParams();
        Map<String, List<String>> map = new HashMap<>();
        String[] sizes = {String.format("%d", params.getPageSize())};
        String[] orderings = {params.getSortByKey()};
        String[] pages = {String.format("%d", params.getPageNum() + pageDiff)};
        map.put(PAGE_PARAM_NAME, Arrays.asList(pages));
        map.put(SIZE_PARAM_NAME, Arrays.asList(sizes));
        map.put(SORT_PARAM_NAME, Arrays.asList(orderings));
        return new QueryParameters(map);

    }

    private void updateComponents() {
        this.removeAll();
        this.add(new H1("Photo List Page"));
        this.add(this.headerLayout);
        this.add(this.photoGrid);
        this.add(this.footerLayout);
    }

    private void refreshData() {
        this.headerLayout.removeAll();
        this.newPhotoBtn = new Button("New photo", VaadinIcon.PLUS.create());
        this.headerLayout.add(newPhotoBtn);
        this.headerLayout.add(this.editor);
        newPhotoBtn.addClickListener(e -> editor.editPhoto(new Photo()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            PagingParams params = this.getPagingParams();
            Page<Photo> data = this.service.listAllPhotosByPage(
                PageRequest.of(
                    params.getPageNum(),
                    params.getPageSize(),
                    Sort.by(params.getSortByKey())
                )
            );
            this.photoGrid.setItems(data.stream());
        });
        PagingParams params = this.getPagingParams();
        Page<Photo> data = this.service.listAllPhotosByPage(
            PageRequest.of(
                params.getPageNum(),
                params.getPageSize(),
                Sort.by(params.getSortByKey())
            )
        );
        this.photoGrid = new Grid<>();
        this.photoGrid.asSingleSelect().addValueChangeListener(e -> {
            editor.editPhoto(e.getValue());
        });
        this.photoGrid.setItems(data.stream());
        this.photoGrid.addColumn(p -> p.getProject().getName()).setHeader("Project Name");
        this.photoGrid.addColumn(p -> p.getImageType()).setHeader("Image Type");
        this.photoGrid.addColumn(p -> p.getStatus()).setHeader("Image Status");
        this.photoGrid.addColumn(p -> p.getUploader()).setHeader("Uploader");
        this.photoGrid.addColumn(new ComponentRenderer<>(p -> new Image(
            new StreamResource(String.format("photo %d", p.getId()), () -> new ByteArrayInputStream(p.getData())),
            "preview photo"
        ))).setHeader("Preview");
        this.photoGrid.setMinHeight("600px");
        this.footerLayout.removeAll();
        if (data.hasPrevious()) {
            NativeButton button = new NativeButton(
                "previous");
            button.addClickListener(e ->
                button.getUI().ifPresent(ui ->
                    ui.navigate("", this.getQueryParameters(-1)))
            );
            footerLayout.add(button);
        }
        footerLayout.add(new Paragraph(new StringBuilder()
            .append(" current page ")
            .append(params.getPageNum())
            .append(" |  page size ")
            .append(params.getPageSize())
            .append(" | total page ")
            .append(data.getTotalPages())
            .append(" | total items ")
            .append(data.getTotalElements())
            .toString())
        );
        if (data.hasNext()) {
            NativeButton button = new NativeButton(
                "next");
            button.addClickListener(e ->
                button.getUI().ifPresent(ui ->
                    ui.navigate("", this.getQueryParameters(1)))
            );
            footerLayout.add(button);
        }
    }

    public MainView(StorageService service, PhotoEditor editor) {
        this.service = service;
        this.editor = editor;
        this.headerLayout = new VerticalLayout();
        this.footerLayout = new HorizontalLayout();
    }

}

