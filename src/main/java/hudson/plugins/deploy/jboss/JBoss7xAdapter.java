package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class JBoss7xAdapter extends JBossAdapter {
    private static final long serialVersionUID = 651865313737173321L;

    @DataBoundConstructor
    public JBoss7xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss7x";
    }

    @Extension
    @Symbol("jboss7")
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 7.x";
        }
    }
}
