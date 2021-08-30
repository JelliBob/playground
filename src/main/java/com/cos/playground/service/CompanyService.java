package com.cos.playground.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.playground.mapper.CompanyMapper;
import com.cos.playground.model.Company;
import com.cos.playground.model.RBoard;

@Service
@Transactional
public class CompanyService {

	@Autowired
	private CompanyMapper companyMapper;
	
	public Company findByUsername(String username) {
		return companyMapper.findByUsername(username);
	}
	
	public Company findById(int id) {
		return companyMapper.findById(id);
	}
	
	public void join(Company company) {
		companyMapper.join(company);
	}
	
	public void updateInfo(Company company) {
		companyMapper.updateInfo(company);
	}
	
	public void delete(Company company) {
		companyMapper.delete(company);
	}
	
	public List<RBoard> myPostList(int companyId){
		return companyMapper.myPostList(companyId);
	}
}
