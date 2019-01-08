# docker-service-api

Guava Service implementations for unified service mangagement of docker containers, spring boot applications and dockerized spring boot applications.

## Concepts

## Service Abstraction
The service abstraction comprises classes to abstract Spring Boot components and docker services alike.
It is based on [Guava Services](https://github.com/google/guava/wiki/ServiceExplained).

### Docker Service Abstraction


#### ServiceBuilder
A `ServiceBuilder` is simply a `Supplier` of something that is a service.
Implementations of this interface should provide getters and setters for configurations. Finally, a call to `.get()` should then return a service based on the current state of builder. Future changes to the builder should not affect already created services.

```java
public interface ServiceBuilder<T extends Service>
    extends Supplier<T>
{
}
```

#### Docker Service Abstractions
A `DockerService` extends a guava Service with the following method declarations:

```java
public interface DockerService
    extends Service
{
    String getImageName();
    String getContainerId();
    Integer getExitCode();
}
```


In order to create docker services, at mininum an image name and its environment is needed.

The `DockerServiceFactory` handles exactly this basic case.
```java
public interface DockerServiceFactory<T extends DockerService> {
    T create(String imageName, Map<String, String> env);
}
```

```java
myDockerServiceFactory.create("my-image", ImmutableMap.<String, String>builder()
                                  .put("SOME_OPTION", "A")
                                  .put("SOME_OTHER_OPTION", "B")
                                  .build());
```

However, concrete implementations of DockerServiceFactory may introduce state - such a base environment.
If such a factory was then shared between components that modified this state, then calls to `.create()` would interfere with each other.

In order to isololate docker service creation, additional interfaces are provided.


The `DockerServiceBuilder` interface extends `ServiceBuilder` with common docker-related configuration attributes. Further sub-classes may introduce more configuration options. Each call to `.get()` is then expected to yield a fresh service in regard to the current state of the builder.

```java
public interface DockerServiceBuilder<T extends DockerService>
    extends ServiceBuilder<T>
{
    String getImageName();
    DockerServiceBuilder<T> setImageName(String imageName);
    Map<String, String> getLocalEnvironment();
    DockerServiceBuilder<T> setLocalEnvironment(Map<String, String> environment);
}
```

Finally, a `DockerServiceBuilderFactory` is a supplier of idependent and (usually) preconfigured `DockerServiceBuilder` instances.

```java
public interface DockerServiceBuilderFactory<B extends DockerServiceBuilder<? extends DockerService>>
    extends Supplier<B>
{
}
```

#### Docker Service Implementations






