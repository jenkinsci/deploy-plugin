package hudson.plugins.deploy;

import hudson.model.Describable;
import hudson.ExtensionPoint;
import hudson.util.DescriptorList;

/**
 * @author Kohsuke Kawaguchi
 */
public interface ContainerAdapter extends Describable<ContainerAdapter>, ExtensionPoint {
    DescriptorList<ContainerAdapter> LIST = new DescriptorList<ContainerAdapter>(
        Tomcat5xAdapter.DESCRIPTOR,
        Tomcat4xAdapter.DESCRIPTOR,
        JBoss4xAdapter.DESCRIPTOR,
        JBoss3xAdapter.DESCRIPTOR
    );
}
