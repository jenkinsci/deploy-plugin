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
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Serializable;

/**
 * Deploys WAR to a continer.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Publisher implements Serializable {
    public final ContainerAdapter adapter;

    public final String war;

    @DataBoundConstructor
    public DeployPublisher(ContainerAdapter adapter, String war) {
        this.adapter = adapter;
        this.war = war;
    }

    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        FilePath warFile = build.getParent().getWorkspace().child(this.war);
        if(!adapter.redeploy(warFile,build,launcher,listener))
            build.setResult(Result.FAILURE);

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
    }

    private static final long serialVersionUID = 1L;
}
