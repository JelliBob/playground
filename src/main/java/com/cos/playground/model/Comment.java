package com.cos.playground.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Comment {
	private int cid; 
	private int userId;
	private int boardId;
	private String content;
	private Timestamp regdate;
	private int upId; // 상위 댓글의 id
}
