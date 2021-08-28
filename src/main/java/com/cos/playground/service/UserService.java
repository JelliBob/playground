package com.cos.playground.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.playground.mapper.UserMapper;
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
	
}
