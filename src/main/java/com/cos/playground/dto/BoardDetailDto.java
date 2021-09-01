package com.cos.playground.dto;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.core.io.Resource;

import com.cos.playground.model.Comment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailDto {
	private int id; // pk
	private String title;
	private String content;
	private Timestamp regdate;
	private int favCount;
	private int viewCount;
	private int commentCount;
	private int userId; // fk
	private String writer;
	private String category;
	private List<Comment> comments;
	private String usermail;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Resource resource; // 파일
}
