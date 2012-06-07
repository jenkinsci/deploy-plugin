package hudson.plugins.deploy;

import org.codehaus.cargo.container.property.RemotePropertySet;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class PasswordProtectedAdapterCargo extends DefaultCargoContainerAdapterImpl {
    @Property(RemotePropertySet.USERNAME)
    public final String userName;
    private final String password;

    public PasswordProtectedAdapterCargo(String userName, String password) {
        this.password = password;
        this.userName = userName;
    }

    @Property(RemotePropertySet.PASSWORD)
    public String getPassword() {
        return password;
    }
}
