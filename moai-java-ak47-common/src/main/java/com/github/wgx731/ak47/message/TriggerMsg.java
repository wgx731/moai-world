package com.github.wgx731.ak47.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class TriggerMsg implements Serializable {

    private static final long serialVersionUID = 437504489130908158L;

    private Long id;

}
