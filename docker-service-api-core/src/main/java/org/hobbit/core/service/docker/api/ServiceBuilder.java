package org.hobbit.core.service.docker.api;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Service;

/**
 * Tag interface for dependency injection
 * Deriving from Supplier is maybe a bit too generic (so its not bound to the application domain),
 * but probably also harmless.
 *
 * @author raven
 *
 */
public interface ServiceBuilder<T extends Service>
    extends Supplier<T>
{
}
