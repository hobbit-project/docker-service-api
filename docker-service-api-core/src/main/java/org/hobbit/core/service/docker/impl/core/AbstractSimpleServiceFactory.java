package org.hobbit.core.service.docker.impl.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hobbit.core.service.docker.api.SimpleServiceFactory;

import com.google.common.util.concurrent.Service;

public abstract class AbstractSimpleServiceFactory<T extends Service>
    implements SimpleServiceFactory<T>
{
    protected Map<String, String> environment = new LinkedHashMap<>();

    public Map<String, String> getEnvironment() {
        return environment;
    }
}
