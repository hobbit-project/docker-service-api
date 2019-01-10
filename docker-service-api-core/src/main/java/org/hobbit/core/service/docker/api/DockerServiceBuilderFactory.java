package org.hobbit.core.service.docker.api;

public interface DockerServiceBuilderFactory<T extends DockerService>
{
	DockerServiceBuilder<T> newServiceBuilder();
}
