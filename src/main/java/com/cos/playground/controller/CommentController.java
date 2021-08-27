package com.cos.playground.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.cos.playground.service.CommentService;

@RequestMapping("/comment/*")
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	// 댓글 쓰기
	@PostMapping("write/{boardId}")
	public CMRespDto<Comment> writeComment(@PathVariable int boardId, @RequestBody Comment comment, 
			HttpSession session, HttpRequest request) {
//		CBoard board = boardService.findById(id); // 게시글을 가져옴
		List<Comment> comments = new ArrayList<Comment>(); // 댓글목록을 담을 리스트를 선언
		User user = (User) session.getAttribute("loginUser"); // 세션유저정보를 저장
		CMRespDto<Comment> cm = new CMRespDto<Comment>(); // 공통 dto 선언

		HttpHeaders headers = request.getHeaders();
		System.out.println("안드로이드 요청 헤더 : " + headers.toString());
		
		if (user != null) {
			comment.setBoardId(boardId);// 게시판 id를 설정
			comment.setUserId(user.getId()); // 사용자 id를 설정
			commentService.write(comment); // 작성한 댓글을 Comment 디비에 저장
			comments = commentService.findByBoardId(boardId); // 댓글 목록을 가져옴
			System.out.println("comments : " + comments);
			comments.add(comment);
			cm.setCode(1);
			cm.setMsg("댓글달기 성공");
			cm.setData(comment);
			return cm;
		}
		cm.setCode(-1);
		cm.setMsg("댓글달기 실패 : 로그인 정보 없음");
		cm.setData(comment);
		return cm;
	}
	
	// 댓글 수정하기
	@PutMapping("update/{cid}")
	public CMRespDto<Comment> updateComment(@PathVariable int cid, @RequestBody Comment comment, HttpSession session){
		User user = (User) session.getAttribute("loginUser"); // 로그인된 유저 정보 저장
		System.out.println("[테스트] 현재 로그인한 사용자 정보 "+user);
		CMRespDto<Comment> cm = new CMRespDto<Comment>();
		Comment originComment = commentService.findById(cid); // cid로 댓글 하나를 가져옴
		
		// 로그인한 유저와 댓글 작성자가 같을때
		if((user!=null)&&(user.getId()==originComment.getUserId())) {
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
	@DeleteMapping("delete/{cid}")
	public CMRespDto<Comment> deleteComment(@PathVariable int cid, HttpSession session){
		User user = (User) session.getAttribute("loginUser");
		CMRespDto<Comment> cm = new CMRespDto<Comment>();
		Comment comment = commentService.findById(cid);
		
		// 로그인한 유저와 댓글 작성자가 같을때
		if((user!=null)&&(user.getId()==comment.getUserId())) {
			commentService.delete(comment);
			cm.setCode(1);
			cm.setMsg("댓글삭제 성공");
			cm.setData(comment);
			return cm;
		}
		
		cm.setCode(-1);
		cm.setMsg("댓글삭제 실패 : : 댓글작성자가 아님");
		cm.setData(comment);
		return cm;
		
	}
	
	
	
	
}
