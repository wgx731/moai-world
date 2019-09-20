package com.github.wgx731.ak47.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Arrays;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "photo")
public class Photo extends Auditable<String> {

    public enum ProcessStatus {
        UPLOADED, RUNNING, PROCESSED, FAILED, TRANSFERRED;
    }

    public static final String[] sortKeys = {
        "id", "project", "status", "imageType", "uploader"
    };

    public static boolean containsSortKey(String key) {
        return Arrays.asList(sortKeys).contains(key);
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @NonNull
    private Project project;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    @NonNull
    private ProcessStatus status;

    @Lob
    @Column(name = "data")
    @NonNull
    private byte[] data;

    @Column(name = "image_type")
    @NonNull
    private String imageType;

    @Column(name = "uploader")
    @NonNull
    private String uploader;

    @Column(name = "server_id")
    @NonNull
    private String severId;

    @Column(name = "storage_url")
    @NonNull
    private String storageUrl;


}
