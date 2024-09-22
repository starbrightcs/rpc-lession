package com.starbright.discovery.impl;

import com.starbright.discovery.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @description: 随机获取
 * @author: Star Bright
 * @date: 2024/9/22 11:07
 */
public class RandomLoadBalance implements LoadBalance {

	@Override
	public String select(List<String> urls) {
		int index = new Random().nextInt(urls.size());
		return urls.get(index);
	}

}
