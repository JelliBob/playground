package com.cos.playground.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.playground.dto.CMRespDto;
import com.cos.playground.dto.LoginDto;
import com.cos.playground.model.CBoard;
import com.cos.playground.model.Comment;
import com.cos.playground.model.Fav;
import com.cos.playground.model.User;
import com.cos.playground.service.CBoardService;
import com.cos.playground.service.UserService;

@RequestMapping("/user/*")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CBoardService cboardService;

	// 회원 가입하기
	// 1. 유효성 검사
	// 2. 아이디 중복체크
	// 3. 가입일자 생성
	// 4. 비밀번호 암호화
	@PostMapping("join")
	public CMRespDto<User> join(@RequestBody User user) {
		CMRespDto<User> cm = new CMRespDto<User>();
		if (user.getUsername() == null || user.getPassword() == null) {
			cm.setCode(-1);
			cm.setMsg("회원가입 실패 : 아이디와 비밀번호는 필수 입력항목임");
			return cm;
		} else if (userService.findByUsername(user.getUsername()) != null) { // 이미 존재하는 아이디일 경우
			cm.setCode(-1);
			cm.setMsg("회원가입 실패 : 아이디 중복");
			return cm;
		} else {
			// 비밀번호 암호화 처리
			String password = user.getPassword();
			String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			user.setPassword(hashPassword);
			// 가입 날짜, 시간 정보 설정
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			user.setRegdate(ts);
			userService.join(user);
			cm.setCode(1);
			cm.setMsg("회원가입 성공");
			cm.setData(user);
			return cm;
		}
	}

	// 로그인
	@PostMapping("login")
	public CMRespDto<User> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
		CMRespDto<User> cm = new CMRespDto<User>();
		User user = userService.findByUsername(loginDto.getUsername());

		if (user == null) { // 회원 정보가 없으면 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 회원정보가 없음");
			return cm;
		} else if (BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) { // 비밀번호 일치시 로그인 성공
			// 사용자를 인증하는 쿠키값을 응답 헤더에 담아서 전송
			Cookie cookie = new Cookie("user", "authorized");
			response.addCookie(cookie); // 쿠키를 담아서 보내고

			cm.setCode(1);
			cm.setMsg("로그인 성공");
			cm.setData(user);
			return cm;
		} else { // 비밀번호 틀렸을 시 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 비밀번호 틀림");
			return cm;
		}
	}

	// 회원정보 수정하기
	// 아이디와 가입날짜는 변경이 불가하다고 가정
	// 안드로이드에서 현재 로그인한 user에 대한 정보를 매개변수로 받음
	@PutMapping("update")
	public CMRespDto<User> update(@RequestBody User user, HttpServletRequest request) {
		CMRespDto<User> cm = new CMRespDto<User>();
		User originUser = userService.findByUsername(user.getUsername());

		// 클라이언트에서 쿠키값을 받아서 검증
		System.out.println(request.getHeader("Cookie"));
		String cookie = request.getHeader("Cookie").substring(0, 15);

		// 비밀번호 암호화 처리
		String password = user.getPassword();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		user.setPassword(hashPassword);

		// 클라이언트에서 쿠키값을 받아서 검증
		if (cookie.equals("user=authorized")) {
			user.setUsername(originUser.getUsername()); // user 객체에 다시 username을 저장해줌
			user.setRegdate(originUser.getRegdate()); // user 객체에 다시 가입날짜를 저장해줌
			userService.updateInfo(user); // 비밀번호, 이름(name), 이메일, 전화번호, 커리어만 수정가능
			cm.setCode(1);
			cm.setMsg("회원정보 수정 성공");
			cm.setData(user);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("회원정보 수정 실패 : 로그인 후 이용가능");
			cm.setData(user);
			return cm;

		}
	}

	// 로그아웃 하기
	@GetMapping("logout")
	public CMRespDto<User> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		CMRespDto<User> cm = new CMRespDto<User>();
		cm.setCode(1);
		cm.setMsg("로그아웃 성공");

		return cm;
	}

	// 회원 탈퇴하기
	@PostMapping("remove")
	public CMRespDto<User> remove(@RequestBody User user, HttpServletRequest request) {

		String cookie = request.getHeader("Cookie").substring(0, 15);
		CMRespDto<User> cm = new CMRespDto<User>();

		// 로그인한 사용자일때
		if (cookie.equals("user=authorized")) {
			userService.delete(user);
			cm.setCode(1);
			cm.setMsg("회원탈퇴 성공");
			cm.setData(user);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("회원탈퇴 실패");
			return cm;
		}
	}

	// 마이페이지
	@GetMapping("mypage/{userId}")
	public CMRespDto<User> mypage(@PathVariable int userId, HttpServletRequest request) {
		CMRespDto<User> cm = new CMRespDto<User>();
		String cookie = request.getHeader("Cookie").substring(0, 15);

		User user = userService.findById(userId);

		// 로그인한 사용자일때
		if (cookie.equals("user=authorized")) {
			cm.setCode(1);
			cm.setMsg("마이페이지 조회 성공");
			cm.setData(user);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("마이페이지 조회 실패");
			return cm;
		}
	}

	// 커뮤니티 게시글 좋아요 누르기
	// 좋아요 내역이 있는지 확인
	// 좋아요를 누르면 cboard의 fav_count가 1 증가
	@PostMapping("fav")
	public CMRespDto<Fav> likePost(@RequestBody Fav fav, HttpServletRequest request) {
		CMRespDto<Fav> cm = new CMRespDto<Fav>();
		String cookie = request.getHeader("Cookie").substring(0, 15);
		Fav fav1 = userService.favHistory(fav.getUserId(), fav.getBoardId());

		// 로그인한 사용자일때
		if (cookie.equals("user=authorized")) {
			// 이미 좋아요를 누른 게시글일때
			if (fav1 != null) {
				userService.cancleFav(fav.getUserId(), fav.getBoardId());
				cboardService.decreaseFav(fav.getBoardId());
				cm.setCode(1);
				cm.setMsg("좋아요가 취소됨");
				cm.setData(fav);
				return cm;
			} else { // 좋아요를 누른적이 없다면
				userService.likePost(fav.getUserId(), fav.getBoardId());
				cboardService.increaseFav(fav.getBoardId());
				cm.setCode(1);
				cm.setMsg("좋아요 누르기 성공");
				cm.setData(fav);
				return cm;
			}
		} else {
			cm.setCode(-1);
			cm.setMsg("좋아요 누르기 실패 : 로그인 내역 없음");
			return cm;
		}

	}
	
	// 좋아요 누른 글 목록 보기
	@GetMapping("favList/{userId}")
	public CMRespDto<List<CBoard>> favList(@PathVariable int userId, HttpServletRequest request){
		CMRespDto<List<CBoard>> cm = new CMRespDto<List<CBoard>>();
		String cookie = request.getHeader("Cookie").substring(0, 15);
		List<CBoard> list = userService.favList(userId);
		
		if (cookie.equals("user=authorized")) {
			if (list.size()!=0) {
				cm.setCode(1);
				cm.setMsg("좋아요 목록 조회 성공");
				cm.setData(list);
				return cm;
			} else {
				cm.setCode(1);
				cm.setMsg("좋아요를 누른 내역이 없음");
				cm.setData(list);
				return cm;
			}
		} else {
			cm.setCode(1);
			cm.setMsg("게시글 목록 조회 실패 : 로그인 정보 없음");
			return cm;
		}
	}
	
	
	// 작성글 목록 보기
	@GetMapping("myPost/{userId}")
	public CMRespDto<List<CBoard>> myPost(@PathVariable int userId, HttpServletRequest request) {
		CMRespDto<List<CBoard>> cm = new CMRespDto<List<CBoard>>();
		String cookie = request.getHeader("Cookie").substring(0, 15);
		List<CBoard> list = userService.myPostList(userId);
		

		if (cookie.equals("user=authorized")) {
			if (list.size()!=0) {
				cm.setCode(1);
				cm.setMsg("게시글 목록 조회 성공");
				cm.setData(list);
				return cm;
			} else {
				cm.setCode(1);
				cm.setMsg("아직 작성한 게시글이 없음");
				cm.setData(list);
				return cm;
			}
		} else {
			cm.setCode(-1);
			cm.setMsg("게시글 목록 조회 실패 : 로그인 정보 없음");
			return cm;
		}
	}
	
	// 작성한 댓글 목록 보기
	@GetMapping("myComment/{userId}")
	public CMRespDto<List<Comment>> myComment(@PathVariable int userId, HttpServletRequest request){
		CMRespDto<List<Comment>> cm = new CMRespDto<List<Comment>>();
		String cookie = request.getHeader("Cookie").substring(0, 15);
		List<Comment> list = userService.myCommentList(userId);
		
		if (cookie.equals("user=authorized")) {
			if (list.size()!=0) {
				cm.setCode(1);
				cm.setMsg("댓글 목록 조회 성공");
				cm.setData(list);
				return cm;
			} else {
				cm.setCode(1);
				cm.setMsg("아직 작성한 댓글이 없음");
				cm.setData(list);
				return cm;
			}
		} else {
			cm.setCode(-1);
			cm.setMsg("댓글 목록 조회 실패 : 로그인 정보 없음");
			return cm;
		}
	}
	
	
	// 채용 게시글 북마크하기
	
	// 북마크 내역 보기
	
	// 입사 지원하기
	
	// 입사 지원 내역 보기
	

}
