package com.kh.spring.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

public class AccessValidator extends HandlerInterceptorAdapter{

	//preHandle : 전처리, postHandle : 후처리
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object hanlder ) {
		
		// 요청 url 정보 spring/board/list.bo -> board/list.bo
		String requestUrl = req.getRequestURI().substring(req.getContextPath().length());
		
		// 권한 체크
		String role = getGrade(req.getSession());
		
		//로그인 안한 사용자
		if(role == null) {// 권한이 없다(로그인을 안함) 
			
			try {
				req.setAttribute("errorMsg", "로그인 후 이용할 수 있습니다.");
				req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req, res);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		//로그인 했지만 권한이 없는 사용자.
		if(requestUrl.indexOf("admin") > -1 && !role.equals("A")) { //requestUrl.indexOf("admin") > -1 = url에 admin이라는 내용이 포함됨
			try {
				req.setAttribute("errorMsg", "권한이 없습니다.");
				req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req, res);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		// 모두 통과했을때
		return true;
	}
	
	public String getGrade(HttpSession session) {
		
		Member member = (Member)session.getAttribute("loginUser");
		
		if(member == null) {
			return null;
		}
		
		return member.getRole();
	}
	
}
