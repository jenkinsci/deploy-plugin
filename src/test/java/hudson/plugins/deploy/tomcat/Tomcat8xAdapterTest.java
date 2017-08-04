package hudson.plugins.deploy.tomcat;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.StreamBuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat8xRemoteContainer;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
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
    private static final String usernameVariable = "user";
    private static final String password = "password";
    private static final String variableStart = "${";
    private static final String variableEnd = "}";
    
    @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "test", "sample", username, password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new  Tomcat8xAdapter(url, c.getId());
        adapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new Tomcat8xRemoteContainer(null).getId());            
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(url, adapter.url);
        Assert.assertEquals(username, adapter.getUsername());
        Assert.assertEquals(password, adapter.getPassword());
    }

    @Test
    public void testVariables() throws Exception {
        Node n = jenkinsRule.createSlave();
    	EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty();
    	EnvVars envVars = property.getEnvVars();
    	envVars.put(urlVariable, url);
    	envVars.put(usernameVariable, username);
    	jenkinsRule.jenkins.getGlobalNodeProperties().add(property);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.setAssignedNode(n);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, null,
                "", getVariable(usernameVariable), password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new  Tomcat8xAdapter(getVariable(urlVariable), c.getId());
        Configuration config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.migrateCredentials(Collections.EMPTY_LIST);
        adapter.loadCredentials(project);
        adapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        Assert.assertEquals(configuredUrl, config.getPropertyValue(RemotePropertySet.URI));
        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
    }
    
    private String getVariable(String variableName) {
    	return variableStart + variableName + variableEnd;
    }
}
