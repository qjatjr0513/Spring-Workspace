package com.kh.spring.board.controller;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.common.Utils;
import com.kh.spring.common.Template.Pagination;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.service.BoardServiceImpl;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.member.model.vo.Member;


@Controller
@RequestMapping("/board")
// 클래스에서도 리퀘스트매핑 추가가능.
// 그럼 현재 컨트롤러는 /spring/board의 경로로 들어오는 모든 url요청을 받아준다.
public class BoardController {
	
	//private BoardServiceImpl boardServiceImpl;
	private BoardService boardService;
	
	
	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@RequestMapping("/list/{boardCode}")
	// @PathVariable("value") : URL 경로에 포함되어있는 값을 변수로 사용할수 있게 해줌. -> 자동으로 requestScope에 저장됨. -> jsp에서도 el 태그로 사용가능.
	
	// /spring/board/list/C
	// /spring/board/list/T
	// /spring/board/list/N 
	// /spring/board/list/M
	public String selectList(
			@PathVariable("boardCode") String boardCode,
			@RequestParam(value = "cpage", defaultValue = "1") int currentPage, Model model,
			@RequestParam Map<String, Object> paramMap
	// 검색요청이 들어왔다면 paramMap 안에는 keyword, conditon
	) {
		Map<String, Object> map = new HashMap();
		
		if (paramMap.get("condition") == null) { // 검색요청을 하지 않은 경우

			// 2. 게시글 select
		   map = boardService.selectList(currentPage, boardCode);

		} else { // 검색요청을 한 경우
			// 검색에 필요한 데이터를 paramMap을 넣어서 호출
			// condition, keyword

			paramMap.put("cpage", currentPage);
			paramMap.put("boardCode", boardCode);
			
			// 2. 게시글 select
			map = boardService.selectList(paramMap);

		}
		
		// 3. 페이지 포워딩(pi 객체와 list객체 저장시키면서)
		model.addAttribute("map", map);
		return "board/boardListView";
	}
	
	@RequestMapping("/enrollForm/{boardCode}")
	public String boardEnrollForm(
									@PathVariable("boardCode") String boardCode,
									Model model,
									@RequestParam(value="mode", required = false, defaultValue="insert") String mode,
									@RequestParam(value="bno", required = false, defaultValue="0") int bno
									) {
		
		if(mode.equals("update")) {
			// 수정하기 페이지 요청.
			try {
				Board b = boardService.selectBoard(bno);
				
				// 개행문자가 <br> 태그로 치환되어있는 상태
				// textarea에 출력할것이기 때문에 \n으로 변경해줌.
				b.setBoardContent(Utils.newLineClear(b.getBoardContent()));
				
				model.addAttribute("b", b);
			} catch (Exception e) {
				
				logger.error("selectBoard 메서드 에러");
			}
			
		}
		
		if(boardCode.equals("C")) {
			return "board/boardEnrollForm";			
		}else {
			return "board/boardPictureEnrollForm";		
		}
			
		
		
	}
	
