package com.github.wgx731.ak47.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class FinishProcessMsg implements Serializable {

    private static final long serialVersionUID = 3818132362342856577L;

    private Long id;

    private String uploader;

}
