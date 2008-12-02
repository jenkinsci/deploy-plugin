package hudson.plugins.deploy;

import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.plugins.deploy.glassfish.GlassFish2xAdapter;
import hudson.plugins.deploy.jboss.JBoss3xAdapter;
import hudson.plugins.deploy.jboss.JBoss4xAdapter;
import hudson.plugins.deploy.jboss.JBoss5xAdapter;
import hudson.plugins.deploy.tomcat.Tomcat4xAdapter;
import hudson.plugins.deploy.tomcat.Tomcat5xAdapter;
import hudson.plugins.deploy.tomcat.Tomcat6xAdapter;
import hudson.util.DescriptorList;

import java.io.IOException;

/**
 * Encapsulates container-specific deployment operation.
 *
 * <h2>Persistence</h2>
 * <p>
 * Instances of these objects are persisted in projects' configuration XML via XStream.
 * 
 *
 * @author Kohsuke Kawaguchi
 */
public interface ContainerAdapter extends Describable<ContainerAdapter>, ExtensionPoint {
    /**
     * Perform redeployment.
     *
     * If failed, return false.
     */
    boolean redeploy(FilePath war, AbstractBuild<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException;

    DescriptorList<ContainerAdapter> LIST = new DescriptorList<ContainerAdapter>(
        Tomcat6xAdapter.DESCRIPTOR,
        Tomcat5xAdapter.DESCRIPTOR,
        Tomcat4xAdapter.DESCRIPTOR,
        JBoss5xAdapter.DESCRIPTOR,
        JBoss4xAdapter.DESCRIPTOR,
        JBoss3xAdapter.DESCRIPTOR,
        GlassFish2xAdapter.DESCRIPTOR
    );
}
