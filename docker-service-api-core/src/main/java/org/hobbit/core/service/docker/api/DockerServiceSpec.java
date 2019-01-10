package org.hobbit.core.service.docker.api;

/**
 * Simple mutable specification of a docker(-like) service.
 * 
 * Note, that this is deliberately an interface so that implementations
 * can choose to use different backing specification objects,
 * such as JSON or RDF resources. 
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 */
public interface DockerServiceSpec
	extends DockerServiceSpecLike<DockerServiceSpec>
{
//	String getContainerName();
//	DockerServiceSpec setContainerName(String containerName);
//	
//	String getImageName();
//	DockerServiceSpec setImageName(String imageName);
//	
//	Map<String, String> getLocalEnvironment();
//	DockerServiceSpec setLocalEnvironment(Map<String, String> localEnvironment);
}
