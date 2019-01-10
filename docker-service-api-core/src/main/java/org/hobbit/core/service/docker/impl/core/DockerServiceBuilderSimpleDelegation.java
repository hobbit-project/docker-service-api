package org.hobbit.core.service.docker.impl.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.hobbit.core.service.docker.api.DockerService;
import org.hobbit.core.service.docker.api.DockerServiceBuilder;

/**
 * Delegates the creation of a docker container to the provided
 * start / stop and run functions.
 *
 *
 * FIXME How to determine whether the service is running / terminated?
 * - One option could be that there has to be a run function which blocks as long as the remote service is running.
 *   For instance, the run function could wait for the termination signal from the platform
 *   Downside: The service needs an extra thread just to wait
 * - Another option: A handler outside of the service receives the signals and calls stop
 *   This is the way to go
 *
 * @author raven Sep 24, 2017
 *
 */
public class DockerServiceBuilderSimpleDelegation
    implements DockerServiceBuilder<DockerService>//, Cloneable
{
	// FIXME The design we actually want is to use an Resource to hold the state,
	// and then just delegate the resource to a function
	// The consumer can then create any view of the resource it wants
	protected String containerName;
    protected String imageName;
    protected Map<String, String> localEnvironment;

    // Delegate to start a service; arguments are the image Name and the localEnvironment
    // Result must be the container id
    protected BiFunction<String, Map<String, String>, String> startServiceDelegate;

    // Function to stop a container. Argument is the container id
    protected Consumer<String> stopServiceDelegate;

    protected Runnable runDelegate;

    // Registration for state changes of the container
    // protected Function<String, Publisher<Sring>> serviceStatus;


    public DockerServiceBuilderSimpleDelegation(
            BiFunction<String, Map<String, String>, String> startServiceDelegate,
            Consumer<String> stopServiceDelegate) {
        super();
        this.startServiceDelegate = startServiceDelegate;
        this.stopServiceDelegate = stopServiceDelegate;
        this.localEnvironment = new HashMap<>();
    }

//    @Override
//    public DockerServiceBuilderSimpleDelegation clone() throws CloneNotSupportedException {
//        DockerServiceBuilderSimpleDelegation result = new DockerServiceBuilderSimpleDelegation(
//                startServiceDelegate,
//                stopServiceDelegate
//        );
//        result.setImageName(imageName);
//        result.setLocalEnvironment(localEnvironment);
//        return result;
//    }

	@Override
	public String getContainerName() {
		return containerName;
	}

	@Override
	public DockerServiceBuilder<DockerService> setContainerName(String containerName) {
		this.containerName = containerName;
		return this;
	}

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public DockerServiceBuilderSimpleDelegation setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    @Override
    public Map<String, String> getLocalEnvironment() {
        return localEnvironment;
    }

    @Override
    public DockerServiceBuilderSimpleDelegation setLocalEnvironment(Map<String, String> environment) {
        this.localEnvironment = environment;
        return this;
    }

    @Override
    public DockerService get() {
        DockerServiceSimpleDelegation result = new DockerServiceSimpleDelegation(imageName, localEnvironment, startServiceDelegate, stopServiceDelegate);
        return result;
    }

}