	@RequestMapping("/insert/{boardCode}")
	public String insertBoard(Board b,
			                  @RequestParam(value="images", required = false) List<MultipartFile> imgList, // 업로드용 이미지
			                  MultipartFile upfile, // 첨부파일
			                  @PathVariable("boardCode") String boardCode,
			                  HttpSession session, 
			                  Model model,
			                  @RequestParam(value="mode", required=false, defaultValue= "insert") String mode,
			                  @RequestParam(value="deleteList", required=false) String deleteList) {
		
		// 사진게시판 이미지들 저장 경로 얻어오기
		String webPath = "/resources/images/boardT/";
		String serverFolderPath  = session.getServletContext().getRealPath(webPath);
		b.setBoardCd(boardCode);
		int result = 0;
		// 첨부파일을 선택하고 안하고 상관없이 객체는 생성됨(upfile) 
		// 전달된 파일이 있는지 없는지는 filename으로 첨부파일 유무 확인. 
		//전달된 파일이 있을경우 -> 파일명 수정 작업후 서버업로드 -> 원본 파일명과, 수정된 파일명, 서버에 업로드된 경로를 b 객체 안에 담기
		if(!upfile.getOriginalFilename().equals("")) { // 파일명이 비어있지 않은 경우
			String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/");
			String changeName = Utils.saveFile(upfile, savePath);
			
			try {
				upfile.transferTo(new File(savePath+changeName));
			} catch (IllegalStateException | IOException e) {
				logger.error(e.getMessage());
			}
			
			b.setChangeName("resources/uploadFiles/"+changeName);
			b.setOriginName(upfile.getOriginalFilename());
			
		}
		if(mode.equals("insert")) {
			//db에 board테이블에 데이터 추가
			try {
				result = boardService.insertBoard(b, imgList, webPath, serverFolderPath);			
			} catch(Exception e) {
				logger.error("에러발생");
			}
		} else { // 수정
			
			// 게시글 수정 서비스 호출
			// b객체 안에 boardNo
			try {
				result = boardService.updateBoard(b, imgList, webPath, serverFolderPath, deleteList);
			} catch (Exception e) {
				logger.error("에러발생");
			}
			
		}
		
		// 성공적으로 추가시
		// alertMsg  게시글을 작성하셨습니다. -> session
		// list.bo
		if(result > 0) {
			session.setAttribute("alertMsg", "게시글을 작성에 성공하셨습니다.");
			return "redirect:../list/"+boardCode;
		}else {
			// 실패시 
			// errorMsg 게시글 작성 실패 -> request
			// errorPage
			model.addAttribute("errorMsg", "게시글 작성 실패");
			return "common/errorPage";
		}
		
	}
	
	
	@RequestMapping("/detail/{boardCode}/{boardNo}")
	public ModelAndView selectBoard(@PathVariable("boardCode") String boardCode, 
			                        @PathVariable("boardNo") int boardNo, 
			                        HttpSession session,
			                        ModelAndView mv,
			                        HttpServletRequest req,
			                        HttpServletResponse res) {
		
		Board b = null;
		try {
			b = boardService.selectBoard(boardNo);
		} catch (Exception e) {
			logger.error("에러남");
		}
		
		// 쿠키를 이용해서 조회수 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수가 증가되지 않도록.
		if(b != null) { // 상세 조회 성공.
			
			
			
			Member loginUser = (Member)session.getAttribute("loginUser");
			
			int userNo = 0;
			if(loginUser != null) {
				userNo = loginUser.getUserNo();
			}
			
			// 작성자의 번호와 현재 세션의 유저번호가 같지 않을때만 조회수가 증가
			if(!b.getBoardWriter().equals(userNo+"")) {
				
				Cookie cookie = null; // 기존애 존재하던 쿠키를 저장하는 변수
				
				Cookie[] cArr = req.getCookies(); // 쿠키얻기
				
				if(cArr != null && cArr.length > 0) {
					
					for(Cookie c : cArr ) {
						if(c.getName().equals("readBoardNo")) {
							cookie = c;
							break;
						}
					}
				}
				
				int result = 0;
				if(cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없다.
					// 쿠키 생성
					cookie = new Cookie("readBoardNo", boardNo+"");
					// 조회수 증가서비스 호출
					//1. 조회수 증가시키는 서비스.
					result =  boardService.increaseCount(boardNo);					
				}else {
					// 기존에 readboardNo라는 이름의 쿠키가 있다. -> 쿠키에 저장된 값 뒤쪽에 현재 조회된 게시글 번호를 추가
					// 										   단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가.
					
					String[] arr = cookie.getValue().split("/");
					
					// "readBoardNo" : 1/2/5/14/103/115 -> ['1', '2', '5', '14', '103', '115']
				
					List<String> list = Arrays.asList(arr); // 컬렉션으로 변환
					
					// "100".indexOf("100") -> 0
					// "100".indexOf("10")  -> 0
					// "100".indexOf("1")   -> 0
					
					// List.indexOf(Object) : 
					// - list안에서 매개변수로 들어온 Object와 일치하는 부분의 인덱스 반환
					// - 일치하는 부분이 없으면 -1반환
					if(list.indexOf(boardNo+"") == -1) { // 기존값에 같은 글번호가 없다.
						cookie.setValue(cookie.getValue()+"/"+boardNo);
						result =  boardService.increaseCount(boardNo);	
					}
				}
				
				if(result >0) { // 성공적으로 조회수가 증가되었다.
					b.setCount(b.getCount()+1);
					
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간(60초 * 60분 * 1시간 )
					res.addCookie(cookie);
					// 상세보기할 정보를 조회
					
					
				
				} 
			}
			mv.addObject("b", b);
			mv.setViewName("board/boardDetailView");
		}else {
			mv.addObject("errorMsg", "게시글 상세조회 실패");
			mv.setViewName("common/errorPage");
		}	
		
		return mv;
	}
	
