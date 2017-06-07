package hudson.plugins.deploy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Scrambler;
import org.codehaus.cargo.container.property.RemotePropertySet;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class PasswordProtectedAdapterCargo extends DefaultCargoContainerAdapterImpl {
    @Property(RemotePropertySet.USERNAME)
    public final String userName;
    @Deprecated // backward compatibility
    private String password;
    private String passwordScrambled;

    public PasswordProtectedAdapterCargo(String userName, String password) {
        this.password = null;
        this.passwordScrambled = Scrambler.scramble(password);
        this.userName = userName;
    }

    @Property(RemotePropertySet.PASSWORD)
    public String getPassword() {
        return Scrambler.descramble(passwordScrambled);
    }

    @SuppressFBWarnings("SE_PRIVATE_READ_RESOLVE_NOT_INHERITED")
    private Object readResolve() {
        // backward compatibility
        if(passwordScrambled == null && password != null){
            passwordScrambled = Scrambler.scramble(password);
            password = null;
        }
        return this;
    }
}
