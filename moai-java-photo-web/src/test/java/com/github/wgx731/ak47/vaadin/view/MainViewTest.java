package com.github.wgx731.ak47.vaadin.view;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.service.StorageService;
import com.github.wgx731.ak47.vaadin.model.PagingParams;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MainViewTest {

    @Mock
    private StorageService service;

    @Mock
    private PhotoEditor editor;

    @Mock
    private Grid<Photo> photoGrid;

    @Mock
    private HorizontalLayout footerLayout;

    @Mock
    private Page<Photo> data;

    private MainView testCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(data.stream()).thenReturn(Stream.empty());
        Mockito.when(service.listAllPhotosByPage(Mockito.any())).thenReturn(
            data
        );
        testCase = new MainView(
            service,
            editor,
            photoGrid,
            footerLayout
        );
    }

    @AfterEach
    public void tearDown() {
        service = null;
        editor = null;
        photoGrid = null;
        footerLayout = null;
        testCase = null;
    }

    @Test
    @DisplayName("default paging params")
    public void testDefaultPagingParams() {
        PagingParams params = testCase.getPagingParams();
        assertThat(params.getPageNum()).isEqualTo(MainView.DEFAULT_PAGE_NUM);
        assertThat(params.getPageSize()).isEqualTo(MainView.DEFAULT_PAGE_SIZE);
        assertThat(params.getSortByKey()).isEqualTo(MainView.DEFAULT_SORT_KEY);
    }

    @Test
    @DisplayName("customize paging params")
    public void testCustomizePagingParams() {
        final int pageNum = 10;
        final int pageSize = 5;
        final String sortKey = "project";
        BeforeEvent event = Mockito.mock(BeforeEvent.class);
        Location location = Mockito.mock(Location.class);
        Mockito.when(event.getLocation()).thenReturn(
            location
        );
        QueryParameters queryParameters = Mockito.mock(QueryParameters.class);
        Mockito.when(location.getQueryParameters()).thenReturn(
            queryParameters
        );
        Map<String, List<String>> map = new HashMap<>();
        map.put(MainView.PAGE_PARAM_NAME, List.of(String.valueOf(pageNum)));
        map.put(MainView.SIZE_PARAM_NAME, List.of(String.valueOf(pageSize)));
        map.put(MainView.SORT_PARAM_NAME, List.of(sortKey));
        Mockito.when(queryParameters.getParameters()).thenReturn(
            map
        );
        testCase.setParameter(
            event,
            ""
        );
        PagingParams params = testCase.getPagingParams();
        assertThat(params.getPageNum()).isEqualTo(pageNum);
        assertThat(params.getPageSize()).isEqualTo(pageSize);
        assertThat(params.getSortByKey()).isEqualTo(sortKey);
    }

    @Test
    @DisplayName("query parameters")
    public void testQueryParameters() {
        QueryParameters parameters = testCase.getQueryParameters(-1);
        assertThat(parameters.getQueryString()).contains("page=-1");
        parameters = testCase.getQueryParameters(1);
        assertThat(parameters.getQueryString()).contains("page=1");
    }

}