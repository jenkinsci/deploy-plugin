package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.util.VariableResolver;

import org.apache.commons.lang.StringUtils;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.cargo.container.deployable.EAR;

/**
 * Provides container-specific glue code.
 *
 * <p>
 * To support remote operations as an inner class, marking the class as
 * serializable.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter extends ContainerAdapter implements Serializable {

    /**
     * Returns the container ID used by Cargo.
     *
     * @return
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     *
     * @param config
     * @param envVars 
     * @param variableResolver 
     */
    protected abstract void configure(Configuration config);
    
    /**
     * Fills in the {@link Configuration} object supporting environment variables.
     *
     * @param config
     * @param envVars 
     * @param variableResolver 
     */
    protected abstract void configure(Configuration config, VariableResolver<String> variableResolver, EnvVars envVars);
    
	protected Container getContainer(ConfigurationFactory configFactory,
									 ContainerFactory containerFactory, String id, VariableResolver<String> variableResolver, EnvVars envVars) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config, variableResolver, envVars);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }

    protected void deploy(DeployerFactory deployerFactory, final BuildListener listener, Container container, File f, String contextPath) {
        Deployer deployer = deployerFactory.createDeployer(container);

        listener.getLogger().println("Deploying " + f + " to container " + container.getName());
        deployer.setLogger(new LoggerImpl(listener.getLogger()));

        String extension = FilenameUtils.getExtension(f.getAbsolutePath());
        if ("WAR".equalsIgnoreCase(extension)) {
            WAR war = createWAR(f);
            if (!StringUtils.isEmpty(contextPath)) {
                war.setContext(contextPath);
            }
            deployer.redeploy(war);
        } else if ("EAR".equalsIgnoreCase(extension)) {
            EAR ear = createEAR(f);
            deployer.redeploy(ear);
        } else {
            throw new RuntimeException("Extension File Error.");
        }
    }

    /**
     * Creates a Deployable object WAR from the given file object.
     *
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A Deployable object.
     */
    protected WAR createWAR(File deployableFile) {
        return new WAR(deployableFile.getAbsolutePath());
    }

    /**
     * Creates a Deployable object EAR from the given file object.
     *
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A Deployable object.
     */
    protected EAR createEAR(File deployableFile) {
        return new EAR(deployableFile.getAbsolutePath());
    }

    @Override
	public boolean redeploy(FilePath war, final String contextPath, final AbstractBuild<?, ?> build,
							Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
    	
    	final VariableResolver<String> variableResolver = build.getBuildVariableResolver(); 
    	final EnvVars envVars = build.getEnvironment(listener);
    	
        return war.act(new FileCallable<Boolean>() {
            public Boolean invoke(File f, VirtualChannel channel) throws IOException {
                if (!f.exists()) {
                    listener.error(Messages.DeployPublisher_NoSuchFile(f));
                    return true;
                }
                ClassLoader cl = getClass().getClassLoader();
                final ConfigurationFactory configFactory = new DefaultConfigurationFactory(cl);
                final ContainerFactory containerFactory = new DefaultContainerFactory(cl);
                final DeployerFactory deployerFactory = new DefaultDeployerFactory(cl);

				Container container = getContainer(configFactory,
												   containerFactory, 
												   getContainerId(), 
												   variableResolver,
												   envVars);
				
                deploy(deployerFactory, listener, container, f, contextPath);
                return true;
            }
        });
    }
}
