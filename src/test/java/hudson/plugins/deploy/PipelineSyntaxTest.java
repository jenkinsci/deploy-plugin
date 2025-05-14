package hudson.plugins.deploy;

import hudson.model.Result;
import hudson.plugins.deploy.tomcat.Tomcat8xAdapter;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.cps.SnippetizerTester;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.CoreStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.Collections;

/**
 * Tests pipeline compatibility. Since there is no Builder that sets the build status all of these tests
 * will ultimately result in a no-op.
 */
@WithJenkins
class PipelineSyntaxTest {

    private JenkinsRule j;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
    }

    private String getFullScript (String func) {
        return "node {\n" +
                    "writeFile(file: 'readme.txt', text: 'this creates a workspace if one doesnt already exist')\n" +
                    func +
                "}";
    }

    @Test
    void testNoAdapterDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "DryRunTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(war: 'target/app.war', contextPath: 'app', onFailure: false)"),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        // we expect a failed build status because there are no WAR files to deploy
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("No wars found. Deploy aborted.", r);
    }

    @Test
    void testMockAdapterDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(adapters: [workflowAdapter()], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        // we expect a failed build status because there are no WAR files to deploy
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("No wars found. Deploy aborted.", r);
    }

    @Test
    void testMockAdaptersDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(adapters: [workflowAdapter(), workflowAdapter(), workflowAdapter()], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        // we expect a failed build status because there are no WAR files to deploy
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("No wars found. Deploy aborted.", r);
    }

    @Test
    void testGlassFishAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "GlassfishTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                        """
                                def gf2 = glassfish2(
                                    home: 'FAKE',
                                    credentialsId: 'FAKE',
                                    adminPort: '1234')
                                def gf3 = glassfish3(
                                    home: 'FAKE',
                                    credentialsId: 'FAKE',
                                    adminPort: '1234',
                                    hostname: 'localhost')
                                deploy(adapters: [gf2, gf3], war: 'target/app.war', contextPath: 'app')"""),
                false));

        WorkflowRun r = p.scheduleBuild2(0).get();
        // we expect a failed build status because there are no WAR files to deploy
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("No wars found. Deploy aborted.", r);
    }

    @Test
    void testTomcatAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "TomcatTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                        """
                                def tc7 = tomcat7(
                                    url: 'FAKE',
                                    credentialsId: 'FAKE')
                                def tc8 = tomcat8(
                                    credentialsId: 'FAKE')
                                deploy(adapters: [tc7, tc8], war: 'target/app.war', contextPath: 'app')"""),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        // we expect a failed build status because there are no WAR files to deploy
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("No wars found. Deploy aborted.", r);
    }

    @Test
    void testLegacyAdapterThrows() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "legacyTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                        "writeFile(file: 'target/app.war', text: '')\n" +
                        "deploy(adapters: [legacyAdapter()], war: 'target/app.war', contextPath: 'app', onFailure: true)"),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        j.assertBuildStatus(Result.FAILURE, r);
        j.assertLogContains("Please contact the plugin maintainer", r);
    }

    @Test
    void testSnippetizerDefaults() throws Exception {
        j.getInstance().createProject(WorkflowJob.class, "SnippetTest");
        SnippetizerTester t = new SnippetizerTester(j);

        ContainerAdapter tc = new Tomcat8xAdapter("http://example.com", "test-id", null, null);
        DeployPublisher dp = new DeployPublisher(Collections.singletonList(tc), "app.war");

        t.assertRoundTrip(new CoreStep(dp), "deploy adapters: [tomcat8(credentialsId: 'test-id', url: 'http://example.com')], war: 'app.war'");
    }

    @Test
    void testSnippetizerNonDefault() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "SnippetTest");
        SnippetizerTester t = new SnippetizerTester(j);

        ContainerAdapter tc = new Tomcat8xAdapter("http://example.com", "test-id", null, null);
        DeployPublisher dp = new DeployPublisher(Collections.singletonList(tc), "app.war");
        dp.setOnFailure(!j.jenkins.getDescriptorByType(DeployPublisher.DescriptorImpl.class).defaultOnFailure(p));
        dp.setContextPath("my-app");

        t.assertRoundTrip(new CoreStep(dp), "deploy adapters: [tomcat8(credentialsId: 'test-id', url: 'http://example.com')], contextPath: 'my-app', onFailure: false, war: 'app.war'");
    }

    @Test
    void testSnippetizerPath() throws Exception {
        j.getInstance().createProject(WorkflowJob.class, "SnippetTest");
        SnippetizerTester t = new SnippetizerTester(j);

        ContainerAdapter tc = new Tomcat8xAdapter("http://example.com", "test-id", null, "/foo-manager/text");
        DeployPublisher dp = new DeployPublisher(Collections.singletonList(tc), "app.war");

        t.assertRoundTrip(new CoreStep(dp), "deploy adapters: [tomcat8(credentialsId: 'test-id', path: '/foo-manager/text', url: 'http://example.com')], war: 'app.war'");
    }
}