	// 댓글 목록 불러오기
	@RequestMapping("reply.bo")
	@ResponseBody// @ResponseBody : 별도의 뷰페이지가 아니라 리턴값을 직접 지정해야 하는 경우 사용.
	public String selectReplyList(int bno) {
		// 댓글 목록 조회
		ArrayList<Reply> list = boardService.selectReplyList(bno);
		//gson으로 파싱
		Gson gson = new GsonBuilder().create();
		// [reply, reply, reply]
		
		String rList = gson.toJson(list);
		return rList;
	}
	
	
	@RequestMapping("insertReply.bo")
	@ResponseBody
	public String insertReply(Reply r, HttpSession session) {
		Member m = (Member)session.getAttribute("loginUser");
		if(m != null) {
			r.setReplyWriter(m.getUserNo());
		}
		
		int result = boardService.insertReply(r);
		
		if(result > 0) {
			return "1";
		}else {
			return "0";
		}
		
	}
	
//	@RequestMapping("search.bo")
//	public String  selectSearchList(String keyword, String condition, @RequestParam(value="cpage",defaultValue="1") int currentPage, Model model) {
//		// 1.  페이징 처리 작업.
//		
//		int pageLimit = 10;
//		int boardLimit = 5;
//		
//		HashMap<String, String> map = new HashMap();
//		map.put("condition", condition);
//		map.put("keyword", keyword);
//		
//		int listCount = boardService.selectSearchCount(map);
//		
//		PageInfo pi = pn.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
//		
//		ArrayList<Board> list = boardService.selectSearchList(map, pi);
//		
//		model.addAttribute("list", list);
//		model.addAttribute("pi", pi);
//		model.addAttribute("condition", condition);
//		model.addAttribute("keyword", keyword);
//		return "board/boardListView";
//	}
	
	@RequestMapping("delete.bo")
	public String deleteBoard(int bno, HttpSession session, @PathVariable("boardCode") String boardCode) {
		int result =  boardService.deleteBoard(bno);
		
		if(result == 1) { // 성공적으로 게시글이 삭제되었다.
			session.setAttribute("alertMsg", "게시글을 삭제에 성공하셨습니다.");
			return "redirect:../list/"+boardCode;
		} else {
			session.setAttribute("errorMsg", "게시글 삭제 실패");
			return "common/errorPage";
		}
	}
	
	@RequestMapping("updateForm.bo")
	public ModelAndView updateForm(int bno, ModelAndView mv) {
		
		Board b = null;
		try {
			b = boardService.selectBoard(bno);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		mv.addObject("b", b);
		mv.setViewName("board/boardUpdateForm");
		
		return mv;
	}
	
	@Autowired
	private ServletContext application;
	
//	@RequestMapping("update.bo")
//	public String updateBoard(int bno, String boardTitle,MultipartFile upfile, String boardContent, HttpSession session, Model model, @PathVariable("boardCode") String boardCode) {
//		Board b = new Board();
//		b.setBoardNo(bno);
//		b.setBoardTitle(boardTitle);
//		b.setBoardContent(boardContent);
//		
//		if(!upfile.getOriginalFilename().equals("")) {
//			String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/");
//			String changeName = Utils.saveFile(upfile, savePath);
//			b.setChangeName("resources/uploadFiles/"+changeName);
//			b.setOriginName(upfile.getOriginalFilename());
//			
//			if(upfile.getName() != null && !upfile.getName().equals("") ) {
//				File path = new File(application.getRealPath("/resources/uploadFiles"));
//				new File(path+upfile.getOriginalFilename()).delete();
//			}
//			
//		}
//		
//		int result = boardService.updateBoard(b);
//		if(result > 0) {
//			session.setAttribute("alertMsg", "게시글을 수정에 성공하셨습니다.");
//			return "redirect:../list/"+boardCode;
//		}else {
//			// 실패시 
//			// errorMsg 게시글 작성 실패 -> request
//			// errorPage
//			model.addAttribute("errorMsg", "게시글 수정 실패");
//			return "common/errorPage";
//		}
//	}
	
	
}
