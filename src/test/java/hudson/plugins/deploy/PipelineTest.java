package hudson.plugins.deploy;

import hudson.model.Label;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test pipeline compat
 */
public class PipelineTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testNoAdapterDeployNoContainer() throws Exception {
        WorkflowJob p1 = j.getInstance().createProject(WorkflowJob.class, "DryRunTest");
        j.createOnlineSlave(Label.get("remote"));
        p1.setDefinition(new CpsFlowDefinition("node {" +
                "deploy(adapters: [], war: '/target/app.war', contextPath: '/app', onFailure: false) \n"+
                "}"));
        j.assertBuildStatusSuccess(p1.scheduleBuild2(0));
    }

    @Test
    public void testAdapterDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition("node { \n" +
                "def mockContainer = [$class: 'MockAdapter', containerName: 'mock'] \n" +
                "deploy(adapters: [mockContainer], war: '/target/app.war', contextPath: '/app') \n"+
                "}"));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testGlassFishAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "GlassfishTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition("node { \n" +
                "def gf2 = [$class: 'GlassFish2xAdapter', " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE', " +
                    "adminPort: '1234', " +
                    "hostname: 'localhost'] \n" +
                "def gf3 = [$class: 'GlassFish3xAdapter', " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE', " +
                    "adminPort: '1234', " +
                    "hostname: 'localhost'] \n" +
                "deploy(adapters: [gf2, gf3], war: '/target/app.war', contextPath: '/app') \n"+
                "}"));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testTomcatAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "TomcatTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition("node { \n" +
                "def tc7 = [$class: 'Tomcat7xAdapter', " +
                    "url: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE'] \n" +
                "def tc8 = [$class: 'Tomcat8xAdapter', " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE'] \n" +
                "deploy(adapters: [tc7, tc8], war: '/target/app.war', contextPath: '/app') \n"+
                "}"));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

}
