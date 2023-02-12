package com.kh.spring.chat.model.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ChatMessage {
	private int cmNo;
	private String message;
	private Timestamp createDate;
	private int chatRoomNo;
	private int userNo;
	private String userName;
}
