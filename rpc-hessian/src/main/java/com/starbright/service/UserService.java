package com.starbright.service;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/2 15:27
 */
public interface UserService {

	boolean login(String username, String password);

	void register(User user);

}
