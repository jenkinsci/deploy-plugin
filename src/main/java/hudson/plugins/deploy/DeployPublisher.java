package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Deploys WAR to a container.
 *
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Builder implements Serializable {

    private List<ContainerAdapter> adapters;
    public final String contextPath;
    public final String attempts;

    public final String war;
    public final boolean onFailure;
    public final String action;

    /**
     * @deprecated Use {@link #getAdapters()}
     */
    public final ContainerAdapter adapter = null;

    @DataBoundConstructor
    public DeployPublisher(List<ContainerAdapter> adapters, String war, String contextPath, String attempts, boolean onFailure, String action) {
        this.adapters = adapters;
        this.war = war;
        this.onFailure = onFailure;
        this.contextPath = contextPath;
        this.attempts = attempts;
        this.action = action;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if ((build.getResult() != null && build.getResult().equals(Result.SUCCESS)) || build.getResult() == null || onFailure) {
            // expand context path using build env variables
            String contextPath = expandVariable(build.getEnvironment(listener), build.getBuildVariableResolver(), this.contextPath);
            String retries = expandVariable(build.getEnvironment(listener), build.getBuildVariableResolver(), this.attempts);
            System.err.println("Retries: " + retries);
            for (FilePath warFile : build.getWorkspace().list(this.war)) {
                for (ContainerAdapter adapter : adapters) {
                    if (!adapter.execute(warFile, contextPath, retries != null ? Integer.parseInt(retries) : 1, action, build, launcher, listener)) {

                        build.setResult(Result.FAILURE);
                    }
                }
            }
        } else {
        }

        return true;
    }

    protected String expandVariable(EnvVars envVars, VariableResolver<String> resolver, String variable) {
        String temp = envVars.expand(variable);
        return Util.replaceMacro(envVars.expand(variable), resolver);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public Object readResolve() {
        if (adapter != null) {
            if (adapters == null) {
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

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        /**
         * Sort the descriptors so that the order they are displayed is more
         * predictable
         */
        public List<ContainerAdapterDescriptor> getAdaptersDescriptors() {
            List<ContainerAdapterDescriptor> r = new ArrayList<ContainerAdapterDescriptor>(ContainerAdapter.all());
            Collections.sort(r, new Comparator<ContainerAdapterDescriptor>() {
                public int compare(ContainerAdapterDescriptor o1, ContainerAdapterDescriptor o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
            return r;
        }
    }

    public FormValidation doCheckAttempts(@QueryParameter String value) {
        try {
            Integer.parseInt(value);
        } catch (Exception e) {
            return FormValidation.error("Not an integer.");
        }

        return FormValidation.ok();
    }

    private static final long serialVersionUID = 1L;
}
