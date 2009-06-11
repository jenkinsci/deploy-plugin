package hudson.plugins.deploy;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.DescriptorList;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.QueryParameter;

/**
 * Deploys WAR to a continer.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Publisher implements Serializable {
    public final ContainerAdapter adapter;

    public final String war;
    public final boolean onFailure;

    @DataBoundConstructor
    public DeployPublisher(ContainerAdapter adapter, String war, boolean onFailure) {
        this.adapter = adapter;
        this.war = war;
        this.onFailure = onFailure;
    }

    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	if (build.getResult().equals(Result.SUCCESS) || onFailure) {
	        FilePath warFile = build.getParent().getWorkspace().child(this.war);
	        if(!adapter.redeploy(warFile,build,launcher,listener))
	            build.setResult(Result.FAILURE);
    	}

        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        public DescriptorList<ContainerAdapter> getContainerAdapters() {
            return ContainerAdapter.LIST;
        }

        public FormValidation doCheckUrl(final StaplerRequest req, StaplerResponse rsp, @QueryParameter String value) throws IOException, ServletException {
            String url = value;

            if (url != null && url.length() > 0) {
                try {
                    URL u = new URL(url);
                } catch (Exception e) {
                    return FormValidation.error(Messages.DeployPublisher_BadFormedUrl());
                }
            }

            return FormValidation.ok();
	    }

    }

    private static final long serialVersionUID = 1L;
}
