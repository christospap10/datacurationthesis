package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Test;
import com.datacurationthesis.datacurationthesis.service.TestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;





@RestController
@RequestMapping("/api/datacuration/test")
public class TestController {

	@Autowired
	private TestService testService;

	@GetMapping("/all")
	public List<Test> getAllTests() {
		return testService.getAllTests();
	}

	@PostMapping("/savetest")
	public Test addTest(@RequestBody Test test) {
		return testService.saveTest(test);
	}
	
	
}
