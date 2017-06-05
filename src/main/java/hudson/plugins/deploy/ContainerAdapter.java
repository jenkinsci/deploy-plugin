package hudson.plugins.deploy;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Run;
import jenkins.model.Jenkins;

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
public abstract class ContainerAdapter implements Describable<ContainerAdapter>, ExtensionPoint {
    /**
     * Perform redeployment.
     *
     * If failed, return false.
     * Retained as abstract for back-compatibility in cases where a plugin extends deploy plugin and implements this only.
     * Implementations that are pipeline compatible should implement {@link #redeploy(FilePath, String, Run, Launcher, BuildListener)}
     *   and have this simply delegate to the now-compatible implementation.
     * @deprecated Prefer to invoke {@link #redeploy(FilePath, String, Run, Launcher, BuildListener)} where possible.
     */
    @Deprecated
    public abstract boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException;

    /**
     * Perform redeployment.
     *
     * If failed, return false.
     *
     * Implementations should override me and make {@link #redeploy(FilePath, String, AbstractBuild, Launcher, BuildListener)}
     *  delegate to that implementation to be usable within Pipeline projects
     */
    public boolean redeploy(FilePath war, String aContextPath, Run<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        if (build instanceof AbstractBuild) {
            return redeploy(war, aContextPath, build, launcher, listener);
        }
        return false;
    }

    public ContainerAdapterDescriptor getDescriptor() {
        return (ContainerAdapterDescriptor)Jenkins.getInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<ContainerAdapter,ContainerAdapterDescriptor> all() {
        return Jenkins.getInstance().<ContainerAdapter,ContainerAdapterDescriptor>getDescriptorList(ContainerAdapter.class);
    }
}
