package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.security.SecurityUtils;
import com.github.wgx731.ak47.service.StorageService;
import com.github.wgx731.ak47.vaadin.model.PagingParams;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Route
@Slf4j
@PWA(
    name = "spring vaadin photo app",
    shortName = "SVPA",
    description = "A demo photo upload app build with spring and vaadin"
)
public class MainView extends VerticalLayout implements HasUrlParameter<String> {

    private static final long serialVersionUID = -2519398887123033381L;

    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    public static final String SORT_PARAM_NAME = "sort";
    public static final int DEFAULT_PAGE_NUM = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_KEY = "id";

    transient StorageService service;
    transient SecurityUtils securityUtils;
    Map<String, List<String>> parametersMap;
    Page<Photo> data;

    VerticalLayout headerLayout;
    Grid<Photo> photoGrid;
    PhotoEditor editor;
    Button newPhotoBtn;
    Button refreshBtn;
    Button logOutBtn;
    HorizontalLayout footerLayout;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        this.parametersMap = event.getLocation().getQueryParameters().getParameters();
        refreshData();
        updateComponents();
    }

    PagingParams getPagingParams() {
        int pageNum = DEFAULT_PAGE_NUM, size = DEFAULT_PAGE_SIZE;
        String sortBy = DEFAULT_SORT_KEY;
        if (this.parametersMap.containsKey(PAGE_PARAM_NAME)) {
            try {
                pageNum = Integer.parseInt(
                    this.parametersMap.get(PAGE_PARAM_NAME).get(0))
                ;
            } catch (NumberFormatException e) {
                log.warn(String.format("%s value invalid", PAGE_PARAM_NAME), e);
            }
        }
        if (this.parametersMap.containsKey(SIZE_PARAM_NAME)) {
            try {
                size = Integer.parseInt(
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

    QueryParameters getQueryParameters(int pageDiff) {
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

    void updateComponents() {
        this.photoGrid.setItems(this.data.stream());
        this.photoGrid.recalculateColumnWidths();

        this.footerLayout.removeAll();
        PagingParams params = this.getPagingParams();
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

    void refreshData() {
        PagingParams params = this.getPagingParams();
        this.data = this.service.listUserPhotosByPage(
            this.securityUtils.getCurrentUserString(),
            PageRequest.of(
                params.getPageNum(),
                params.getPageSize(),
                Sort.by(params.getSortByKey())
            )
        );
    }

    @Autowired
    public MainView(
        StorageService service,
        PhotoEditor editor,
        SecurityUtils securityUtils
    ) {
        this.service = service;
        this.securityUtils = securityUtils;
        this.editor = editor;

        this.editor.setChangeHandler(() -> {
            this.editor.setVisible(false);
            this.refreshData();
            this.updateComponents();
        });

        this.photoGrid = new Grid<>();
        this.photoGrid.asSingleSelect().addValueChangeListener(e -> {
            if (Objects.isNull(e.getValue())) {
                this.newPhotoBtn.setEnabled(true);
            } else {
                this.newPhotoBtn.setEnabled(false);
            }
            this.editor.editPhoto(e.getValue());
        });
        this.photoGrid.addColumn(p -> p.getProject().getName()).setHeader("Project Name");
        this.photoGrid.addColumn(p -> p.getImageType()).setHeader("Image Type");
        this.photoGrid.addColumn(p -> p.getStatus()).setHeader("Image Status");
        this.photoGrid.addColumn(p -> p.getUploader()).setHeader("Uploader");
        this.photoGrid.addColumn(new ComponentRenderer<>(p -> new Image(
            new StreamResource(String.format("photo %d", p.getId()), () -> new ByteArrayInputStream(p.getData())),
            "preview photo"
        ))).setHeader("Preview");

        this.newPhotoBtn = new Button("New photo", VaadinIcon.PLUS.create());
        this.newPhotoBtn.addClickListener(e -> editor.editPhoto(new Photo()));

        this.refreshBtn = new Button("Refresh", VaadinIcon.REFRESH.create());
        this.refreshBtn.addClickListener(e -> {
            this.refreshData();
            this.updateComponents();
        });

        this.logOutBtn = new Button("Log out", VaadinIcon.SIGN_OUT.create());
        this.logOutBtn.addClickListener(e ->
            this.logOutBtn.getUI().ifPresent(ui ->
                ui.getPage().setLocation("/logout")
            )
        );

        this.headerLayout = new VerticalLayout(new HorizontalLayout(
            this.newPhotoBtn,
            this.refreshBtn,
            this.logOutBtn
        ), this.editor);

        this.footerLayout = new HorizontalLayout();
        this.add(this.headerLayout, this.photoGrid, this.footerLayout);
    }

    MainView(
        StorageService service,
        SecurityUtils securityUtils,
        PhotoEditor editor,
        Grid<Photo> grid,
        HorizontalLayout footerLayout
    ) {
        this.service = service;
        this.securityUtils = securityUtils;
        this.editor = editor;
        this.photoGrid = grid;
        this.footerLayout = footerLayout;
        this.parametersMap = new HashMap<>();
    }

}

