package com.starbright;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/27 12:15
 */
public class SpringServer {

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-server.xml");

		System.in.read();
	}

}
