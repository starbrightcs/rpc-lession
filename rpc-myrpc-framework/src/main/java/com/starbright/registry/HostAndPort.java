package com.starbright.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 注册的节点信息
 * @author: Star Bright
 * @date: 2024/9/24 14:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostAndPort {

	private String hostName;

	private int port;

}
