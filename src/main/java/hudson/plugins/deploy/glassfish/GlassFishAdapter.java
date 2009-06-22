package hudson.plugins.deploy.glassfish;

import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.glassfish.GlassFishPropertySet;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;

public abstract class GlassFishAdapter extends PasswordProtectedAdapterCargo {
	public final String home;
    @Property(GlassFishPropertySet.ADMIN_PORT)
    public final Integer adminPort;

    protected GlassFishAdapter(String home, String password, String userName, Integer adminPort) {
        super(userName, password);        
        this.home = home;
        this.adminPort = adminPort;
    }
    
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.STANDALONE, home);
        configure(config);
        return containerFactory.createContainer(id, ContainerType.INSTALLED, config);
    }
}
