package com.starbright.service;

import com.starbright.User;
import com.starbright.UserService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 服务接口实现类
 * @author: Star Bright
 * @date: 2024/9/3 11:55
 */
public class UserServiceImpl implements UserService.Iface {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public User queryUserByNameAndPassword(String name, String password) throws TException {
		log.info("UserServiceImpl.queryUserByNameAndPassword invoke name: {}, password: {}", name, password);
		return new User(name, password);
	}

	@Override
	public void save(User user) throws TException {
		log.info("UserServiceImpl.save(user) invoke name: {}, password: {}", user.getName(), user.getPassword());
	}

}
