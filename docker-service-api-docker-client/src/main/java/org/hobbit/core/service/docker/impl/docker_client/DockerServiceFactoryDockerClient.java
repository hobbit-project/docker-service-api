package org.hobbit.core.service.docker.impl.docker_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.hobbit.core.service.docker.api.DockerService;
import org.hobbit.core.service.docker.api.DockerServiceFactory;
import org.hobbit.core.service.docker.util.EnvironmentUtils;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerConfig.Builder;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

public class DockerServiceFactoryDockerClient
	implements DockerServiceFactory<DockerService>
{
    protected DockerClient dockerClient;
    protected Supplier<ContainerConfig.Builder> containerConfigBuilderSupplier;
    protected boolean hostMode;
    protected Set<String> networks;

	
	public DockerServiceFactoryDockerClient(
			DockerClient dockerClient,
			Supplier<Builder> containerConfigBuilderSupplier,
			boolean hostMode,
			Set<String> networks
			) {
		super();

		Objects.requireNonNull(dockerClient);
		Objects.requireNonNull(containerConfigBuilderSupplier);
		
		this.dockerClient = dockerClient;
		this.containerConfigBuilderSupplier = containerConfigBuilderSupplier;
		this.hostMode = hostMode;
		this.networks = networks;
	}

	
//	   public static boolean containsVersionTag(String imageName) {
//	        int pos = 0;
//	        // Check whether the given image name contains a host with a port
//	        Matcher matcher = PORT_PATTERN.matcher(imageName);
//	        while (matcher.find()) {
//	            pos = matcher.end();
//	        }
//	        // Check whether there is a ':' in the remaining part of the image name
//	        return imageName.indexOf(':', pos) >= 0;
//	    }

    /**
     * Generates new unique instance name based on image name
     *
     * @param imageName
     *            base image name
     *
     * @return instance name
     */
    public static String deriveHostname(String imageName) {
        String baseName = imageName;
        // If there is a tag it has to be removed
//        if (containsVersionTag(baseName)) {
            int pos = imageName.lastIndexOf(':');
        if(pos >= 0) {
            baseName = baseName.substring(0, pos - 1);
        }
        int posSlash = baseName.lastIndexOf('/');
        int posColon = baseName.lastIndexOf(':');
        if (posSlash > posColon) {
            baseName = baseName.substring(posSlash + 1);
        } else if (posSlash < posColon) {
            baseName = baseName.substring(posColon + 1);
        }
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        StringBuilder builder = new StringBuilder(baseName.length() + uuid.length() + 1);
        builder.append(baseName.replaceAll("[/\\.]", "_"));
        builder.append('-');
        builder.append(uuid);
        return builder.toString();
    }

	@Override
	public DockerService create(String imageName, Map<String, String> localEnv) {
		Builder builder = containerConfigBuilderSupplier.get();
		Map<String, String> env = new LinkedHashMap<>();
		env.putAll(EnvironmentUtils.listToMap(builder.build().env()));
		env.putAll(localEnv);
		
		String containerName = null;
		if(!hostMode) {
			String hostname = deriveHostname(imageName);
			builder.hostname(hostname);
			containerName = hostname;
		}
		
		ContainerConfig containerConfig = builder
			.image(imageName)
			.env(EnvironmentUtils.mapToList(env))
			.build();
		
		DockerServiceDockerClient result = new DockerServiceDockerClient(dockerClient, containerConfig, containerName, hostMode, networks);

		return result;
	}

	
	public static DockerServiceFactory<?> create(
			boolean hostMode, Map<String, String> env, Set<String> networks) throws DockerCertificateException {
        DockerClient dockerClient = DefaultDockerClient.fromEnv().build();


        // Bind container port 443 to an automatically allocated available host
        String[] ports = { }; //{ "80", "22" };
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>();
            hostPorts.add(PortBinding.of("0.0.0.0", port));
            portBindings.put(port, hostPorts);
        }

        List<PortBinding> randomPort = new ArrayList<>();
        randomPort.add(PortBinding.randomPort("0.0.0.0"));
//        portBindings.put("443", randomPort);

        HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        
        //DockerServiceBuilderDockerClient dockerServiceFactory = new DockerServiceBuilderDockerClient();

        //DockerServiceBuilderFactory<DockerServiceBuilder<? extends DockerService>>
        
        Supplier<ContainerConfig.Builder> containerConfigBuilderSupplier = () ->
        	ContainerConfig.builder()
        		.hostConfig(hostConfig)
        		.env(EnvironmentUtils.mapToList("=", env))
        		;
        
        //Set<String> networks = Collections.singleton("hobbit");
        DockerServiceFactoryDockerClient result = new DockerServiceFactoryDockerClient(dockerClient, containerConfigBuilderSupplier, hostMode, networks);
        return result;
	}


	@Override
	public void close() throws Exception {
		dockerClient.close();
	}		
}
