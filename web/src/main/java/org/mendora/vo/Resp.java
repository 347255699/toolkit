package org.mendora.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resp<T> {
    private int code;

    private T data;

    private String msg;
}
