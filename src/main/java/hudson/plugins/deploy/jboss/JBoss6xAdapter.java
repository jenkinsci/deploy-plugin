package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class JBoss6xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss6xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss6x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 6.x";
        }
    }
}
