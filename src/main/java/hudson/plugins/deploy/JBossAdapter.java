package hudson.plugins.deploy;

/**
 * Base class for JBoss adapters.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class JBossAdapter extends PasswordProtectedAdapter {
    protected JBossAdapter(String userName, String password) {
        super(userName, password);
    }
}
