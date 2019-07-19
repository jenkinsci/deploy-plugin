package hudson.plugins.deploy.weblogic;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * WebLogic 10.3.x
 * 
 * @author Kohsuke Kawaguchi
 */
@Restricted(DoNotUse.class)
public class WebLogic10_3_xAdapter extends WebLogicAdapter {
    private static final long serialVersionUID = 7379332493541263691L;

    @DataBoundConstructor
    public WebLogic10_3_xAdapter(String home, String credentialsId, String server, Integer port) {
        super(home, credentialsId, server, port);
    }

    protected String getContainerId() {
        return "weblogic103x";
    }

    // WebLogic support is limited to local only, so it's not very useful.
    // take it off until we figure out the remote support
    // @Extension
    public static final class DescriptorImpl extends WebLogicAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "WebLogic 10.3.x";
        }
    }
}
