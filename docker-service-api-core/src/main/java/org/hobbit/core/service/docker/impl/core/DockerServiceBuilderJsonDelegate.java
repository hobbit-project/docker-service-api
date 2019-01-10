package org.hobbit.core.service.docker.impl.core;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.hobbit.core.mapview.MapViewGson;
import org.hobbit.core.service.docker.api.DockerService;
import org.hobbit.core.service.docker.api.DockerServiceBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Delegates the creation of a docker container to the provided
 * start / stop and run functions.
 *
 * @author raven Sep 24, 2017
 *
 */
public class DockerServiceBuilderJsonDelegate<T extends DockerService>
    implements DockerServiceBuilder<T>//, Cloneable
{
	public static final String KEY_CONTAINER_NAME = "containerName";
	public static final String KEY_IMAGE_NAME = "imageName";
	public static final String KEY_ENV = "env";
	public static final String KEY_BASE_ENV = "baseEnv";
	
	
	protected static Gson gson = new Gson();
	protected JsonObject config;
	
	protected Function<JsonObject, T> delegate;
	
    public DockerServiceBuilderJsonDelegate(Function<JsonObject, T> delegate) {
    	this(delegate, new JsonObject());
    }

    public DockerServiceBuilderJsonDelegate(Function<JsonObject, T> delegate, JsonObject config) {
        super();
        this.delegate = delegate;
        this.config = config;
    }

	@Override
	public String getContainerName() {
        String result = config.get(KEY_CONTAINER_NAME).getAsString();
        return result;
	}

	@Override
	public DockerServiceBuilder<T> setContainerName(String containerName) {
    	config.remove(KEY_CONTAINER_NAME);
    	config.addProperty(KEY_CONTAINER_NAME, containerName);
    	
    	return this;
	}

    @Override
    public String getImageName() {
        String result = config.get(KEY_IMAGE_NAME).getAsString();
        return result;
    }

    @Override
    public DockerServiceBuilderJsonDelegate<T> setImageName(String imageName) {
    	config.remove(KEY_IMAGE_NAME);
    	config.addProperty(KEY_IMAGE_NAME, imageName);
    	
    	return this;
    }

    public Map<String, String> getBaseEnvironment() {
    	if(!config.has(KEY_BASE_ENV)) {
    		config.add(KEY_BASE_ENV, new JsonObject());
    	}
    	
    	Map<String, String> result = MapViewGson.createMapViewString(config.getAsJsonObject(KEY_BASE_ENV));
    	return result;    	
    }
    
    public DockerServiceBuilderJsonDelegate<T> setBaseEnvironment(Map<String, String> environment) {
    	Gson gson = new Gson();
    	JsonElement json = gson.toJsonTree(environment);
    	
    	config.remove(KEY_BASE_ENV);
    	config.add(KEY_BASE_ENV, json);
    	//throw new UnsupportedOperationException();
    	return this;
    }

    
    @Override
    public Map<String, String> getLocalEnvironment() {
    	if(!config.has(KEY_ENV)) {
    		config.add(KEY_ENV, new JsonObject());
    	}
    	
    	Map<String, String> result = MapViewGson.createMapViewString(config.getAsJsonObject(KEY_ENV));
    	return result;
    }

    @Override
    public DockerServiceBuilderJsonDelegate<T> setLocalEnvironment(Map<String, String> environment) {
    	Gson gson = new Gson();
    	JsonElement json = gson.toJsonTree(environment);
    	
    	config.remove(KEY_ENV);
    	config.add(KEY_ENV, json);
    	//throw new UnsupportedOperationException();
    	return this;
    }

    @Override
    public T get() {
    	T result = delegate.apply(config);
        return result;
    }

    
    public static Type mapStringStringType = new TypeToken<Map<String, String>>() {}.getType();
    
    // TODO The argument should be Function<? super DockerServiceSpec, T> serviceFactory
    public static <T extends DockerService> DockerServiceBuilderJsonDelegate<T> create(BiFunction<String, Map<String, String>, T> serviceFactory) {
    	Function<JsonObject, T> wrapperFn = jsonObj -> {
    		String imageName = jsonObj.get(KEY_IMAGE_NAME).getAsString();
    		
    		
    		
    		Map<String, String> overallEnv = new HashMap<String, String>();

    		{
        		JsonElement envE = jsonObj.get(KEY_BASE_ENV);
    			Map<String, String> env = gson.fromJson(envE, mapStringStringType);
        		env = env != null ? env : Collections.emptyMap();
    			overallEnv.putAll(env);
    		}

    		{
        		JsonElement envE = jsonObj.get(KEY_ENV);
    			Map<String, String> env = gson.fromJson(envE, mapStringStringType);
        		env = env != null ? env : Collections.emptyMap();
    			overallEnv.putAll(env);
    		}
    		
    		
    		
    		T r = serviceFactory.apply(imageName, overallEnv);
    		return r;
    	};
    	
    	DockerServiceBuilderJsonDelegate<T> result = new DockerServiceBuilderJsonDelegate<>(wrapperFn);
    	return result;
    }

}
