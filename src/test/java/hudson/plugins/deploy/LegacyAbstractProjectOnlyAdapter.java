package hudson.plugins.deploy;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * A ContainerAdapter that uses the AbstractProject method
 */
@Extension
public class LegacyAbstractProjectOnlyAdapter extends ContainerAdapter {
    String containerName;

    @DataBoundConstructor
    public LegacyAbstractProjectOnlyAdapter() {
        this.containerName = "mock";
    }

    @Override
    public boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Mock container deployed to " + containerName);
        return true;
    }

    public String getContainerId() {
            return containerName;
        }


    @Symbol("legacyAdapter")
    @Extension
    public static class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Mock Container";
        }
    }
}
