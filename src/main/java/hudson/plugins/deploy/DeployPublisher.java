package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Deploys WAR to a container.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Notifier implements Serializable {
    public final ContainerAdapter adapter;
    public final String contextPath;

    public final String war;
    public final boolean onFailure;

    @DataBoundConstructor
    public DeployPublisher(ContainerAdapter adapter, String war, String contextPath, boolean onFailure) {
        this.adapter = adapter;
        this.war = war;
        this.onFailure = onFailure;
        this.contextPath = contextPath;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	EnvVars envVars = new EnvVars();
    	envVars = build.getEnvironment(listener);
    	String warFiles = envVars.expand(this.war);
    	String containerContextPath = envVars.expand(this.contextPath);
    	
    	if (build.getResult().equals(Result.SUCCESS) || onFailure) {
                for (FilePath warFile : build.getWorkspace().list(warFiles)) {
                if(!adapter.redeploy(warFile,containerContextPath,build,launcher,listener))
                    build.setResult(Result.FAILURE);
            }
        }

        return true;
    }

	private void resolveVariables(AbstractBuild<?, ?> build,
			BuildListener listener) throws IOException, InterruptedException {
		

	}

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        /**
         * Sort the descriptors so that the order they are displayed is more predictable
         */
        public List<ContainerAdapterDescriptor> getContainerAdapters() {
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
