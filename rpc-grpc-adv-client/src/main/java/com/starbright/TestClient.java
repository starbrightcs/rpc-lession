package com.starbright;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: 客户端使用consul
 * @author: Star Bright
 * @date: 2024/9/11 15:53
 */
public class TestClient {

	private static final Logger log = LoggerFactory.getLogger(TestClient.class);

	public static void main(String[] args) {
		// 获取consul连接
		Consul consulConnection = Consul.builder().build();
		// 获取健康的client，和服务端有区别，服务端那边只是获取agentClient，也就是普通的client
		HealthClient healthClient = consulConnection.healthClient();
		// 根据服务名称获取健康的服务，需要注意这个服务名称是和服务端那边的name名称一致
		ConsulResponse<List<ServiceHealth>> healthyServiceInstances = healthClient.getHealthyServiceInstances("grpc-server");
		List<ServiceHealth> serviceHealthList = healthyServiceInstances.getResponse();
		serviceHealthList.forEach(serviceHealth -> log.info("service: {}", serviceHealth.getService()));
	}

}
