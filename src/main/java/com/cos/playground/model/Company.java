package com.cos.playground.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
	private int id;
	private String username;
	private String password;
	private String name; // 회사 이름
	private String email;
	private String tel;
	private String info;
}
