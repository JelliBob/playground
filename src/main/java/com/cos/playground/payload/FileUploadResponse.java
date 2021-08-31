package com.cos.playground.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String fileName; // 파일명
    private String fileDownloadUri; // 다운로드 uri
    private String fileType; // mime-type
    private long size; // 파일 크기
}
