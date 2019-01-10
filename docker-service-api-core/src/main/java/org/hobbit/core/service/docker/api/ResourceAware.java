package org.hobbit.core.service.docker.api;

/**
 * Interface for Java classes to expose whether they can make use of Jena's
 * Resource infrastructure
 * 
 * @author Claus Stadler, Jan 9, 2019
 *
 */
public interface ResourceAware {
	boolean isResource();
	
}
