package com.starbright;

import com.starbright.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/27 12:34
 */
public class SpringClient {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-client.xml");
		UserService userService = applicationContext.getBean(UserService.class);
		boolean result = userService.login("starbright", "123456");
		System.out.println("result = " + result);
	}

}
