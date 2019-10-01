package com.github.wgx731.ak47.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "project")
public class Project extends Auditable<String> {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(
        name = "name",
        unique = true
    )
    @NonNull
    private String name;

}
