package com.starbright.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/2 15:30
 */
@Slf4j
public class UserServiceImpl implements UserService {

	@Override
	public boolean login(String username, String password) {
		log.debug("login method invoke username:{} password:{}", username, password);
		return true;
	}

	@Override
	public void register(User user) {
		log.debug("register method invoke user: {}", user);
	}
}
