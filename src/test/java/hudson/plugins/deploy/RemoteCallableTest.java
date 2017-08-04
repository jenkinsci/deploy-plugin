/*
 * The MIT License
 *
 * Copyright (c) 2017, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.FilePath;
import hudson.model.*;
import hudson.plugins.deploy.tomcat.Tomcat8xAdapter;
import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;

/**
 * Tests that deployment can be called from a remote agent.
 *
 * @author Alex Johnson
 */
public class RemoteCallableTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testCallableSerialization () throws Exception {
        j.jenkins.setNumExecutors(0);
        Slave s = j.createOnlineSlave();

        /* create fake war, credentials and adapter to test RemoteCallable serialization */
        FreeStyleProject project = j.createFreeStyleProject();
        project.setAssignedNode(s);
        project.scheduleBuild2(0).get(); // touch workspace

        FilePath ws = s.getWorkspaceFor(project);
        FilePath war = ws.createTempFile("simple", ".war");

        CredentialsProvider.lookupStores(Jenkins.getInstance()).iterator().next().addCredentials(Domain.global(),
                new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "test-id", "", "user", "pass"));

        ArrayList<ContainerAdapter> adapters = new ArrayList<ContainerAdapter>();
        adapters.add(new Tomcat8xAdapter("http://example.com", "test-id"));
        project.getPublishersList().add(
                new DeployPublisher(adapters, war.getName(), "/app", false));

        Run run = project.scheduleBuild2(0).get();
        j.assertBuildStatus(Result.FAILURE, run); // should fail because Tomcat DNE
        j.assertLogContains("java.io.FileNotFoundException: http://example.com/manager/text/list", run);
    }
}
