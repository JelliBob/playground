package com.cos.playground.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cos.playground.dao.FileDAO;
import com.cos.playground.dto.BoardDetailDto;
import com.cos.playground.dto.BoardWriteDto;
import com.cos.playground.dto.CMRespDto;
import com.cos.playground.entity.UploadFile;
import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.Fav;
import com.cos.playground.model.User;
import com.cos.playground.service.CBoardService;
import com.cos.playground.service.CommentService;
import com.cos.playground.service.FileUploadDownloadService;
import com.cos.playground.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@RequestMapping("/cboard/*")
@RestController
public class CBoardController {

	@Autowired
	private UserService userService;

	@Autowired
	private CBoardService boardService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private FileUploadDownloadService fileService;

	@Autowired
	private FileDAO fileDAO;

	// 게시글 한 건 보기
	// 주소 요청시 viewCount 1 증가
	@PostMapping("detail/{id}")
	public CMRespDto<BoardDetailDto> detail(@PathVariable int id, @RequestBody User user) {
		// 현재 로그인한 User 정보를 받아야함. detail 화면에서 수정 삭제 권한이 달라지기때문에.
		// 안드로이드에서는 SessionUser 클래스의 User를 전달하면 될듯함.

		CBoard board = boardService.findById(id); // id로 게시글 한 건 가져오기
		List<Comment> comments = commentService.findByBoardId(id); // 게시글 id로 댓글 목록 가져오기

		BoardDetailDto boardDto = new BoardDetailDto();
		boardDto.setId(id);
		boardDto.setTitle(board.getTitle());
		boardDto.setContent(board.getContent());
		boardDto.setRegdate(board.getRegdate());
		boardDto.setFavCount(board.getFavCount());
		boardDto.setViewCount(board.getViewCount());
		boardDto.setCommentCount(board.getCommentCount());
		boardDto.setUserId(board.getUserId());
		boardDto.setWriter(board.getWriter());
		boardDto.setCategory(board.getCategory());
		boardDto.setComments(comments);
		User user1 = userService.findByUsername(user.getUsername());
		boardDto.setUsermail(user1.getEmail()); // usermail 담아보내기
		// 좋아요 내역이 있으면 1, 없으면 0
		Fav fav = userService.favHistory(user.getId(), id);
		if (fav == null) {
			boardDto.setIsFav(0);
		} else {
			boardDto.setIsFav(1);
		}
		board.getFileId();
		// 파일을 아이디로 검색해서 파일이름을 넣어줘야함
		if (board.getFileId() != 0) {
			Resource resource = fileService.loadFileAsResource(fileDAO.findByFileId(board.getFileId()));
			boardDto.setResource(resource);
		}
		// 안드로이드에서 resource를 url conntect 요청을 해서 비트맵으로 변환후 이미지로 뿌려주면 될듯

		CMRespDto<BoardDetailDto> cm = new CMRespDto<BoardDetailDto>();

		// 사용자가 게시글 작성자일때
		if (board.getUserId() == user.getId()) {
			cm.setCode(1);
			cm.setMsg("게시글 한건보기 성공(로그인한 유저 = 글 작성자)");
			cm.setData(boardDto);
			boardService.increaseView(id); // 조회수 1 증가
		} else {
			cm.setCode(0);
			cm.setMsg("게시글 한건보기 성공");
			cm.setData(boardDto);
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
	@PostMapping(value = "write", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public CMRespDto<CBoard> write(@RequestPart(value = "strBoardDto") String strBoardDto, HttpServletRequest request,
			@RequestPart(value = "file", required = false) MultipartFile file) throws ParseException, IOException {
		// 안드로이드에서 SessionUser 클래스의 User의 id를 BoardWriteDto에 담아와야 할듯.

		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();

		String cookie = request.getHeader("Cookie").substring(0, 15);

		// 문자열을 json으로
		System.out.println("테스트중 " + strBoardDto); // 양 끝 따옴표 제거
		strBoardDto = strBoardDto.substring(1, strBoardDto.length() - 1);
		strBoardDto = strBoardDto.replaceAll("\\\\", "");
		System.out.println("테스트중 " + strBoardDto);

		if (file != null) {
			System.out.println("Multipart file");
			System.out.println("name : " + file.getName());
			System.out.println("originFileName : " + file.getOriginalFilename());
			System.out.println("class : " + file.getClass());
			System.out.println("contentType : " + file.getContentType());
			System.out.println("resource uri : " + file.getResource());
			System.out.println("bytes : " + file.getBytes());
		}

//		// json string을 자바 객체로
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		BoardWriteDto boardDto = objectMapper.readValue(strBoardDto, BoardWriteDto.class);
		System.out.println("제목 : " + boardDto.getTitle());

		// 디비의 cboard 테이블에 글쓰기 내용을 저장
		CBoard board = new CBoard();
		board.setTitle(boardDto.getTitle()); // 제목
		board.setContent(boardDto.getContent()); // 내용
		board.setCategory(boardDto.getCategory()); // 카테고리
		board.setUserId(boardDto.getUserId()); // userId
		User user = userService.findById(boardDto.getUserId());
		board.setWriter(user.getUsername()); // 글 작성자
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		board.setRegdate(ts); // 글 작성 시간
//		User user = userService.findById(boardDto.getUserId());
//		board.setWriter(user.getUsername());

		// 파일 테이블에 저장하기
		// 안드로이드에서 글쓰기 요청시 MultiparFile 객체를 파일이름과 함께 전달해줘야함

		if (file != null) {
			UploadFile uploadFile = fileService.storeFile(file);
			board.setFileId(uploadFile.getId());
		}

		// BoardDetailDto에 파일 (파일 아이디) 담아서 보내기 (가져올땐 아이디만 검색해서 BoardDetailDto에 넣어주면되니까)
		// 파일을 받아서 파일의 id만 저장해서 보내면 될것같기도 하고..?

		if (cookie.equals("user=authorized")) {
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
	public CMRespDto<CBoard> update(@PathVariable int id, @RequestBody BoardWriteDto boardDto,
			HttpServletRequest request) {
		CMRespDto<CBoard> cm = new CMRespDto<CBoard>();

		CBoard board = boardService.findById(id); // 원본 게시글 가져오기

		// BoardDto의 userId와 게시글의 userID가 일치할 경우
		// 안드로이드에서 user 정보를 BoardDto에 담아서 보내줘야함
		if (boardDto.getUserId() == board.getUserId()) {
			board.setTitle(boardDto.getTitle()); // 제목 수정
			board.setContent(boardDto.getContent()); // 내용 수정
			board.setCategory(boardDto.getCategory()); // 카테고리 수정
			User user = userService.findById(boardDto.getUserId()); // writer를 user의 username으로 설정하기 위함.
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
	@PostMapping("delete/{id}")
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

	// 인기 게시글 보기
	@GetMapping("topPost")
	public CMRespDto<List<CBoard>> topPost() {
		CMRespDto<List<CBoard>> cm = new CMRespDto<List<CBoard>>();

		cm.setCode(1);
		cm.setMsg("인기 게시글 조회 성공");
		cm.setData(boardService.topPost());
		return cm;
	}

}
