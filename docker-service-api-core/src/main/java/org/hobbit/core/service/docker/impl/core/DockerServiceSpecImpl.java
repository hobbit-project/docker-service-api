package org.hobbit.core.service.docker.impl.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hobbit.core.service.docker.api.DockerServiceSpec;

/**
 * Most simple implementation of a service specification as a Pojo.
 * 
 * 
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 */
public class DockerServiceSpecImpl
	implements DockerServiceSpec
{
	protected String containerName = null;
	protected String imageName = null;
	protected Map<String, String> localEnvironment = new LinkedHashMap<>();

	@Override
	public String getContainerName() {
		return containerName;
	}

	@Override
	public DockerServiceSpec setContainerName(String containerName) {
		this.containerName = containerName;
		return this;
	}

	@Override
	public String getImageName() {
		return imageName;
	}
	
	@Override
	public DockerServiceSpec setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	@Override
	public Map<String, String> getLocalEnvironment() {
		return localEnvironment;
	}

	@Override
	public DockerServiceSpec setLocalEnvironment(Map<String, String> localEnvironment) {
		this.localEnvironment = localEnvironment;
		return this;
	}

	@Override
	public String toString() {
		return "DockerServiceSpecImpl [containerName=" + containerName + ", imageName=" + imageName
				+ ", localEnvironment=" + localEnvironment + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containerName == null) ? 0 : containerName.hashCode());
		result = prime * result + ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result + ((localEnvironment == null) ? 0 : localEnvironment.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DockerServiceSpecImpl other = (DockerServiceSpecImpl) obj;
		if (containerName == null) {
			if (other.containerName != null)
				return false;
		} else if (!containerName.equals(other.containerName))
			return false;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (localEnvironment == null) {
			if (other.localEnvironment != null)
				return false;
		} else if (!localEnvironment.equals(other.localEnvironment))
			return false;
		return true;
	}
}
