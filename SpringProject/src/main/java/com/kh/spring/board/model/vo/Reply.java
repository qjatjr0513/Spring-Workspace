package com.kh.spring.board.model.vo;

import java.sql.Date;

import com.kh.spring.member.model.vo.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reply {
	private int replyNo;
	private String replyContent;
	private int refBno;
	private int replyWriter;
	private Date createDate;
	private String status;
	private Member member;
	
	
	
}
