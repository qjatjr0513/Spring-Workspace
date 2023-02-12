<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	#updateForm>table{width:100%;}
	#updateForm>table *{margin:5px;}
</style>
</head>
<body>
	
	<jsp:include page="../common/header.jsp"/>
	
	<div class="content">
		<br><br>
		<div class="innerOuter">
			<h2>게시글 수정하기</h2>
			<br>
			
			<form id="updateForm" action="update.bo?bno=${b.boardNo }" enctype="multipart/form-data" method="post">
				<table align="center">
					<tr>
						<th><label for="title">제목</label></th>
						<td><input type="text" id="title" class="form-control" name="boardTitle" value="${b.boardTitle }" required></td>
					</tr>
					<tr>
						<th><label for="writer">작성자</label></th>
						<td><input type="text" id="writer" class="form-control" value="${loginUser.userNo }" name="boardWriter" readonly></td>
					</tr>
					<tr>
						<th><label for="upfile">첨부파일</label></th>
						<td>			
							<input type="hidden" name="originName" value="${b.originName }">
	               			<input type="hidden" name="changeName" value="${b.changeName }">
	               			<input type="file" id="upfile" class="form-control" name="upfile">
						</td>
					</tr>
					<tr>
						<th><label for="content">내용</label></th>
						<td><textarea id="content" style="resize:none;" rows="10" class="form-control" name="boardContent" required="required">${b.boardContent }</textarea></td>
					</tr>
				</table>
				<br>
				
				<div align="center">
				<button type="submit" class="btn btn-primary">등록하기</button>
					<button type="reset" class="btn btn-danger">취소하기</button>
				</div>
			</form>
		</div>
		
	</div>
	
	
	
	
	<jsp:include page="../common/footer.jsp"/>
	
</body>
</html>