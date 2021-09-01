package com.cos.playground.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cos.playground.model.CBoard;

@Mapper
public interface CBoardMapper {
	CBoard findById(int id); // 게시글 한 건 보기
	void increaseView(int id); // 조회수 증가
	List<CBoard> findAll(); // 게시글 목록보기
	void write(CBoard board); // 게시글 쓰기
	void update(CBoard board); // 게시물 수정하기
	void delete(int id);
	void increaseComment(int id); // 댓글수 1 증가
	void decreaseComment(int id); // 댓글수 1 감소
	void increaseFav(int id); // 좋아요 수 1 증가
	void decreaseFav(int id); // 좋아요 수 1 감소
	List<CBoard> topPost(); // 인기 게시글 보기
}
