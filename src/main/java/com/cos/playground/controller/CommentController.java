package com.cos.playground.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.playground.dto.CMRespDto;
import com.cos.playground.model.Comment;
import com.cos.playground.model.User;
import com.cos.playground.service.CBoardService;
import com.cos.playground.service.CommentService;
import com.cos.playground.service.UserService;

@RequestMapping("/comment/*")
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CBoardService cboardService;
	
	// 댓글 쓰기
	// 댓글 작성시 게시글의 commentCount가 1 증가해야함
	@PostMapping("write/{boardId}")
	public CMRespDto<Comment> writeComment(@PathVariable int boardId, @RequestBody Comment comment, 
			HttpServletRequest request) {
		CMRespDto<Comment> cm = new CMRespDto<Comment>(); // 공통 dto 선언
		
		List<Comment> comments = new ArrayList<Comment>(); // 댓글목록을 담을 리스트를 선언

		String cookie = request.getHeader("Cookie").substring(0,15);
		
		if (cookie.equals("user=authorized")) {
			comment.setBoardId(boardId);// 게시판 id를 설정
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			comment.setRegdate(ts); // 댓글 작성시간을 설정
			commentService.write(comment); // 1. 작성한 댓글을 Comment 디비에 저장
			comments = commentService.findByBoardId(boardId); // 2. 댓글 목록을 가져옴
			comments.add(comment); // 3. 작성한 댓글을 리스트에 추가
			cboardService.increaseComment(boardId); // 글의 댓글수를 1 증가시킴 
			cm.setCode(1);
			cm.setMsg("댓글달기 성공");
			cm.setData(comment);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("댓글달기 실패 : 로그인 정보 없음");
			cm.setData(comment);
			return cm;
		}
	}
	
	// 댓글 수정하기
	@PutMapping("update/{cid}")
	public CMRespDto<Comment> updateComment(@PathVariable int cid, @RequestBody Comment comment, 
			HttpServletRequest request){
		CMRespDto<Comment> cm = new CMRespDto<Comment>();
		Comment originComment = commentService.findById(cid); // cid로 수정할 댓글을 가져옴
		
		String cookie = request.getHeader("Cookie").substring(0,15);
		
		// 기존 댓글의 작성자와 안드로이드에서 전송받은 comment의 작성자가 같을때
		if((cookie.equals("user=authorized"))&&(comment.getUserId()==originComment.getUserId())) {
			originComment.setContent(comment.getContent());
			commentService.update(originComment);
			cm.setCode(1);
			cm.setMsg("댓글수정 성공");
			cm.setData(originComment);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("댓글수정 실패 : 댓글작성자가 아님");
			cm.setData(originComment);
			return cm;
		}

	}
	
	// 댓글 삭제하기
	// 안드로이드에서 userID를 받아야할듯
	@DeleteMapping("delete/{cid}")
	public CMRespDto<Comment> deleteComment(@PathVariable int cid, @RequestBody User user,
			HttpServletRequest request){
		CMRespDto<Comment> cm = new CMRespDto<Comment>();
		Comment comment = commentService.findById(cid);
		
		String cookie = request.getHeader("Cookie").substring(0,15);
		
		// 로그인한 유저와 댓글 작성자가 같을때
		if((cookie.equals("user=authorized")&&(user.getId()==comment.getUserId()))) {
			commentService.delete(comment);
			cm.setCode(1);
			cm.setMsg("댓글삭제 성공");
			cm.setData(comment);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("댓글삭제 실패 : : 댓글작성자가 아님");
			cm.setData(comment);
			return cm;
		}
	}
	
	
	
}
