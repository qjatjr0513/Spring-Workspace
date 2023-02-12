package com.kh.spring.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.member.model.vo.Member;

@Controller
@SessionAttributes({"loginUser", "chatRoomNo"})
// model에 추가된 값의 key와 SessionAttribute 어노테이션에 작성된 키값이 같으면
// 해당 값을 session scope에 자동으로 이동시켜줌.
public class ChattingController {
	
	@Autowired
	private ChatService service;
	
	// 채팅방 목록 조회
	@GetMapping("/chat/chatRoomList")
	public String selectChatRoomList(Model model) {
		
		List<ChatRoom> crList = service.selectChatRoomList();
		
		model.addAttribute("chatRoomList", crList);
		
		return "chat/chatRoomList";
	}
	
	// 채팅방 만들기
	@PostMapping("/chat/openChatRoom")
	public String openChatRoom(
								@ModelAttribute("loginUser") Member loginUser, 
								Model model,
								ChatRoom room, 
								RedirectAttributes ra) {
		
		room.setUserNo(loginUser.getUserNo());
		
		int chatRoomNo = service.openChatRoom(room);
		
		String path = "redirect:/chat/";
		
		if(chatRoomNo > 0) {
			//path += "room/"+chatRoomNo; // 상세보기화면 구현후 변경
			ra.addFlashAttribute("alertMsg", "채팅방 만들기 성공");
			path +="chatRoomList";
		}else {
			path += "chatRoomList";
			ra.addFlashAttribute("alertMsg", "채팅방 만들기 실패");
			/*
			 * addFlashAttribute 내가 처음 넣은 객체를 sessionScope에 저장시켰다가, 
			 * redirect가 완료되면 requestScope로 변경해줌.
			 * 
			 */
		}
		return path;
		//@ModelAttribute -> 커맨드객체 (@ModelAttribute 생략가능.)
	}
	
	// 채팅방 입장.
	@GetMapping("/chat/room/{chatRoomNo}")
	public String joinChatRoom(
								@ModelAttribute("loginUser") Member loginUser,
								Model model,
								@PathVariable("chatRoomNo") int chatRoomNo,
								ChatRoomJoin join,
								RedirectAttributes ra
													) {
		join.setUserNo(loginUser.getUserNo());
		List<ChatMessage> list = service.joinChatRoom(join);
		
		model.addAttribute("list", list);
		model.addAttribute("chatRoomNo", chatRoomNo); // session스코프에 chatRoomNo저장됨.
		
		return "chat/chatRoom";
	}
	
	@GetMapping("/chat/exit")
	@ResponseBody
	public int exitChatRoom(@ModelAttribute("loginUesr") Member loginUser, 
			                ChatRoomJoin join) {
		return service.exitChatRoom(join);
	}
}
