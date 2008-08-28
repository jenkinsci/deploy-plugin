package hudson.plugins.deploy;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.DescriptorList;
import hudson.util.FormFieldValidator;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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

    public DescriptorImpl getDescriptor() {
        return DescriptorImpl.INSTANCE;
    }

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private DescriptorImpl() {
            super(DeployPublisher.class);
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        public DescriptorList<ContainerAdapter> getContainerAdapters() {
            return ContainerAdapter.LIST;
        }

        public static final DescriptorImpl INSTANCE = new DescriptorImpl();
        
        public void doCheckUrl(final StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {        
	        new FormFieldValidator(req,rsp,true) {
	            public void check() throws IOException, ServletException {
	            	String url = req.getParameter("value");
	            	
	            	if (url != null && url.length() > 0) {
	            		try {
		            		URL u = new URL(url);
		            	} catch (Exception e) {
		            		error(Messages.DeployPublisher_BadFormedUrl());
		            		return;
		            	}	
	            	}
	            		                	                
	                ok();
	            }
	        }.process();
	    }

    }

    private static final long serialVersionUID = 1L;
}
