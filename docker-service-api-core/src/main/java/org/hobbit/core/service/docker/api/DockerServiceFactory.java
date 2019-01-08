package org.hobbit.core.service.docker.api;

import java.util.Map;

/**
 * In contrast to the builder, the service factory only provides a single create method.
 * 
 * @author raven Nov 18, 2017
 *
 */
public interface DockerServiceFactory<T extends DockerService>
	extends AutoCloseable
{
	T create(String imageName, Map<String, String> env);
}
