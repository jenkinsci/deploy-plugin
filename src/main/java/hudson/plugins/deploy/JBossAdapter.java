package hudson.plugins.deploy;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;

import java.net.URL;

/**
 * Base class for JBoss adapters.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class JBossAdapter extends PasswordProtectedAdapterCargo {
    public final URL url;

    protected JBossAdapter(URL url, String userName, String password) {
        super(userName, password);
        this.url = url;
    }

    @Override
    public void configure(Configuration config) {
        super.configure(config);
        config.setProperty(GeneralPropertySet.PROTOCOL,url.getProtocol());
        config.setProperty(GeneralPropertySet.HOSTNAME,url.getHost());
        int p = url.getPort();
        if(p<0) p=80;
        config.setProperty(ServletPropertySet.PORT,String.valueOf(p));
    }
}
