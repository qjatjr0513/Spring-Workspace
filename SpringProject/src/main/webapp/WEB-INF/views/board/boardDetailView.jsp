<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	table * {margin:5px}
	table {width:100%}
</style>
</head>
<body>
	<jsp:include page="../common/header.jsp"/>
	
	<div class="content">
		<br><br>
		<div class="innerOuter">
			<h2 align="center">게시판 상세보기</h2>
			<br>
			
			<a class="btn btn-secondary" style="float:right;" href="${contextPath }/board/list/${b.boardCd }">목록으로</a>
			<br><br>
			
			<table id="contentArea" align="center" class="table">
				<tr>
					<th width="100">제목</th>
					<td colspan="3">${b.boardTitle }</td>
				</tr>
				<tr>
					<th>첨부파일</th>
					<td colspan="3">
						<a href="${contextPath }/${b.changeName}" download="${b.originName }">${b.originName }</a>
					</td>
				</tr>
				<c:if test="${!empty b.imgList }">
					<c:forEach var="i" begin="0" end="${fn:length(b.imgList) -1 }">
						<tr>
							<th>이미지${i+1 }</th>
							<td colspan="3">
							<img src="${contextPath}/resources/images/boardT/${b.imgList[i].changeName}">
							<a href="${contextPath}/resources/images/boardT/${b.imgList[i].changeName}"
							download="${b.imgList[i].originName}">다운로드</a>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				<tr>
					<th>내용</th>
					<td colspan="3">
					
					</td>
				</tr>
				<tr>
					<td colspan="4"><p style="height:150px;">${b.boardContent }</p></td>
				</tr>
			</table>
			<br>
			<c:if test="${not empty loginUser && loginUser.userId eq b.boardWriter }">
			<div align="center">
				<!-- 수정하기, 삭제하기 버튼은 이글이 본인이 작성한 글인 경우에만 보여져야한다. -->
				<a class="btn btn-primary" href="${contextPath}/board/enrollForm/${boardCode}?mode=update&bno=${b.boardNo}">수정하기</a>
				<a class="btn btn-danger" href="${contextPath}/board/delete.bo?bno=${b.boardNo }">삭제하기</a>
			</div>
			</c:if>
			<br><br>
			
			<!-- 댓글등록기능 -->
			<table id="replyArea" class="table" align="center">
				<thead>
					<tr>
						<th colspan="2">
							<textarea class="form-control" name="replyContent" id="replyContent" rows="2" cols="55"
							style="resize:none; width:100%"></textarea>
						</th>
						<th style="vertical-align: middle;"><button class="btn btn-secondary" onclick="insertReply();">등록하기</button></th>
					</tr>
					<tr>
						<td colspan="3">댓글(<span id="rcount"></span>)</td>
					</tr>
				</thead>
				<tbody>
				<!-- 스크립트 구문으로 댓글 추가 -->
					<script>
					$(function(){
						selectReplyList();
						
						//setInterval(selectReplyList , 1000);
					});
				
					function insertReply(){
						$.ajax({
							url : "insertReply.bo",
							data : {
								refBno     : '${b.boardNo}',
								replyContent : $("#replyContent").val()
							},
							type : "post",
							success : function(result){
									$("#replyContent").val("");
								if(result == "1"){ // 댓글등록 성공 => 갱신된 댓글리스트 조회
									alertify.alert("서비스 요청 성공", '댓글 등록 성공');
								}else{
									alertify.alert("서비스 요청 실패", '댓글 등록 실패');
								}
								selectReplyList();
							}, complete : function(){
								$("#replyContent").val("")
							}
						})
					}
					
					function selectReplyList(){
						$.ajax({
							url : "reply.bo",
							data : {bno : ${b.boardNo}},
							dataType: 'json',      // 호출 했을 때 결과타입
							contentType: "application/json",
							success : (rList) => {
								console.log(rList)
								let result = ""; // result 에 문자열로 꾸며준 data 하나하나 넣고 html사용.
								for(let r of rList){ // list를 반복문 돌릴때 of 사용. // of => 배열에서 [1,2,3]가 있으면 1꺼내서 i에 저장 => 순차적으로 꺼내서 저장.
									result += "<tr>"
													+ "<td>"+r.member.userId+"</td>"
													+ "<td>"+r.replyContent+"</td>"
													+ "<td>"+r.createDate+"</td>"
										   + "</tr>";
								}
								$("#replyArea tbody").html(result);
								$("#rcount").html(rList.length);
							},
							error : function(){
								console.log("댓글리스트조회용 ajax통신 실패~");
							}
						});
					}
					
					</script>

					<script>
// 					function deleteBoard(){
// 						$.ajax({
// 							url : "delete.bo",
// 							data : {bno : ${b.boardNo}},
// 							dataType: 'json',      // 호출 했을 때 결과타입
// 							contentType: "application/json",
// 							success : function(result){
// 								$("#replyContent").val("");
// 							if(result == "1"){ // 댓글등록 성공 => 갱신된 댓글리스트 조회
// 								movePage();
// 							}else{
// 								alertify.alert("서비스 요청 실패", '게시글 삭제 실패');
// 							}
// 						}
// 					})
// 				}
					
					function movePage(){
				 		location.href = '${contextPath}/board/boardListView';
				 	}
					</script>
				</tbody>
			</table>
			
		</div>
	</div>
	
		
	
	<jsp:include page="../common/footer.jsp"/>
</body>
</html>