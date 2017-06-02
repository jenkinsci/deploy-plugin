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
 * Mocks out a ContainerAdapter so that we can actually run the deployer in a trivial way, just to test pipeline compatibility
 * CHECKME Might need to extend CargoContainerAdapter to better exercise APIs
 */
public class MockAdapter extends ContainerAdapter {
    String containerName;

    @Override
    public boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Mock container deployed to "+containerName);
        return true;
    }

    public String getContainerId() {
        return "mockContainer";
    }

    @DataBoundConstructor
    public MockAdapter(String containerName) {
        this.containerName = containerName;
    }

    @Extension
    @Symbol("mock")
    public static class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Mock Container";
        }
    }
}
