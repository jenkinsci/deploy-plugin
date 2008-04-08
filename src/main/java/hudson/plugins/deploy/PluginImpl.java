package hudson.plugins.deploy;

import hudson.Plugin;
import hudson.tasks.BuildStep;

/**
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {
    public void start() throws Exception {
        BuildStep.PUBLISHERS.addRecorder(DeployPublisher.DescriptorImpl.INSTANCE);
    }
}
