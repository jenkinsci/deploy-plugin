package hudson.plugins.deploy;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Mocks out a ContainerAdapter so that we can actually run the deployer in a trivial way, just to test pipeline compatibility
 * CHECKME Might need to extend CargoContainerAdapter to better exercise APIs
 */
@Extension
public class WorkflowCompatibleAdapter extends ContainerAdapter {
    String containerName;

    @DataBoundConstructor
    public WorkflowCompatibleAdapter() {
        this.containerName = "workflowAdapter";
    }

    @Override
    public void redeployFile(FilePath war, String aContextPath, Run<?, ?> run, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Mock container deployed to " + containerName);
    }

    public String getContainerId() {
            return containerName;
        }


    @Symbol("workflowAdapter")
    @Extension
    public static class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Workflow Adapter";
        }
    }
}
