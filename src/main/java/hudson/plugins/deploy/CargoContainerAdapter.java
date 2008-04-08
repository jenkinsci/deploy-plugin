package hudson.plugins.deploy;

import org.codehaus.cargo.container.configuration.Configuration;

/**
 * Provides container-specific glue code.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter implements ContainerAdapter {
    /**
     * Returns the container ID used by Cargo. 
     */
    public abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     */
    public abstract void configure(Configuration config);

}
