package com.cos.playground.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cos.playground.entity.UploadFile;

public interface FileDAO extends CrudRepository<UploadFile, Integer>{
	
	@Query(value="select file_name from upload_file where id = ?1", nativeQuery = true)
	public String findByFileId(int id);
}

