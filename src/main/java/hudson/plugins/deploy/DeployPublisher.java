package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Result;
import hudson.model.AbstractProject;
import hudson.model.listeners.ItemListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import jenkins.util.io.FileBoolean;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;

/**
 * Deploys WAR to a container.
 *
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Notifier implements SimpleBuildStep, Serializable {

    private List<ContainerAdapter> adapters;
    private String contextPath = "";

    private String war;
    private boolean onFailure = true;

    /**
     * @deprecated
     *      Use {@link #getAdapters()}
     */
    public final ContainerAdapter adapter = null;

    @DataBoundConstructor
    public DeployPublisher(List<ContainerAdapter> adapters, String war) {
        this.adapters = adapters;
        this.war = war;
    }

    @Deprecated
    public DeployPublisher(List<ContainerAdapter> adapters, String war, String contextPath, boolean onFailure) {
   		this.adapters = adapters;
        this.war = war;
    }

    public String getWar () {
        return war;
    }

    public boolean isOnFailure () {
        return onFailure;
    }

    @DataBoundSetter
    public void setOnFailure (boolean onFailure) {
        this.onFailure = onFailure;
    }

    public String getContextPath () {
        return contextPath;
    }

    @DataBoundSetter
    public void setContextPath (String contextPath) {
        this.contextPath = Util.fixEmpty(contextPath);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        Result result = run.getResult();
        if (onFailure || result == null || Result.SUCCESS.equals(result)) {
            if (!workspace.exists()) {
                listener.getLogger().println("[DeployPublisher][ERROR] Workspace not found");
                throw new AbortException("Workspace not found");
            }

            FilePath[] wars = workspace.list(this.war);
            if (wars == null || wars.length == 0) {
                listener.getLogger().printf("[DeployPublisher][WARN] No wars found. Deploy aborted. %n");
                return;
            }
            listener.getLogger().printf("[DeployPublisher][INFO] Attempting to deploy %d war file(s)%n", wars.length);

            for (FilePath warFile : wars) {
                for (ContainerAdapter adapter : adapters) {
                    adapter.redeployFile(warFile, contextPath, run, launcher, listener);
                }
            }
        } else {
            listener.getLogger().println("[DeployPublisher][INFO] Build failed, project not deployed");
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
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public boolean defaultOnFailure (Object job) {
            return !(job instanceof AbstractProject);
        }

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

    @Restricted(NoExternalUse.class)
    @Extension
    public static final class Migrator extends ItemListener {

        @SuppressWarnings("deprecation")
        @Override
        public void onLoaded() {
            FileBoolean migrated = new FileBoolean(getClass(), "migratedCredentials");
            if (migrated.isOn()) {
                return;
            }
            List<StandardUsernamePasswordCredentials> generatedCredentials = new ArrayList<StandardUsernamePasswordCredentials>();
            for (AbstractProject<?,?> project : Jenkins.getActiveInstance().getAllItems(AbstractProject.class)) {
                try {
                    DeployPublisher d = project.getPublishersList().get(DeployPublisher.class);
                    if (d == null) {
                        continue;
                    }
                    boolean modified = false;
                    boolean successful = true;
                    for (ContainerAdapter a : d.getAdapters()) {
                        if (a instanceof PasswordProtectedAdapterCargo) {
                            PasswordProtectedAdapterCargo ppac = (PasswordProtectedAdapterCargo) a;
                            if (ppac.getCredentialsId() == null) {
                                successful &= ppac.migrateCredentials(generatedCredentials);
                                modified = true;
                            }
                        }
                    }
                    if (modified) {
                        if (successful) {
                            Logger.getLogger(DeployPublisher.class.getName()).log(Level.INFO, "Successfully migrated DeployPublisher in project: {0}", project.getName());
                            project.save();
                        } else {
                            // Avoid calling project.save() because PasswordProtectedAdapterCargo will null out the username/password fields upon saving
                            Logger.getLogger(DeployPublisher.class.getName()).log(Level.SEVERE, "Failed to create credentials and migrate DeployPublisher in project: {0}, please manually add credentials.", project.getName());
                        }
                    }
                } catch (IOException e) {
                    Logger.getLogger(DeployPublisher.class.getName()).log(Level.WARNING, "Migration unsuccessful", e);
                }
            }
            migrated.on();
        }
    }

}
