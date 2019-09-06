package com.github.wgx731.ak47.vaadin.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Builder
@Getter
public class PagingParams {

    private int pageNum;

    private int pageSize;

    private String sortByKey;

}
