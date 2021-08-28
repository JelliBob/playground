package com.cos.playground.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import com.cos.playground.dto.BoardWriteDto;
import com.cos.playground.dto.CMRespDto;
import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.User;
import com.cos.playground.service.CBoardService;
import com.cos.playground.service.CommentService;
import com.cos.playground.service.UserService;

@RequestMapping("/cboard/*")
@RestController
public class CBoardController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private CBoardService boardService;

	@Autowired
	private CommentService commentService;

	// 게시글 한 건 보기
	// 주소 요청시 viewCount 1 증가
	@PostMapping("detail/{id}")
	public CMRespDto<CBoard> detail(@PathVariable int id, @RequestBody User user) {
		// 현재 로그인한 User 정보를 받아야함. detail 화면에서 수정 삭제 권한이 달라지기때문에.
		// 안드로이드에서는 SessionUser 클래스의 User를 전달하면 될듯함.
		
		CBoard board = boardService.findById(id); // id로 게시글 한 건 가져오기
		List<Comment> comments = commentService.findByBoardId(id); // 게시글 id로 댓글 목록 가져오기
		board.setComments(comments);
		
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();
		
		// 사용자가 게시글 작성자일때
		if(board.getUserId() == user.getId()) {
			cm.setCode(1);
			cm.setMsg("게시글 한건보기 성공(로그인한 유저 = 글 작성자)");
			cm.setData(board);
			boardService.increaseView(id); // 조회수 1 증가
		} else {
			cm.setCode(0);
			cm.setMsg("게시글 한건보기 성공");
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
	public CMRespDto<CBoard> write(@RequestBody BoardWriteDto boardDto, 
			HttpServletRequest request) {
		// 안드로이드에서 SessionUser 클래스의 User의 id를 BoardWriteDto에 담아와야 할듯.
		
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();
		
		String cookie = request.getHeader("Cookie").substring(0,15);
	
		CBoard board = new CBoard();
		board.setTitle(boardDto.getTitle());
		board.setContent(boardDto.getContent());
		board.setCategory(boardDto.getCategory());
		board.setUserId(boardDto.getUserId());
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		board.setRegdate(ts);
		User user = userService.findById(boardDto.getUserId());
		board.setWriter(user.getUsername());
		
		if(cookie.equals("user=authorized")) {
			cm.setCode(1);
			cm.setMsg("글쓰기 성공");
			cm.setData(board);
			boardService.write(board);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("글쓰기 실패 : 로그인 상태가 아님");
			cm.setData(board);
			return cm;
		}
		
	}

	// 게시글 수정하기
	@PutMapping("update/{id}")
	public CMRespDto<CBoard> update(@PathVariable int id,@RequestBody BoardWriteDto boardDto, 
			HttpServletRequest request) {
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();
		
		CBoard board = boardService.findById(id); // 원본 게시글 가져오기
		User user = userService.findById(boardDto.getUserId()); // writer를 user의 username으로 설정하기 위함.
		
		// BoardDto의 userId와 게시글의 userID가 일치할 경우
		// 안드로이드에서 user 정보를 BoardDto에 담아서 보내줘야함
		if (boardDto.getUserId() == board.getUserId()) {
			board.setTitle(boardDto.getTitle());
			board.setContent(boardDto.getContent());
			board.setCategory(boardDto.getCategory());
			board.setWriter(user.getUsername());
			boardService.update(board);
			cm.setCode(1);
			cm.setMsg("글수정 성공");
			cm.setData(board);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("글수정 실패 : 수정권한이 없음");
			return cm;
		}
	}

	// 게시글 삭제하기
	@DeleteMapping("delete/{id}")
	public CMRespDto<CBoard> delete(@PathVariable int id, @RequestBody User user) {
		// 게시글의 writer 정보와 현재 로그인한 user의 정보를 알아야함
		CBoard board = boardService.findById(id); // 게시글 하나의 정보를 가져옴
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
