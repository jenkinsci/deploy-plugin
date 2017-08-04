package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.AbstractProject;
import hudson.plugins.deploy.glassfish.GlassFish3xAdapter;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Confirms that old adapters are serialized and deserialized correctly
 *
 * @author Alex Johnson
 */
public class PasswordProtectedAdapterCargoTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    // these need to match what is configured in the @LocalData resource .zip
    private String username0 = "admin";
    private String password0 = "schoolbus";
    private String username1 = "manager";
    private String password1 = "lighthouse";

    @Test
    @LocalData
    public void testMigrateOldPLainPassword () throws Exception {
        AbstractProject project = j.getInstance().getItemByFullName("plainPassword", AbstractProject.class);
        DeployPublisher deployer = (DeployPublisher) project.getPublishersList().get(DeployPublisher.class);

        GlassFish3xAdapter adapter = (GlassFish3xAdapter) deployer.getAdapters().get(0);
        adapter.loadCredentials(project);

        // adapter returns correct username and password
        assertEquals(username0, adapter.getUsername());
        assertEquals(password0, adapter.getPassword());
    }

    @Test
    @LocalData
    public void testMigrateOldScrambledPassword () throws Exception {
        AbstractProject project = j.getInstance().getItemByFullName("scrambledPassword", AbstractProject.class);
        DeployPublisher deployer = (DeployPublisher) project.getPublishersList().get(DeployPublisher.class);

        GlassFish3xAdapter adapter = (GlassFish3xAdapter) deployer.getAdapters().get(0);
        adapter.loadCredentials(project);

        // adapter returns correct username and password
        assertEquals(username1, adapter.getUsername());
        assertEquals(password1, adapter.getPassword());
    }

    @Test
    @LocalData
    public void testMatchGeneratedCredentials () throws Exception {
        // create 2 projects and first build
        AbstractProject project0 = j.getInstance().getItemByFullName("scrambledPassword", AbstractProject.class);
        project0.scheduleBuild2(0).get();
        AbstractProject project1 = j.getInstance().getItemByFullName("samePassword", AbstractProject.class);
        project1.scheduleBuild2(0).get();
        AbstractProject project2 = j.getInstance().getItemByFullName("plainPassword", AbstractProject.class);
        project2.scheduleBuild2(0).get();

        StandardUsernamePasswordCredentials cred0 = extractCredentials(project0);
        StandardUsernamePasswordCredentials cred1 = extractCredentials(project1);
        StandardUsernamePasswordCredentials cred2 = extractCredentials(project2);

        assertEquals(cred0, cred1);
        assertNotEquals(cred0, cred2);
        assertNotEquals(cred1, cred2);
    }

    private StandardUsernamePasswordCredentials extractCredentials(AbstractProject project) {
        DeployPublisher publisher = (DeployPublisher) project.getPublishersList().get(DeployPublisher.class);
        String id = ((PasswordProtectedAdapterCargo) publisher.getAdapters().get(0)).getCredentialsId();
        return CredentialsProvider.findCredentialById(id,
                StandardUsernamePasswordCredentials.class, project.getFirstBuild());
    }
}
