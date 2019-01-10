package org.hobbit.core.service.docker.api;

import java.util.Map;

public interface DockerServiceSpecLike<F extends DockerServiceSpecLike<?>> {
	String getContainerName();
	F setContainerName(String containerName);
	
	String getImageName();
	F setImageName(String imageName);
	
	Map<String, String> getLocalEnvironment();
	F setLocalEnvironment(Map<String, String> localEnvironment);
}
