package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.servlet.ServletException;

import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
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

    protected static StandardUsernamePasswordCredentials lookupCredentials(@CheckForNull Item project, String url, String credentialId) {
        return (credentialId == null) ? null : CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, project, ACL.SYSTEM, URIRequirementBuilder.fromUri(url).build()),
                CredentialsMatchers.withId(credentialId));
    }

    @Restricted(NoExternalUse.class)
    public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item project,
                                                 @QueryParameter String url,
                                                 @QueryParameter String credentialsId) {
        if (project == null && !Jenkins.getActiveInstance().hasPermission(Jenkins.ADMINISTER) ||
                project != null && !project.hasPermission(Item.EXTENDED_READ)) {
            return new StandardListBoxModel().includeCurrentValue(credentialsId);
        }
        return new StandardListBoxModel()
                .includeEmptyValue()
                .includeMatchingAs(
                        project instanceof Queue.Task
                                ? Tasks.getAuthenticationOf((Queue.Task) project) : ACL.SYSTEM,
                        project, StandardUsernameCredentials.class, URIRequirementBuilder.fromUri(url).build(),
                        CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class))
                .includeCurrentValue(credentialsId);
    }

    public FormValidation doCheckCredentialsId(@AncestorInPath Item project,
                                               @QueryParameter String url,
                                               @QueryParameter String value) {
        if (project == null && !Jenkins.getActiveInstance().hasPermission(Jenkins.ADMINISTER) ||
                project != null && !project.hasPermission(Item.EXTENDED_READ)) {
            return FormValidation.ok();
        }

        value = Util.fixEmptyAndTrim(value);
        if (value == null) {
            return FormValidation.ok();
        }

        for (ListBoxModel.Option o : CredentialsProvider
                .listCredentials(StandardUsernameCredentials.class, project, project instanceof Queue.Task
                                ? Tasks.getAuthenticationOf((Queue.Task) project) : ACL.SYSTEM, URIRequirementBuilder.fromUri(url).build(),
                        CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class))) {
            if (StringUtils.equals(value, o.value)) {
                return FormValidation.ok();
            }
        }
        // no credentials available, can't check
        return FormValidation.warning("Cannot find any credentials with id " + value);
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
