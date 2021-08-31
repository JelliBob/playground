package com.cos.playground.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BoardWriteDto {

	private String category;
    private String title;
    private String content;
    private int userId;
    
	public BoardWriteDto() {
	}

	public BoardWriteDto(@JsonProperty("category") String category,
			@JsonProperty("title") String title, 
			@JsonProperty("content") String content,
			@JsonProperty("userId") int userId) {
		super();
		this.title = title;
		this.content = content;
		this.category = category;
		this.userId = userId;
	}

   
}
