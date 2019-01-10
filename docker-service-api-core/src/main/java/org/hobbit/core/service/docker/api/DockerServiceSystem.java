package org.hobbit.core.service.docker.api;


/**
 * A DockerServiceSystem combines both the factory and the builder APIs:
 * - The builder API creates preconfigured configuration objects that can be modified using the
 *   DockerServiceSpec API (possibly using casts)
 * - The factory API, which only takes a single service specification, and fills the rest up with
 *   default values based on the factory state. 
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 * @param <T>
 */
public interface DockerServiceSystem<T extends DockerService>
	extends DockerServiceFactory<T>, DockerServiceBuilderFactory<T>
{

}
