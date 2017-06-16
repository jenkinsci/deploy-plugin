package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.model.*;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat8xRemoteContainer;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author frekele
 */
public class Tomcat8xAdapterTest {

    private Tomcat8xAdapter adapter;
    private static final String url = "http://localhost:8080";
    private static final String configuredUrl = "http://localhost:8080/manager/text";
    private static final String urlVariable = "URL";
    private static final String username = "usernm";
    private static final String usernameVariable = "USER";
    private static final String password = "password";
    private static final String variableStart = "${";
    private static final String variableEnd = "}";
    
    @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setup() {
        adapter = new  Tomcat8xAdapter(url, password, username);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new Tomcat8xRemoteContainer(null).getId());            
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.url,url);
        Assert.assertEquals(adapter.userName,username);
        Assert.assertEquals(adapter.getPassword(),password);
    }
    
    @Test
    public void testVariables() throws Exception {
        Node n = jenkinsRule.createSlave();
    	EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty();

    	EnvVars envVars = property.getEnvVars();
    	envVars.put(urlVariable, url);
    	envVars.put(usernameVariable, username);
    	jenkinsRule.jenkins.getGlobalNodeProperties().add(property);

        FreeStyleProject project = jenkinsRule.getInstance().createProject(FreeStyleProject.class, "fsp");
        project.setAssignedNode(n);
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        adapter = new Tomcat8xAdapter(getVariable(urlVariable), password, getVariable(usernameVariable));
        Configuration config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        Assert.assertEquals(configuredUrl, config.getPropertyValue(RemotePropertySet.URI));
        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
    }
    
    private String getVariable(String variableName) {
    	return variableStart + variableName + variableEnd;
    }
}
