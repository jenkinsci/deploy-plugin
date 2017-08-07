package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * JBoss 4.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class JBoss4xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss4xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss4x";
    }

    @Extension
    @Symbol("jboss4")
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 4.x";
        }
    }
}
