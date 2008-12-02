package hudson.plugins.deploy.glassfish;

import hudson.plugins.deploy.PasswordProtectedAdapterCargo;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;

public abstract class GlassFishAdapter extends PasswordProtectedAdapterCargo {
	public final String url;

    protected GlassFishAdapter(String url, String password, String userName) {    	
        super(userName, password);        
        this.url = url;
    }
    
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {    	
        Configuration config = configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.RUNTIME, url);        
        configure(config);
        return containerFactory.createContainer(id, ContainerType.INSTALLED, config);
    }    
}
