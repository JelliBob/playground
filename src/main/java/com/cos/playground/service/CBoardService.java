package com.cos.playground.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.playground.mapper.CBoardMapper;
import com.cos.playground.model.CBoard;

@Service
@Transactional
public class CBoardService {
	
	@Autowired
	private CBoardMapper boardMapper;
	
	public CBoard findById(int id) {
		return boardMapper.findById(id);
	}
	
	public void increaseView(int id) {
		boardMapper.increaseView(id); // 조회수 1 증가
	}

	public List<CBoard> findAll(){
		return boardMapper.findAll();
	}
	
	public void write(CBoard board) {
		boardMapper.write(board);
	}
	
	public void update(CBoard board) {
		boardMapper.update(board);
	}
	
	public void delete(int id) {
		boardMapper.delete(id);
	}
	
	public void increaseComment(int id) {
		boardMapper.increaseComment(id);
	}
}
