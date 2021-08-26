package com.cos.playground.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.playground.mapper.CommentMapper;
import com.cos.playground.model.Comment;

@Service
@Transactional
public class CommentService {
	
	@Autowired
	private CommentMapper commentMapper;
	
	public List<Comment> findByBoardId(int boardId){
		return commentMapper.findByBoardId(boardId);
	}
	
	public Comment findById(int cid) {
		return commentMapper.findById(cid);
	}
	
	public void write(Comment comment) {
		commentMapper.write(comment);
	}
	
	public void update(Comment commnet) {
		commentMapper.update(commnet);
	}
	
	public void delete(Comment comment) {
		commentMapper.delete(comment);
	}
	
}
