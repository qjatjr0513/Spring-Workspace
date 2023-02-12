package com.kh.spring.common.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;

@Component
public class FileDeleteScheduler {
   
   // sysout 쓰지않기위해 logger 등록.
   private Logger logger = LoggerFactory.getLogger(FileDeleteScheduler.class);
   
   // 1. BOARD테이블 안에있는 이미지목록들 조회하여
   // 2. resources/uploadFiles디렉토리안에 있는 이미지들과 대조하여
   // 3. 일치하지 않는 이미지파일을 삭제
   //    (db에는 없는데이터인데 uploadFiles안에는 존재하는경우)
   // 4. 5초간격으로 테스트후 정상이라면 , 정시마다 실행되는 크론표현식으로 변경하기.
   
   @Autowired
   private BoardService service;
   
   @Autowired
   private ServletContext application; // 서버폴더경로를 얻어오기위해 사용.
   
   @Scheduled(fixedDelay = 5000)
   public void deleteFile() {
      
      logger.info("파일삭제 시작");

      
      // 1) board테이블안에있는 모든 파일목록 조회.
      List<String> file = service.selectFileList();
      
      // 2) resources/uploadFiles폴더 아래에 존재하는 모든 이미지 파일 목록 조회.
      File path = new File(application.getRealPath("/resources/uploadFiles"));
      File[] files = path.listFiles(); // path참조하고있는 폴더에 들어가있는 모든파일을 얻어와서 File배열로 반환해주는 녀석
      List<File> fileList = new ArrayList<>(Arrays.asList(files)); // 컬렉션 객체로 만들어줌
      
  
      // 3) 두 목록을 비교해서 일치하지 않는 파일 삭제
      if(!file.isEmpty()) {
    	  // server : C:Spring-workspace .. xxx.jpg
    	  // list : xxx.jpg
    	  for(File serverFile : fileList) {
    		  
    		  String fileName = serverFile.getName(); // 파일명만 얻어오는 메서드
    		  //server : xxx.jpg
        	  //list   : xxx.jpg
    		  
    		  // List.indexOf(value) : List에 value과 같은 값이 있으면 인덱스를 반환 / 없으면 -1을 반환
    		  
    		  if(file.indexOf(fileName) == -1) {
    			  
    			  // select 해온 db목록에는 없는데, 실제 server에는 저장된 파일인 경우
    			  logger.info(serverFile.getName()+"이거 삭제함.");
    			  serverFile.delete();
    		  }
    	  }
    	  
    	  logger.info("서버파일 삭제작업 끝.");
    	  
//    	  for(int i= 0; i<fileList.size(); i++) {
//    		  String str = fileList.get(i).getName();
//    		  if(!file.contains(str)) {
//    			  File delFile = new File(path + File.separator + str); // File.separator  /을 \로 구분해줌 
//    			  logger.info(delFile.toString());
//    			  delFile.delete();
//    		  }
//    	  }    	  
      }

   }
}