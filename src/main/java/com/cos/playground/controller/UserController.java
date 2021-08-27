package com.cos.playground.controller;

import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.playground.dto.CMRespDto;
import com.cos.playground.dto.LoginDto;
import com.cos.playground.model.User;
import com.cos.playground.service.UserService;

@RequestMapping("/user/*")
@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	// 회원 가입하기 
	// 1. 유효성 검사
	// 2. 아이디 중복체크
	// 3. 가입일자 생성
	// 4. 비밀번호 암호화
	@PostMapping("join")
	public CMRespDto<User> join(@RequestBody User user) {
		CMRespDto<User> cm = new CMRespDto<User>();
		if(user.getUsername()==null||user.getPassword()==null) {
			cm.setCode(-1);
			cm.setMsg("회원가입 실패 : 아이디와 비밀번호는 필수 입력항목임");
			return cm;
		} else if(userService.findByUsername(user.getUsername())!=null){ // 이미 존재하는 아이디일 경우
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
		
		if(user==null) { // 회원 정보가 없으면 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 회원정보가 없음");
			return cm;
		} else if(BCrypt.checkpw(loginDto.getPassword(), user.getPassword())){ // 비밀번호 일치시 로그인 성공 
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
		String cookie = request.getHeader("Cookie").substring(0,15);
		
		// 비밀번호 암호화 처리
		String password = user.getPassword();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		user.setPassword(hashPassword);
		
		// 클라이언트에서 쿠키값을 받아서 검증
		if(cookie.equals("user=authorized")) {
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
	public CMRespDto<User> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		// 쿠키에 담긴 내용 삭제. 이건 안드로이드에서 하는건가?
//		Cookie[] cookies = request.getCookies();
//		if(cookies!=null) {
//			for(int i=0;i<cookies.length;i++) {
//				cookies[i].setMaxAge(0);
//				response.addCookie(cookies[i]);
//			}
//		}
//		
//		System.out.println("쿠키 삭제됐는지 확인");
//		System.out.println(cookies[0].getName()+":"+cookies[0].getValue());

		
		CMRespDto<User> cm = new CMRespDto<User>();
		cm.setCode(1);
		cm.setMsg("로그아웃 성공");
		
		return cm;
	}
	
	
	// 회원 탈퇴하기
	// 비밀번호 확인후 탈퇴 처리(아직)
	@PostMapping("remove")
	public CMRespDto<User> remove(@RequestBody User user, HttpServletRequest request){

		String cookie = request.getHeader("Cookie").substring(0,15);
		CMRespDto<User> cm = new CMRespDto<User>();

		// 로그인한 회원이고 비밀번호가 일치할 때
		if(cookie.equals("user=authorized")) {
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
	
	// 커뮤니티 게시글 좋아요 누르기
	
	// 채용 게시글 북마크하기
	
	// 댓글 달기
}
