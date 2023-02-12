package com.kh.spring.chat.model.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

@Repository
public class ChatDao {
	
	public List<ChatRoom> selectChatRoomList(SqlSession sqlSession){
		return sqlSession.selectList("chattingMapper.selectChatRoomList");
	}
	
	public int openChatRoom(SqlSession sqlSession, ChatRoom chatRoom) {
		int result = sqlSession.insert("chattingMapper.openChatRoom", chatRoom);
		
		if(result > 0) {
			return chatRoom.getChatRoomNo();
		}else {
			return 0;
		}
	}
	
	public int insertMessage(SqlSession sqlSession, ChatMessage cm) {
		return sqlSession.insert("chattingMapper.insertMessage", cm);
	}
	
	// 채팅방 참여 여부 확인 / 참여: 1, 미참여 : 0
	public int joinCheck(SqlSession sqlSession, ChatRoomJoin join) {
		return sqlSession.selectOne("chattingMapper.joinCheck", join);
	}
	
	// 채팅방 참여하는 메서드
	public void joinChatRoom(SqlSession sqlSession, ChatRoomJoin join) {
		sqlSession.insert("chattingMapper.joinChatRoom", join);
	}
	
	//채팅메세지 목록 조회
	public List<ChatMessage> selectChatMessage(SqlSession sqlSession, int chatRoomNo){
		return sqlSession.selectList("chattingMapper.selectChatList", chatRoomNo);
	}
	
	public int exitChatRoom(SqlSession sqlSession, ChatRoomJoin join) {
		return sqlSession.delete("chattingMapper.exitChatRoom", join);
	}
	
	public int countChatRoomMember(SqlSession sqlSession, int chatRoomNo) {
		return sqlSession.selectOne("chattingMapper.countChatRoomMember", chatRoomNo);
	}
	
	public int closeChatRoom(SqlSession sqlSession, int chatRoomNo) {
		return sqlSession.update("chattingMapper.closeChatRoom", chatRoomNo);
	}
}
