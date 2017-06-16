package hudson.plugins.deploy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * Deploys WAR to a container.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Publisher implements SimpleBuildStep, Serializable {
    private List<ContainerAdapter> adapters;
    public final String contextPath;

    public final String war;
    public final boolean onFailure;

    /**
     * @deprecated
     *      Use {@link #getAdapters()}
     */
    public final ContainerAdapter adapter = null;
    
    @DataBoundConstructor
    public DeployPublisher(List<ContainerAdapter> containers, String war, String contextPath, boolean onFailure) {
   		this.adapters = containers;
        this.war = war;
        this.onFailure = onFailure;
        this.contextPath = contextPath;
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    @Deprecated
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        perform(false, build, build.getWorkspace(), launcher, listener);
        return true;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        perform(true, run, workspace, launcher, listener);
    }

    private void perform (boolean fromWorkFlow, @Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        if (fromWorkFlow || onFailure || Result.SUCCESS.equals(run.getResult())) {
            if (!workspace.exists()) {
                listener.getLogger().println("[DeployPublisher][ERROR] Workspace not found");
                throw new FileNotFoundException("Workspace not found");
            }

            FilePath[] wars = workspace.list(this.war);
            if (wars.length == 0) {
                listener.getLogger().printf("[DeployPublisher][WARN] No wars found. Deploy aborted. %n");
                return;
            }
            listener.getLogger().printf("[DeployPublisher][INFO] Attempting to deploy %d war file(s)%n", wars.length);

            for (FilePath warFile : wars) {
                for (ContainerAdapter adapter : adapters) {
                    if (!adapter.redeploy(warFile, contextPath, run, launcher, listener)) {
                        run.setResult(Result.FAILURE);
                    }
                }
            }
        } else {
            listener.getLogger().println("[DeployPublisher][INFO] Build failed or incomplete, project not deployed");
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
    
    public Object readResolve() {
    	if(adapter != null) {
    		if(adapters == null) {
    			adapters = new ArrayList<ContainerAdapter>();
    		}
    		adapters.add(adapter);
    	}
    	return this;
    }

    @Override
    public Descriptor getDescriptor () {
        return new DeployPublisher.DescriptorImpl();
    }

    /**
	 * Get the value of the adapterWrappers property
	 *
	 * @return The value of adapterWrappers
	 */
	public List<ContainerAdapter> getAdapters() {
		return adapters;
	}

	@Symbol("deploy")
    @Extension
    public static final class DescriptorImpl extends Descriptor<Publisher> {

        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        /**
         * Sort the descriptors so that the order they are displayed is more predictable
         *
         * @return a alphabetically sorted list of AdapterDescriptors
         */
        public List<ContainerAdapterDescriptor> getAdaptersDescriptors() {
            List<ContainerAdapterDescriptor> r = new ArrayList<ContainerAdapterDescriptor>(ContainerAdapter.all());
            Collections.sort(r,new Comparator<ContainerAdapterDescriptor>() {
                public int compare(ContainerAdapterDescriptor o1, ContainerAdapterDescriptor o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
            return r;
        }
    }

    private static final long serialVersionUID = 1L;
}
