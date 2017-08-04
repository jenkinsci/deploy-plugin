package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class JBoss7xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss7xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss7x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 7.x";
        }
    }
}
