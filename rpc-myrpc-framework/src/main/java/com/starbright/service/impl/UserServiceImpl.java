package com.starbright.service.impl;

import com.starbright.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/24 22:51
 */
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public boolean login(String username, String password) {
		log.info("login method invoke, username:{}, password:{}", username, password);
		return false;
	}

}
