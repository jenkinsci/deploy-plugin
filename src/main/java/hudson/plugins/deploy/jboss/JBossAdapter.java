package hudson.plugins.deploy.jboss;

import hudson.EnvVars;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import hudson.util.VariableResolver;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.jboss.JBossPropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;

/**
 * Base class for JBoss adapters.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class JBossAdapter extends PasswordProtectedAdapterCargo {

    public final String url;
    public final String portOffset;

    @Property(JBossPropertySet.JBOSS_MANAGEMENT_NATIVE_PORT)
    public final Integer managementNativePort;

    protected JBossAdapter(String url, String password, String userName, String portOffset) {
        super(userName, password);
        this.url = url;
        this.portOffset = portOffset;
        int mnp = 9999;
        try {
            mnp += Integer.parseInt(portOffset);
        } finally {
            this.managementNativePort = mnp;
        }
    }

    @Override
    public void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver) {
        super.configure(config, envVars, resolver);
        try {
            URL _url = new URL(expandVariable(envVars, resolver, url));
            config.setProperty(GeneralPropertySet.PROTOCOL, _url.getProtocol());
            config.setProperty(GeneralPropertySet.HOSTNAME, _url.getHost());
            int p = _url.getPort();
            if (p < 0) {
                p = 80;
            }
            config.setProperty(ServletPropertySet.PORT, String.valueOf(p));
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }
}
