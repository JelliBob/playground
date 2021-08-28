package com.cos.playground.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardWriteDto {
    private String title;
    private String content;
    private String category;
    private int userId;
}
