package com.cos.playground.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.Fav;
import com.cos.playground.model.User;

@Mapper
public interface UserMapper {
	User findByUsername(String username); // 회원 한건보기
	User findById(int id);
	void join(User user); // 회원 가입하기
	void updateInfo(User user); // 회원 정보 수정하기
	void delete(User user); // 회원 탈퇴
	void likePost(int param1, int param2); // 게시글 좋아요 누르기
	void cancleFav(int param1, int param2); // 게시글 좋아요 취소하기
	Fav favHistory(int param1, int param2); // 해당글 좋아요 내역이 있는지 확인
	List<CBoard> favList(int userId); // 좋아요 누른 게시글 목록 조회하기
	List<CBoard> myPostList(int userId); // 작성한 글 목록 보기
	List<Comment> myCommentList(int userId); // 작성한 댓글 목록 보기
}
