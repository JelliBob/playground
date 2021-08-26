package com.cos.playground.controller;

import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
	public CMRespDto<User> login(@RequestBody LoginDto loginDto, HttpSession session) {
		CMRespDto<User> cm = new CMRespDto<User>();
		User user = userService.findByUsername(loginDto.getUsername());
		
		if(user==null) { // 회원 정보가 없으면 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 회원정보가 없음");
			return cm;
		} else if(BCrypt.checkpw(loginDto.getPassword(), user.getPassword())){ // 비밀번호 일치시 로그인 성공 
			session.setAttribute("loginUser", user); // 로그인시 회원 정보를 세션에 저장
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
	// 세션에 저장된 로그인 객체가 있는지 확인
	@PutMapping("update")
	public CMRespDto<User> update(@RequestBody User user, HttpSession session) {
		CMRespDto<User> cm = new CMRespDto<User>();
		if(session.getAttribute("loginUser")==null) {
			cm.setCode(-1);
			cm.setMsg("회원정보 수정 실패 : 로그인 후 이용가능");
			cm.setData(user);
			return cm;
		} else {
			userService.updateInfo(user);
			cm.setCode(1);
			cm.setMsg("회원정보 수정 성공");
			cm.setData(user);
			return cm;
		}
	}
	
	// 로그아웃 하기
	@GetMapping("logout")
	public CMRespDto<User> logout(HttpSession session){
		CMRespDto<User> cm = new CMRespDto<User>();
		cm.setCode(1);
		cm.setMsg("로그아웃 성공");
		cm.setData((User)session.getAttribute("loginUser"));
		session.invalidate();
		return cm;
	}
	
	
	// 회원 탈퇴하기
	@SuppressWarnings("rawtypes")
	// 비밀번호 확인후 탈퇴 처리
	@PostMapping("remove")
	public CMRespDto<User> remove(@RequestBody String password, HttpSession session){
		User user = (User)session.getAttribute("loginUser");
		String pwd=null;
		JSONParser parser = new JSONParser();
		try {
			Object jsonObject = (JSONObject) parser.parse(password);
			pwd = (String) ((HashMap) jsonObject).get("password");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CMRespDto<User> cm = new CMRespDto<User>();
		
		if(BCrypt.checkpw(pwd, user.getPassword())) {
			userService.delete(user);
			cm.setCode(1);
			cm.setMsg("회원탈퇴 성공");
			cm.setData(user);
			return cm;
		} else {
			System.out.println(pwd);
			cm.setCode(-1);
			cm.setMsg("회원탈퇴 실패 : 비밀번호 틀림");
			return cm;
		}
	}
	
	// 커뮤니티 게시글 좋아요 누르기
	
	// 채용 게시글 북마크하기
	
	// 댓글 달기
}
