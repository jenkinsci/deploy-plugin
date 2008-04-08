package hudson.plugins.deploy;

import org.codehaus.cargo.container.property.RemotePropertySet;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class PasswordProtectedAdapter extends DefaultContainerAdapterImpl {
    @Property(RemotePropertySet.USERNAME)
    public final String userName;
    @Property(RemotePropertySet.PASSWORD)
    public final String password;

    public PasswordProtectedAdapter(String userName, String password) {
        this.password = password;
        this.userName = userName;
    }
}
