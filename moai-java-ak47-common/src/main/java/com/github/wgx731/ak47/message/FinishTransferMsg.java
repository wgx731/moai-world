package com.github.wgx731.ak47.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class FinishTransferMsg implements Serializable {

    private static final long serialVersionUID = -1075538218264955230L;

    private Long id;

    private String storageUrl;

    private String status;

}
