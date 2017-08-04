package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;
import hudson.util.VariableResolver;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import jenkins.MasterToSlaveFileCallable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.EAR;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;

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
     */
    protected abstract void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver);

    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars envVars, VariableResolver<String> resolver) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config, envVars, resolver);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }

    protected void deploy(DeployerFactory deployerFactory, final BuildListener listener, Container container, File f, String contextPath) {
        Deployer deployer = deployerFactory.createDeployer(container);

        listener.getLogger().println("Deploying " + f + " to container " + container.getName() + " with context " + contextPath);
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

    protected String expandVariable(EnvVars envVars, VariableResolver<String> resolver, String variable) {
        String temp = envVars.expand(variable);
        return Util.replaceMacro(envVars.expand(variable), resolver);
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

    public boolean redeploy(FilePath war, final String contextPath, final AbstractBuild<?, ?> build, Launcher launcher,
                            final BuildListener listener) throws IOException, InterruptedException {
        return war.act(new DeployCallable(this, getContainerId(), build.getEnvironment(listener), listener, contextPath));
    }

    public static class DeployCallable extends MasterToSlaveFileCallable<Boolean> {

        private CargoContainerAdapter adapter;
        private String containerId;
        private BuildListener listener;
        private String contextPath;
        private EnvVars envVars;

        public DeployCallable (CargoContainerAdapter adapter, String containerId, EnvVars envVars,
                               BuildListener listener, String contextPath) {
            this.adapter = adapter;
            this.containerId = containerId;
            this.envVars = envVars;
            this.listener = listener;
            this.contextPath = contextPath;
        }

        public Boolean invoke(File f, VirtualChannel channel) throws IOException {
            if (!f.exists()) {
                listener.error(Messages.DeployPublisher_NoSuchFile(f));
                return true;
            }

            ClassLoader cl = getClass().getClassLoader();
            DeployerFactory deployerFactory = new DefaultDeployerFactory(cl);
            ConfigurationFactory configFactory = new DefaultConfigurationFactory(cl);
            ContainerFactory containerFactory = new DefaultContainerFactory(cl);

            VariableResolver<String> resolver = new VariableResolver.ByMap<String>(envVars);
            Container container = adapter.getContainer(configFactory, containerFactory, containerId, envVars, resolver);
            adapter.deploy(deployerFactory, listener, container, f, Util.replaceMacro(envVars.expand(contextPath), resolver));

            return true;
        }
    }
}
