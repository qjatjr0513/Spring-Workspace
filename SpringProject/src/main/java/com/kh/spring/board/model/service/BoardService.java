package com.kh.spring.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;

public interface BoardService {
	// 게시글 리스트 조회
	public int selectListCount(String boardCode);
	
	public int selectListCount(Map<String, Object> paramMap);
	
	public Map<String, Object> selectList(int currentPage, String boardCode);
	
	public int insertBoard(Board b, List<MultipartFile> list, String webPath, String serverFolderPath) throws Exception;
	
	public int increaseCount(int bno);
	
	public Board selectBoard(int bno) throws Exception;
	
//	public Board selectBoard(int bno);
	
	public ArrayList<Reply> selectReplyList(int bno);
	
	public int insertReply(Reply r);
	
	public int selectSearchCount(HashMap<String, String> map);
	
	public List<String> selectFileList();
	
	public ArrayList<Board> selectSearchList(HashMap<String, String> map, PageInfo pi);
	
	public int deleteBoard(int bno);
	
//	public  int updateBoard(Board b);
	
	public int updateBoard(Board b, List<MultipartFile> list, String webPath, String serverFolderPath, String deleteList) throws Exception;

	Map<String, Object> selectList(Map<String, Object> paramMap);
	
	public List<BoardType> selectBoardTypeList();
	
	
	
}
