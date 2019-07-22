package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 9.x
 *
 * @author m4ndr4ck
 */
public class Tomcat9xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = -7479041536985095270L;

    private static final String PATH = "/manager/text";

    /**
     * Tomcat 9 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId tomcat manager username password credentials
     */
    @DataBoundConstructor
    public Tomcat9xAdapter(String url, String credentialsId) {
        super(url, credentialsId, PATH);
    }

    @Override
    public String getContainerId() {
        return "tomcat9x";
    }

    @Symbol("tomcat9")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "Tomcat 9.x";
        }
    }
}
