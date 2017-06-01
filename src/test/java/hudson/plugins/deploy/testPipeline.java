package hudson.plugins.deploy;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test pipeline compat
 */
public class testPipeline {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void testSimpleCase() throws Exception {
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("node {" +
                    "deploy(mockDeploy('bob')) \n"+
                "}"));
        rule.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }
}
