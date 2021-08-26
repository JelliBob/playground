package com.cos.playground.dto;

import com.cos.playground.model.Comment;

import lombok.Data;

@Data
public class CMRespDto<T> {
    private int code;
    private String msg;
    private T data;
}
