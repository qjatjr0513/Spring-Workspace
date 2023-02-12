<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<style>
	.chatting-area{
		margin : auto;
		height : 600px;
		width : 800px;
		margin-top : 50px;
		margin-bottom : 50px;
	}
	#exit-area{
		text-align: right;
		margin-bottom: 10px;
	}
	.display-chatting{
		width:100%;
		height:450px;
		border : 1px solid pink;
		overflow : auto;
		list-style : none;
		padding : 10px 10px;
	}
	.chat{
		display : inline-block;
		border-radius : 5px;
		padding: 5px;
		background-color : #eee;
	}
	.input-area{
		width:100%;
		display:flex;
	}
	#inputChatting{
		width:80%;
		resize:none;
	}
	#send{
		width:20%;
	}
	.myChat{
		text-align : right;
	}
	.myChat > p{
		background-color : yellow;
	}
	.chatDate{
		font-size : 10px;
	}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
	
	<div class="chatting-area">
		<div id="exit-area">
			<button class="btn btn-outline-danger" id="exit-btn">나가기</button>
		</div>
		<ul class="display-chatting">
			<c:forEach items="${list }" var="msg">
				<fmt:formatDate var="chatDate" value="${msg.createDate }" pattern="yyyy년 MM월 dd일 HH:mm:ss"/>
				
				<%-- 1) 내가 보낸 메세지 --%>
				<c:if test="${msg.userNo == loginUser.userNo}">
					<li class="myChat">
						<span class="chatDate">${chatDate }</span>
						<p class="chat">${msg.message }</p>
					</li>
				</c:if>
				
				<%-- 2) 남(이름)이 보낸 메세지 --%>
				<c:if test="${msg.userNo != loginUser.userNo}">
					<li class="">
						<b>${msg.userName }</b>
						<p class="chat">${msg.message }</p>
						<span class="chatDate">${chatDate }</span>
					</li>
				</c:if>
				
			</c:forEach>
		</ul>
		
		<div class="input-area">
			<textarea id="inputChatting" rows="3"></textarea>
			<button id="send">보내기</button>
		</div>
		
	</div>
	
	<br><br>
	
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
	<!-- sockjs  -->
	<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>

	
	<script>
	
		//el 태그 통해 js변수 셋팅
		const userNo = "${loginUser.userNo}";
      	const userEmail = "${loginUser.email}";
      	const userName = "${loginUser.userName}";
      	const chatRoomNo = "${chatRoomNo}";
      	const contextPath = "${contextPath}";

	
		// /chat이라는 요청주소로 통신할 수 있는 webSocket 객체 생성 --> /spring/chat
		
		let chatSocket = new SockJS(contextPath+"/chat");
		//-> websocket 프로토콜을 이용해서 해당 주소로 데이터를 송/수신 할 수 있다.
		
		/*
		   webSocket
		  
		  - 브라우저와 웹 서버간의 전이중 통신을 지원하는 프로토콜
		  
		  * 전이중 통신(full duplex) : 두 대의 단말기가 데이터를 동시에 송/수신하기 위해 각각 독립된 회선을 사용하는 통신 방식
		  
		  * HTML5, JAVA7버전 이상, SPRING 4버전 이상에서 지원.
		*/
	</script>
	
	<script src="${contextPath }/resources/js/chat.js"></script>
</body>
</html>