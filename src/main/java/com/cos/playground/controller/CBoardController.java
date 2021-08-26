package com.cos.playground.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.playground.dto.CMRespDto;
import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.User;
import com.cos.playground.service.CBoardService;
import com.cos.playground.service.CommentService;

@RequestMapping("/cboard/*")
@RestController
public class CBoardController {

	@Autowired
	private CBoardService boardService;

	@Autowired
	private CommentService commentService;

	// 게시글 한 건 보기
	// 주소 요청시 viewCount 1 증가
	@GetMapping("detail/{id}")
	public CMRespDto<CBoard> detail(@PathVariable int id, HttpSession session) {
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();
		User user = (User) session.getAttribute("loginUser");
		CBoard board = boardService.findById(id);
		List<Comment> comments = commentService.findByBoardId(id);
		board.setComments(comments);

		if (user == null) {
			cm.setCode(0);
			cm.setMsg("게시글 한건보기 성공");
			cm.setData(board);
			boardService.increaseView(id); // 조회수 1 증가
		} else if (board.getUserId() == user.getId()) { // 사용자가 로그인되어있고 게시글 작성자일때
			cm.setCode(1);
			cm.setMsg("게시글 한건보기 성공(로그인한 유저 = 글 작성자)");
			cm.setData(board);
			boardService.increaseView(id); // 조회수 1 증가
		}
		return cm;

	}

	// 게시글 목록보기
	@GetMapping("list")
	public CMRespDto<List<CBoard>> list() {
		CMRespDto<List<CBoard>> cm = new CMRespDto<List<CBoard>>();
		cm.setCode(1);
		cm.setMsg("게시글 목록보기 성공");
		cm.setData(boardService.findAll());
		return cm;
	}

	// 게시글 쓰기
	@PostMapping("write")
	public CMRespDto<CBoard> write(@RequestBody CBoard board, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		board.setUserId(user.getId());
		board.setWriter(user.getUsername());

		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();
		cm.setCode(1);
		cm.setMsg("글쓰기 성공");
		cm.setData(board);
		boardService.write(board);
		return cm;
	}

	// 게시글 수정하기
	@PutMapping("update/{id}")
	public CMRespDto<CBoard> update(@PathVariable int id, @RequestBody CBoard board, HttpSession session) {
		CBoard originboard = boardService.findById(id);
		User user = (User) session.getAttribute("loginUser");
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();

		if (user.getId() == originboard.getUserId()) {
			originboard.setTitle(board.getTitle());
			originboard.setContent(board.getContent());
			boardService.update(originboard);
			cm.setCode(1);
			cm.setMsg("글수정 성공");
			cm.setData(originboard);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("글수정 실패 : 수정권한이 없음");
			return cm;
		}
	}

	// 게시글 삭제하기
	@DeleteMapping("delete/{id}")
	public CMRespDto<CBoard> delete(@PathVariable int id, HttpSession session) {
		CBoard board = boardService.findById(id);
		User user = (User) session.getAttribute("loginUser");
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();

		if (user.getId() == board.getUserId()) {
			boardService.delete(id);
			cm.setCode(1);
			cm.setMsg("글삭제 성공");
			cm.setData(board);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("글삭제 실패");
			cm.setData(board);
			return cm;
		}
	}

	
	

}
