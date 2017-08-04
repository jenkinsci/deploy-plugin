package hudson.plugins.deploy;

import hudson.model.Descriptor;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;

import org.kohsuke.stapler.QueryParameter;

/**
 * Base class for {@link ContainerAdapter} descriptors.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ContainerAdapterDescriptor extends Descriptor<ContainerAdapter> {

    protected ContainerAdapterDescriptor(Class<? extends ContainerAdapter> clazz) {
        super(clazz);
    }

    protected ContainerAdapterDescriptor() {
    }

    public FormValidation doCheckUrl(@QueryParameter String value) throws IOException, ServletException {
        if (value != null && value.length() > 0) {
            try {
                new URL(value);
            } catch (Exception e) {
                return FormValidation.warning(Messages.DeployPublisher_BadFormedUrl());
            }
        }

        return FormValidation.ok();
    }
}
