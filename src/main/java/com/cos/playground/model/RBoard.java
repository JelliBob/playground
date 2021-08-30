package com.cos.playground.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RBoard {
	private int id; // pk
	private String title;
	private String content;
	private Timestamp regdate;
	private String file;
	private int companyId;
}
