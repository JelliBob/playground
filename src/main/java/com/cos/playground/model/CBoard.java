package com.cos.playground.model;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CBoard {
	private int id; // pk
	private String title;
	private String content;
	private Timestamp regdate;
	private String file;
	private int favCount;
	private int viewCount;
	private int commentCount;
	private int userId; // fk
	private String writer;
	private List<Comment> comments;
}
