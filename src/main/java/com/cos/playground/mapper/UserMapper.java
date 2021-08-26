package com.cos.playground.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.cos.playground.model.User;

@Mapper
public interface UserMapper {
	User findByUsername(String username); // 회원 한건보기
	void join(User user); // 회원 가입하기
	void updateInfo(User user); // 회원 정보 수정하기
	void delete(User user); // 회원 탈퇴
}
