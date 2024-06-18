package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Test;
import com.datacurationthesis.datacurationthesis.repository.TestRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class TestService {

	@Autowired
	private TestRepository testRepository;

	public List<Test> getAllTests() {
		List<Test> tests = testRepository.findAll();
		return tests;
	}

	public Test saveTest(Test test) {
		return testRepository.save(test);
	}

}
