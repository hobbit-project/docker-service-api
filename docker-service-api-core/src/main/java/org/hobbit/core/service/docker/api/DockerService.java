package org.hobbit.core.service.docker.api;

import com.google.common.util.concurrent.Service;

/**
 * A docker service at minimum exposes the imageName, the container id (if running) and the exit code (if stopped).
 *
 * @author raven Sep 20, 2017
 *
 */
public interface DockerService
    extends Service
{
    String getImageName();


    /**
     * The container id of a once started service
     * 
     * TODO This is usually NOT the docker Id, but an IP address, a host name, or some other form of handle
     * 
     * @return
     */
    String getContainerId();

    /**
     * The exit code of a terminated service
     * 
     * @return
     */
    Integer getExitCode();
}
