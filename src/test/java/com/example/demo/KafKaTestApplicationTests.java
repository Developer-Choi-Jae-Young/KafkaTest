package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafKaTestApplicationTests {
	@Autowired
	private TestProducer testProducer;

	@Test
	void contextLoads() {
	}

	@Test
	void test() {
		testProducer.create();
	}
}
