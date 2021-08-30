package com.cos.playground.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.cos.playground.model.Company;
import com.cos.playground.model.RBoard;
import com.cos.playground.service.CompanyService;

@RequestMapping("/company/*")
@RestController
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	// 회원가입
	@PostMapping("join")
	public CMRespDto<Company> join(@RequestBody Company company) {
		CMRespDto<Company> cm = new CMRespDto<Company>();

		// 아이디 혹은 비밀번호 미입력시
		if (company.getUsername() == null || company.getPassword() == null) {
			cm.setCode(-1);
			cm.setMsg("회원가입 실패 : 아이디와 비밀번호는 필수 입력항목임");
			return cm;
		} else if (companyService.findByUsername(company.getUsername()) != null) {
			cm.setCode(-1);
			cm.setMsg("회원가입 실패 : 아이디 중복");
			return cm;
		} else {
			// 비밀번호 암호화 처리
			String password = company.getPassword();
			String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			company.setPassword(hashPassword);
			companyService.join(company);
			cm.setCode(1);
			cm.setMsg("회원가입 성공");
			cm.setData(company);
			return cm;
		}
	}

	// 로그인
	@PostMapping("login")
	public CMRespDto<Company> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
		CMRespDto<Company> cm = new CMRespDto<Company>();
		Company company = companyService.findByUsername(loginDto.getUsername());

		if (company == null) { // 회원 정보가 없으면 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 회원정보가 없음");
			return cm;
		} else if (BCrypt.checkpw(loginDto.getPassword(), company.getPassword())) { // 비밀번호 일치시 로그인 성공
			// 사용자를 인증하는 쿠키값을 응답 헤더에 담아서 전송
			Cookie cookie = new Cookie("user", "authorized");
			response.addCookie(cookie); // 쿠키를 담아서 보내고

			cm.setCode(1);
			cm.setMsg("로그인 성공");
			cm.setData(company);
			return cm;
		} else { // 비밀번호 틀렸을 시 로그인 실패
			cm.setCode(-1);
			cm.setMsg("로그인 실패 : 비밀번호 틀림");
			return cm;
		}
	}

	// 회원정보 수정
	@PutMapping("update")
	public CMRespDto<Company> update(@RequestBody Company company, HttpServletRequest request) {
		CMRespDto<Company> cm = new CMRespDto<Company>();
		Company originCompany = companyService.findByUsername(company.getUsername());

		// 클라이언트에서 쿠키값을 받아서 검증
		System.out.println(request.getHeader("Cookie"));
		String cookie = request.getHeader("Cookie").substring(0, 15);

		// 비밀번호 암호화 처리
		String password = company.getPassword();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		company.setPassword(hashPassword);

		// 클라이언트에서 쿠키값을 받아서 검증
		if (cookie.equals("user=authorized")) {
			company.setUsername(originCompany.getUsername()); // user 객체에 다시 username을 저장해줌
			companyService.updateInfo(company);
			cm.setCode(1);
			cm.setMsg("회원정보 수정 성공");
			cm.setData(company);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("회원정보 수정 실패 : 로그인 후 이용가능");
			cm.setData(company);
			return cm;

		}
	}
	
	
	
	// 로그아웃
	

	// 회원 탈퇴하기
	@PostMapping("remove")
	public CMRespDto<Company> remove(@RequestBody Company company, HttpServletRequest request) {

		String cookie = request.getHeader("Cookie").substring(0, 15);
		CMRespDto<Company> cm = new CMRespDto<Company>();

		// 로그인한 사용자일때
		if (cookie.equals("user=authorized")) {
			companyService.delete(company);
			cm.setCode(1);
			cm.setMsg("회원탈퇴 성공");
			cm.setData(company);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("회원탈퇴 실패");
			return cm;
		}
	}
	

	// 마이페이지
	@GetMapping("mypage/{companyId}")
	public CMRespDto<Company> mypage(@PathVariable int companyId, HttpServletRequest request) {
		CMRespDto<Company> cm = new CMRespDto<Company>();
		String cookie = request.getHeader("Cookie").substring(0, 15);

		Company company = companyService.findById(companyId);

		// 로그인한 사용자일때
		if (cookie.equals("user=authorized")) {
			cm.setCode(1);
			cm.setMsg("마이페이지 조회 성공");
			cm.setData(company);
			return cm;
		} else {
			cm.setCode(-1);
			cm.setMsg("마이페이지 조회 실패");
			return cm;
		}
	}
	

	// 작성글 보기
	@GetMapping("myPost/{companyId}")
	public CMRespDto<List<RBoard>> myPost(@PathVariable int companyId, HttpServletRequest request) {
		CMRespDto<List<RBoard>> cm = new CMRespDto<List<RBoard>>();
		String cookie = request.getHeader("Cookie").substring(0, 15);
		List<RBoard> list = companyService.myPostList(companyId);
		

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
			cm.setCode(1);
			cm.setMsg("게시글 목록 조회 성공 : 로그인 정보 없음");
			return cm;
		}
	}
	

	// 입사 지원자 목록 보기

}
