package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 8.x
 *
 * @author soudmaijer
 */
public class Tomcat8xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = -998875391401118618L;

    private static final String PATH = "/manager/text";

    /**
     * Tomcat 8 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId tomcat manager username password credentials
     */
    @DataBoundConstructor
    public Tomcat8xAdapter(String url, String credentialsId) {
        super(url, credentialsId, PATH);
    }

    @Override
    public String getContainerId() {
        return "tomcat8x";
    }

    @Symbol("tomcat8")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "Tomcat 8.x";
        }
    }
}
