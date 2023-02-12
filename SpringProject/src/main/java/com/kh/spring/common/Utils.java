package com.kh.spring.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	// 변경된 이름을 돌려주면서 원본파일을 변경된 파일이름으로 서버에 저장시키는 메서드
	static public String saveFile(MultipartFile upfile, String savePath) {
		// ex) dog.jpg -> "2022121316232312"
		//1. 원본 파일명 뽑기
		String originName = upfile.getOriginalFilename();// "dog.jpg"
		
		//2. 시간 형식을 문자열로 뽑아오기
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		//3. 뒤에 붙을 5자리 랜덤값 뽑기
		int random = (int)(Math.random() * 90000 + 10000);
		
		//4. 원본파일명으로부터 확장자명 뽑기.
		String ext = originName.substring(originName.lastIndexOf("."));
		
		//5. 다 이어붙이기
		String changeName = currentTime + random + ext;
		
		//6. 업로드 하고자하는 물리적인 위치 알아내기
		
		
		// 7. 경로와 수정파일명을 합쳐서 업로드하기
//        try {
//           upfile.transferTo(new File(savePath+changeName));
//        } catch (IllegalStateException | IOException e) {
//           logger.error("에러남");
//           //e.printStackTrace();
//        }      
        
        return changeName;
	}
	
	// 크로스 사이트 스크립트 공격을 방지하기 위한 메소드
	public static String XSSHandling(String content) {
		if(content != null) {
			// <script> -> script
			content = content.replaceAll("&", "%amp;");
			content = content.replaceAll("<", "&lt;");
			content = content.replaceAll(">", "&gt;");
			content = content.replaceAll("\"", "&quot;");
		}
		return content;
	}
	
	// 개행문자 처리
	public static String newLineHandling(String content) {
		return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
	}
	
	// 개행문자 해체
	public static String newLineClear(String content) {
		return content.replaceAll("<br>", "\n");
	}
}
