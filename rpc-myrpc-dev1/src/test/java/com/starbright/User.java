package com.starbright;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: user对象
 * @author: Star Bright
 * @date: 2024/9/14 14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = 2208615557286955430L;

	private String name;

	private String password;

	private int age;

	private String address;

	private String tel;

	private String email;

	public User(String name) {
		this.name = name;
	}
}
