package org.hobbit.core.service.docker.impl.docker_client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hobbit.core.service.docker.api.DockerServiceSpec;
import org.hobbit.core.service.docker.util.EnvironmentUtils;

import com.spotify.docker.client.messages.ContainerConfig;

/**
 * DockerServceSpec implementation that is backed by
 * DockerClient's containerConfig object
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 */
public class DockerServiceSpecDockerClient
	implements DockerServiceSpec
{
	protected ContainerConfig.Builder containerConfigBuilder;
	protected boolean hostMode;
	protected Set<String> networks;
	protected String containerName;
	
	protected Map<String, String> localEnv = new LinkedHashMap<>();


	public static DockerServiceSpecDockerClient wrap(ContainerConfig.Builder containerConfigBuilder) {
		DockerServiceSpecDockerClient result = new DockerServiceSpecDockerClient();
		result.setContainerConfigBuilder(containerConfigBuilder);
		
		return result;
	}
	
	public ContainerConfig.Builder getContainerConfigBuilder() {
		return containerConfigBuilder;
	}

	public DockerServiceSpecDockerClient setContainerConfigBuilder(ContainerConfig.Builder containerConfigBuilder) {
		this.containerConfigBuilder = containerConfigBuilder;
		return this;
	}

	@Override
	public String getContainerName() {
		return containerName;
	}

	@Override
	public DockerServiceSpec setContainerName(String containerName) {
		this.containerName = containerName;
		return this;
	}


	public String getImageName() {
		return containerConfigBuilder.build().image();
	}

	public DockerServiceSpecDockerClient setImageName(String imageName) {
		containerConfigBuilder.image(imageName);
		return this;
	}

	@Override
	public Map<String, String> getLocalEnvironment() {
		return localEnv;
	}

	@Override
	public DockerServiceSpecDockerClient setLocalEnvironment(Map<String, String> environment) {
		this.localEnv = environment;
		return this;
	}

	public Set<String> getNetworks() {
		return networks;
	}

	public boolean isHostMode() {
		return hostMode;
	}

	/**
	 * Specifically, merges the local environment into that of the containerConfig
	 *
	 * @return
	 */
	public static void finalizeContainerConfig(DockerServiceSpecDockerClient config) {
		//Objects.requireNonNull(containerConfigBuilder);
		ContainerConfig.Builder containerConfigBuilder = config.getContainerConfigBuilder();
		Map<String, String> localEnv = config.getLocalEnvironment();
		
		// Merge the local environment into that of the containerConfig
		List<String> rawEnv = containerConfigBuilder.build().env();
		if (rawEnv == null) {
			rawEnv = Collections.emptyList();
		}

		Map<String, String> env = EnvironmentUtils.listToMap(rawEnv);
		env.putAll(localEnv);

		List<String> envList = EnvironmentUtils.mapToList(env);
		containerConfigBuilder.env(envList);
		//ContainerConfig containerConfig = containerConfigBuilder.build();
		
		//return containerConfig;
	}

}
