package org.hobbit.core.service.docker.impl.spring_boot.util;

import java.util.Map;
import java.util.function.Supplier;

import org.hobbit.core.service.docker.impl.core.DockerServiceBuilderFactory;
import org.hobbit.core.service.docker.impl.spring_boot.ServiceSpringApplicationBuilder;
import org.hobbit.sdk.docker.registry.DockerServiceRegistry;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class DockerServiceRegistrySpringBootUtils {
	public static DockerServiceRegistry registerSpringApplications(DockerServiceRegistry registry, Map<String, Supplier<SpringApplicationBuilder>> map) {
		Map<String, DockerServiceBuilderFactory<?>> m = ServiceSpringApplicationBuilder.convert(map);

		registry.getServiceFactoryMap().putAll(m);
		return registry;
	}

}
