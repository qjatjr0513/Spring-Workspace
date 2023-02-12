package com.kh.spring.common.interceptor;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.BoardType;

public class BoardTypeInterceptor extends HandlerInterceptorAdapter{

	// 
	@Autowired
	private BoardService boardService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// application scope에 boardTypeList를 저장.
		
		// 항상저장하는것이 아니라 application scope에 boardTypeList가 없는 경우에만. 
		
		//application 객체 얻어오기
		ServletContext application = ((HttpServletRequest) request).getSession().getServletContext();
		
		if(application.getAttribute("boardTypeList") == null) {
			//boardService를 통해 Board_CD 값들을 얻어옴.
			List<BoardType> boardTypeList= boardService.selectBoardTypeList();
			
			application.setAttribute("boardTypeList", boardTypeList);
		}
		
		return true;
	}
}
