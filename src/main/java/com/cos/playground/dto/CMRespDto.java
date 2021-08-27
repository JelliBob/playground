package com.cos.playground.dto;

import lombok.Data;

@Data
public class CMRespDto<T> {
    private int code;
    private String msg;
    private T data;
}
