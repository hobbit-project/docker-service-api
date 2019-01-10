package org.hobbit.core.service.docker.impl.core;

import java.util.Map;

import org.hobbit.core.service.docker.api.DockerService;
import org.hobbit.core.service.docker.api.DockerServiceBuilder;
import org.hobbit.core.service.docker.api.DockerServiceFactory;
import org.hobbit.core.service.docker.api.DockerServiceSpec;
import org.hobbit.core.service.docker.api.DockerServiceSpecLike;

/**
 * 
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 * @param <T>
 */
public class DockerServiceBuilderImpl<T extends DockerService>
	extends DockerServiceSpecLikeForwarding<DockerServiceBuilder<T>>
	implements DockerServiceBuilder<T>
{
	protected DockerServiceFactory<T> dockerServiceFactory;
	protected DockerServiceSpec serviceSpec;

	
	public DockerServiceBuilderImpl(DockerServiceFactory<T> dockerServiceFactory, DockerServiceSpec serviceSpec) {
		super();
		this.dockerServiceFactory = dockerServiceFactory;
		this.serviceSpec = serviceSpec;
	}

	@Override
	public DockerServiceSpecLike<?> getDelegate() {
		return serviceSpec;
	}
	
	@Override
	public T get() {
		T result = dockerServiceFactory.create(serviceSpec);
		return result;
	}
}
