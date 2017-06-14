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

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.*;

/**
 * @author Alex Johnson
 */
public class DeployStep extends Step {

    private static final String FUNC_NAME = "deploy";

    private DeployPublisher pub;

    @DataBoundConstructor
    public DeployStep (ContainerAdapter container, ContainerAdapter[] containers, String war, String contextPath, boolean onFailure) {
        if (container == null) {
            if (containers == null) {
                containers = new ContainerAdapter[0];
            }
            pub = new DeployPublisher(Arrays.asList(containers), war, contextPath, onFailure);
        } else {
            pub = new DeployPublisher(Arrays.asList(container), war, contextPath, onFailure);
        }
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new DeployStep.ExecutionImpl(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return FUNC_NAME;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, Launcher.class, TaskListener.class);
        }

        @Override
        public String getDisplayName() {
            return "about";
        }
    }

    public static final class ExecutionImpl extends SynchronousNonBlockingStepExecution<Void> {

        private transient final DeployStep step;
        /** The serial ID */
        private static final long serialVersionUID = 1L;

        ExecutionImpl(DeployStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        /** Runs the DeployPublisher */
        @Override
        protected Void run() throws Exception {
            Run r = getContext().get(Run.class);
            if (r instanceof AbstractBuild) {
                Launcher launcher = getContext().get(Launcher.class);
                BuildListener listener = (BuildListener)getContext().get(TaskListener.class);
                if (step.pub.getAdapters().isEmpty()) {
                    listener.getLogger().print(" WARN: No containers specified");
                } else {
                    step.pub.perform((AbstractBuild) r, launcher, listener);
                }
            }
            return null;
        }
    }

}
