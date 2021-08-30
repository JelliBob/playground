package com.cos.playground.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.playground.mapper.UserMapper;
import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.Fav;
import com.cos.playground.model.User;

@Service
public class UserService{

	@Autowired
	private UserMapper userMapper;

	public User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}
	
	public User findById(int id) {
		return userMapper.findById(id);
	}
	
	public void join(User user) {
		userMapper.join(user);
	}
	
	public void updateInfo(User user) {
		userMapper.updateInfo(user);
	}
	
	public void delete(User user) {
		userMapper.delete(user);
	}
	
	public void likePost(int userId, int boardId) {
		userMapper.likePost(userId, boardId);
	}
	
	public void cancleFav(int userId, int boardId) {
		userMapper.cancleFav(userId, boardId);
	}
	
	public Fav favHistory(int userId, int boardId) {
		return userMapper.favHistory(userId, boardId);
	}
	
	public List<CBoard> favList(int userId){
		return userMapper.favList(userId);
	}
	
	public List<CBoard> myPostList(int userId){
		return userMapper.myPostList(userId);
	}
	
	public List<Comment> myCommentList(int userId){
		return userMapper.myCommentList(userId);
	}
	
}
