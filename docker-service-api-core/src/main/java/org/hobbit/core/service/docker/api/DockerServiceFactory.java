package org.hobbit.core.service.docker.api;

import java.util.Map;

import org.hobbit.core.service.docker.impl.core.DockerServiceSpecImpl;

/**
 * In contrast to the builder, the service factory only provides a single create method.
 * 
 * @author raven Nov 18, 2017
 *
 */
public interface DockerServiceFactory<T extends DockerService>
	extends AutoCloseable
{
	/**
	 * Create a service object based on a specification
	 * 
	 * @param serviceSpec
	 * @return
	 */
	T create(DockerServiceSpec serviceSpec);
	
	/**
	 * Method to obtain a new, possibly partially configured, service specification object.
	 * 
	 * @return
	 */
//	default DockerServiceSpec newSpec() {
//		return new DockerServiceSpecImpl();
//	}
	
	// TODO Due to dependence on the serviceSpec object, which might already be implementation dependnent,
	// this implementation might better fit into an abstract class
	default T create(String containerName, String imageName, Map<String, String> env) {
		//DockerServiceSpec r = ModelFactory.createDefaultModel().createResource().as(DockerServiceSpec.class);
		
//		DockerServiceSpec r = newSpec();
		DockerServiceSpec r = new DockerServiceSpecImpl();
		
		r.setContainerName(containerName);
		r.setImageName(imageName);
		r.setLocalEnvironment(env);
		
		T result = create(r);
		return result;
	}
	
	default T create(String imageName, Map<String, String> env) {
		T result = create(null, imageName, env);
		return result;
	}
}
