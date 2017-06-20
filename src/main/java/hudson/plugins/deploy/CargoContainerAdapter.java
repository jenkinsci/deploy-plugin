package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import hudson.util.VariableResolver;
import jenkins.MasterToSlaveFileCallable;
import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;
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
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

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
     * @return the id of the container
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     *
     * @param config the configuration of the adapter
     * @param envVars the environmental variables of the build
     * @param resolver the variable resolver
     */
    protected abstract void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver);

    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars envVars, VariableResolver<String> resolver) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config, envVars, resolver);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }

    protected void deploy(DeployerFactory deployerFactory, final TaskListener listener, Container container, File f, String contextPath) {
        Deployer deployer = deployerFactory.createDeployer(container);

        listener.getLogger().println("[DeployPublisher][INFO] Deploying " + f + " to container " + container.getName() + " with context " + contextPath);
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
            throw new RuntimeException("Extension File Error. Unsupported: .\"" + extension + "\"");
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
     * Expands an encoded environment variable. Ex. if HOME=/user/alex, expands '${HOME}' to '/user/alex'
     *
     * @param envVars the environment variables of the build
     * @param resolver unused
     * @param variable the variable to expand
     * @return the value of the expanded variable
     */
    protected String expandVariable(EnvVars envVars, VariableResolver<String> resolver, String variable) {
        return Util.replaceMacro(envVars.expand(variable), resolver);
    }

    /**
     * Creates a Deployable object EAR from the given file object.
     *
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A deployable object.
     */
    protected EAR createEAR(File deployableFile) {
        return new EAR(deployableFile.getAbsolutePath());
    }

    @Override
    public boolean redeploy(FilePath war, final String contextPath, final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        return redeploy(war, contextPath, build, launcher, (TaskListener)listener);
    }

    public boolean redeploy(FilePath war, final String contextPath, final Run<?, ?> run, final Launcher launcher, final TaskListener listener) throws IOException, InterruptedException {
        return war.act(new MasterToSlaveFileCallable<Boolean>() {
            @Override
            public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {

                ClassLoader cl = getClass().getClassLoader();
                final ConfigurationFactory configFactory = new DefaultConfigurationFactory(cl);
                final ContainerFactory containerFactory = new DefaultContainerFactory(cl);
                final DeployerFactory deployerFactory = new DefaultDeployerFactory(cl);

                try {
                    final EnvVars envVars = getAllEnvVars(run, listener);
                    final VariableResolver<String> resolver = new VariableResolver.ByMap<String>(envVars);
                    Container container = getContainer(configFactory, containerFactory, getContainerId(), envVars, resolver);
                    deploy(deployerFactory, listener, container, f, expandVariable(envVars, resolver, contextPath));
                } catch (IOException e) {
                    listener.getLogger().println("[DeployPublisher][ERROR] Could not load Run-specific environment variables");
                } catch (InterruptedException e) {
                    listener.getLogger().println("[DeployPublisher][ERROR] Could not load Run-specific environment variables");
                }
                return true;
            }
        });
    }

    /**
     * Collects some of the environment variables.
     *
     * @param run the {@Link Job}'s Run
     * @param listener the {@Link TaskListener} of the {@Link Run}
     * @return the list of configured environment variables
     */
    private EnvVars getAllEnvVars (Run<?, ?> run, TaskListener listener) throws IOException, InterruptedException{
        Jenkins j = Jenkins.getActiveInstance();
        EnvVars env = new EnvVars();

        List<EnvironmentVariablesNodeProperty> props = new ArrayList<EnvironmentVariablesNodeProperty>();
        props.addAll(j.getGlobalNodeProperties().getAll(EnvironmentVariablesNodeProperty.class));
        props.addAll(j.getNodeProperties().getAll(EnvironmentVariablesNodeProperty.class));
        for (EnvironmentVariablesNodeProperty n : props) {
                env.putAll(n.getEnvVars());
        }

        if (run instanceof AbstractBuild) {
            env.overrideAll(((AbstractBuild) run).getBuildVariables());
        }

        env.overrideAll(run.getEnvironment(listener));

        return env;
    }
}
