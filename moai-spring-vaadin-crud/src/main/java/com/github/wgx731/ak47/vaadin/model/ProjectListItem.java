package com.github.wgx731.ak47.vaadin.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ProjectListItem {

    private String name;

    private List<byte[]> photos;

}
