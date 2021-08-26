package com.cos.playground.model;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class User {
	private int id; //pk
	private String username;
	private String password;
	private String name;
	private String email;
	private String phone;
	private String career;
	private Timestamp regdate;
}
