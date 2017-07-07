package hudson.plugins.deploy;

import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Run;
import hudson.model.TaskListener;

import jenkins.model.Jenkins;

import java.io.IOException;

import static hudson.Util.isOverridden;

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

    @Deprecated
    public boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        redeployFile(war, aContextPath, build, launcher, listener);
        return true;
    }

    /**
     * Perform redeployment.
     *
     * If failed, return false.
     *
     * Implementations should override me and make {@link #redeploy(FilePath, String, AbstractBuild, Launcher, BuildListener)}
     *  delegate to that implementation to be usable within Pipeline projects
     * @param war the path of the war/ear file to deploy
     * @param aContextPath the context path for the war to be deployed
     * @param build the build that is being deployed
     * @param launcher the launcher of the build
     * @param listener the BuildListener of the build to deploy
     * @throws IOException if there is an error locating the war file
     * @throws InterruptedException if there is an error deploying to the server
     */
    public void redeployFile(FilePath war, String aContextPath, Run<?,?> build, Launcher launcher, final TaskListener listener) throws IOException, InterruptedException {
        if (build instanceof AbstractBuild) {
            if (isOverridden(ContainerAdapter.class, getClass(), "redeploy",
                    FilePath.class, String.class, AbstractBuild.class, Launcher.class, BuildListener.class)) {
                if (!redeploy(war, aContextPath, (AbstractBuild<?, ?>) build, launcher, (BuildListener) listener)) {
                    throw new AbortException("Deployment failed for unknown reason");
                }
            } else {
                throw new AbortException(
                        "This ContainerAdapter doesn't have an implementation of redeployFile(). Please contact " +
                        "the plugin maintainer and ask them to update their plugin to be compatible with Workflow"
                );
            }
        } else {
            throw new AbortException(
                    "[JENKINS-44810] redeploy() called using a Run, but this ContainerAdapter doesn't have an " +
                    "implementation for Run. Please contact the plugin maintainer and ask them to update their " +
                    "plugin to be compatible pipeline"
            );
        }
    }

    public ContainerAdapterDescriptor getDescriptor() {
        return (ContainerAdapterDescriptor)Jenkins.getActiveInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<ContainerAdapter,ContainerAdapterDescriptor> all() {
        return Jenkins.getActiveInstance().<ContainerAdapter,ContainerAdapterDescriptor>getDescriptorList(ContainerAdapter.class);
    }
}
