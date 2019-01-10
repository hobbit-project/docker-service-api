package org.hobbit.core.service.docker.impl.core;

import java.util.Map;

import org.hobbit.core.service.docker.api.DockerServiceSpecLike;

public abstract class DockerServiceSpecLikeForwarding<F extends DockerServiceSpecLike<?>>
	implements DockerServiceSpecLike<F>
{
	public abstract DockerServiceSpecLike<?> getDelegate();

	@Override
	public String getContainerName() {
		return getDelegate().getContainerName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public F setContainerName(String containerName) {
		getDelegate().setContainerName(containerName);
		return (F)this;
	}

	@Override
	public String getImageName() {
		return getDelegate().getImageName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public F setImageName(String imageName) {
		getDelegate().setImageName(imageName);
		return (F)this;
	}

	@Override
	public Map<String, String> getLocalEnvironment() {
		return getDelegate().getLocalEnvironment();
	}

	@SuppressWarnings("unchecked")
	@Override
	public F setLocalEnvironment(Map<String, String> localEnvironment) {
		getDelegate().setLocalEnvironment(localEnvironment);
		return (F)this;
	}

}
