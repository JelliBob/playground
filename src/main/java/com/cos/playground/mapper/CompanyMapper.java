package com.cos.playground.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cos.playground.model.Company;
import com.cos.playground.model.RBoard;

@Mapper
public interface CompanyMapper {
	Company findByUsername(String username); // 기업회원 한건보기
	Company findById(int id);
	void join(Company company); // 회원 가입하기
	void updateInfo(Company company); // 회원 정보 수정하기
	void delete(Company company); // 회원 탈퇴
	List<RBoard> myPostList(int companyId); // 작성글 목록 보기
	
}
