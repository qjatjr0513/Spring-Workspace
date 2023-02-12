package com.kh.spring.board.model.vo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.kh.spring.member.model.vo.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriter;
	private int count;
	private Date createDate;
	private String status;
	
	private String originName; // ORIGIN_NAME VARCHAR2(100BYTE)
	private String changeName; // CHANGE_NAME VARCHAR2(100BYTE)
	private String boardCd;
	
	private ArrayList<Reply> list;
	
	private ArrayList<BoardImg> imgList;
}
