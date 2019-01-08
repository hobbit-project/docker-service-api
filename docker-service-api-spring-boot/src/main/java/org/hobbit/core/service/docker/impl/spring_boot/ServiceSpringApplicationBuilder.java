package org.hobbit.core.service.docker.impl.spring_boot;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hobbit.core.service.docker.impl.core.DockerServiceBuilderFactory;
import org.hobbit.core.service.docker.impl.core.DockerServiceBuilderJsonDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;


/**
 * 
 * Service implementation that when started, looks up a Service in the given
 * context and starts it.
 * 
 * @author raven Nov 22, 2017
 *
 */
public class ServiceSpringApplicationBuilder
	extends AbstractService
{	
	private static final Logger logger = LoggerFactory.getLogger(ServiceSpringApplicationBuilder.class);

	
	protected String appName;
	protected SpringApplicationBuilder appBuilder;
	protected String[] args;

	protected ConfigurableApplicationContext ctx = null;
	protected Service mainService = null;

	
	public ServiceSpringApplicationBuilder(String appName, SpringApplicationBuilder appBuilder) {
		this(appName, appBuilder, new String[]{});
	}
	
	public ServiceSpringApplicationBuilder(String appName, SpringApplicationBuilder appBuilder, String[] args) {
		super();
		this.appName = appName;
		this.appBuilder = appBuilder;
		this.args = args;
	}

	public static <T> T findRoot(T start, Function<? super T, ? extends T> getParent) {
		T current = start;
		T tmp;
		while((tmp = getParent.apply(current)) != null) {
			current = tmp;
		}
		
		return current;
	}

	@Override
	protected void doStart() {
		// Create the application context
		ctx = appBuilder.run(args);


		// Get the service from the context
		mainService = BeanFactoryAnnotationUtils.qualifiedBeanOfType(ctx.getBeanFactory(), Service.class, "MainService");
		//mainService = ctx.getBean("MainService", Service.class);
		
		// Link the life cycle of the context to the service
		ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)findRoot(ctx, ApplicationContext::getParent);
//		ConfigurableApplicationContext rootCtx = ctx;

		// Add a listener that closes the service's (root) context on service termination
		mainService.addListener(new Listener() {
			@Override
			public void running() {
				notifyStarted();
			}
            @Override
            public void failed(State priorState, Throwable t) {
                logger.info("ServiceCapable service wrapped [FAILED] for " + (mainService == null ? "(no active service)" : mainService.getClass()), t);
//              logger.info("ServiceCapable service wrapper stopped");
//                  ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)getRoot(ctx.getParent(), ApplicationContext::getParent);
                try {
                	rootCtx.close();
                } finally {
                	notifyFailed(t);
                }
                //rootCtx.close();
            }

		    @Override
			public void terminated(State priorState) {
				logger.info("ServiceCapable service wrapper [TERMINATED] for " + (mainService == null ? "(no active service)" : mainService.getClass()));
//				logger.info("ServiceCapable service wrapper stopped");
//					ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)getRoot(ctx.getParent(), ApplicationContext::getParent);
				//rootCtx.close();
				try {
					rootCtx.close();
				} finally {
					notifyStopped();
				}
			}
		}, MoreExecutors.directExecutor());

		mainService.startAsync();
	}

	public SpringApplicationBuilder getAppBuilder() {
		return appBuilder;
	}
	
	@Override
	protected void doStop() {
		mainService.stopAsync();
	}
//	@Override
//	protected void run() throws Exception {
//		logger.info("ServiceSpringApplicationBuilder::startUp [begin] " + appName + ", builderHash: " + appBuilder.hashCode());
//		
//
//		mainService.startAsync().awaitTerminated();
//		
//		//ctx = appBuilder.run(args);
//		logger.info("ServiceSpringApplicationBuilder::startUp [end] " + appName + ", builderHash: " + appBuilder.hashCode());
//	}
	
	
	public static Map<String, DockerServiceBuilderFactory<?>> convert(Map<String, Supplier<SpringApplicationBuilder>> imageToAppBuilderSupplier) {

		Map<String, DockerServiceBuilderFactory<?>> result = imageToAppBuilderSupplier.entrySet().stream()
				.collect(Collectors.toMap(
						Entry::getKey,
						e -> () -> DockerServiceBuilderJsonDelegate.create(
								new DockerServiceFactorySpringApplicationBuilder(imageToAppBuilderSupplier)::create).setImageName(e.getKey())));
	
		return result;
	}

}
