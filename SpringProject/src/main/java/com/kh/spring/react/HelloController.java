package com.kh.spring.react;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/test")
	public List<String> Hello(){
		String springServerPort = "8081";
		String reactServerPort = "3000";
		return Arrays.asList(springServerPort, reactServerPort);
		
		
	}
}
