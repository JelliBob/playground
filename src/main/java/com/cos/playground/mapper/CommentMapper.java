package com.cos.playground.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cos.playground.model.Comment;

@Mapper
public interface CommentMapper {
	List<Comment> findByBoardId(int boardId);
	Comment findById(int cid);
	void write(Comment comment);
	void update(Comment comment);
	void delete(Comment comment);
}
