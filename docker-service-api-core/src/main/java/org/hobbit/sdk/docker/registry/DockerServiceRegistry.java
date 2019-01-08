package org.hobbit.sdk.docker.registry;

import java.util.Map;

import org.hobbit.core.service.docker.impl.core.DockerServiceBuilderFactory;

public interface DockerServiceRegistry {
	Map<String, DockerServiceBuilderFactory<?>> getServiceFactoryMap();

//	default DockerServiceFactory<?> asDockerServiceFactory() {
//		
//	}
}
