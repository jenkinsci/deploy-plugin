package hudson.plugins.deploy;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
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
     * @param war the file path of the war to deploy
     * @param aContextPath the context path for the war to be deployed
     * @param build the build that is being deployed
     * @param launcher the launcher of the build
     * @param listener the BuildListener of the build to deploy
     * @return true if deployed successfully, false if failed
     * @throws IOException if there is an error locating the war file
     * @throws InterruptedException if there is an error deploying to the server
     */
    public abstract boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException;

    public ContainerAdapterDescriptor getDescriptor() {
        return (ContainerAdapterDescriptor)Jenkins.getActiveInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<ContainerAdapter,ContainerAdapterDescriptor> all() {
        return Jenkins.getActiveInstance().<ContainerAdapter,ContainerAdapterDescriptor>getDescriptorList(ContainerAdapter.class);
    }
}
