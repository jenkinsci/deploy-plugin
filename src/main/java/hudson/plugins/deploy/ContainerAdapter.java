package hudson.plugins.deploy;

import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.util.DescriptorList;
import org.codehaus.cargo.container.configuration.Configuration;

/**
 * Provides container-specific glue code.
 *
 * @author Kohsuke Kawaguchi
 */
public interface ContainerAdapter extends Describable<ContainerAdapter>, ExtensionPoint {
    /**
     * Returns the container ID used by Cargo. 
     */
    String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     */
    void configure(Configuration config);

    public static DescriptorList<ContainerAdapter> LIST = new DescriptorList<ContainerAdapter>(
        Tomcat5xAdapter.DESCRIPTOR
    );
}
