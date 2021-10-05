[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/deploy.svg)](https://plugins.jenkins.io/deploy)
[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/i/deploy.svg)](https://plugins.jenkins.io/deploy)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/deploy-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fdeploy-plugin/branches)
[![javadoc](https://img.shields.io/badge/javadoc-available-brightgreen.svg)](https://javadoc.jenkins.io/plugin/deploy/)
[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE.md)


Jenkins Deploy Plugin
=========================

This plugin permits Jenkins to Deploy into containers and application servers. It takes a war/ear file and deploys that to a running remote application server at the end of a build. The implementation is based on [Cargo](http://cargo.codehaus.org/). The list of currently supported containers include:

-   Tomcat 4.x/5.x/6.x/7.x/8.x/9.x
-   JBoss 3.x/4.x/5.x/6.x/7.x
-   Glassfish 2.x/3.x/4.x

Refer to the [Deploy WebSphere
Plugin](https://plugins.jenkins.io/websphere-deployer/)
to deploy to a running remote WebSphere Application Server.  
Refer to the [WebLogic Deployer
Plugin](https://plugins.jenkins.io/weblogic-deployer-plugin/)
to deploy to a running remote WebLogic Application Server.

#### How to rollback or redeploy a previous build

There may be several ways to accomplish this, but here is one suggested
method:

1.  Install the [Copy Artifact
    Plugin](https://plugins.jenkins.io/copyartifact/)
2.  Create a new job that you will trigger manually only when needed
3.  Configure this job with a build parameter of type "Build selector
    for Copy Artifact", and a copy artifact build step using "Specified
    by build parameter" to select the build.
4.  Add a post-build action to deploy the artifact that was copied from
    the other job

Now when you trigger this job you can enter the build number (or use any
other available selector) to select which build to redeploy. Thanks to
Helge Taubert for this idea.

## Change Log

##### Version 1.16 (Nov 3, 2020)

-   Call XStream.processAnnotations (#[43](https://github.com/jenkinsci/deploy-plugin/pull/43))
-   Updated parent to latest version (#[37](https://github.com/jenkinsci/deploy-plugin/pull/37))
-   Changed to azure container instances (#[41](https://github.com/jenkinsci/deploy-plugin/pull/41))

##### Version 1.15 (Aug 1, 2019)

-   Configure alternative context for each Tomcat container
    ([JENKINS-51440](https://issues.jenkins.io/browse/JENKINS-51440))

##### Version 1.14 (Jul 24, 2019)

-   deployment plugin does not show any error message when the war file
    does not exist
    ([JENKINS-13219](https://issues.jenkins-ci.org/browse/JENKINS-13219))

-   Nothing happens after build
    ([JENKINS-12760](https://issues.jenkins-ci.org/browse/JENKINS-12760))

-   Add Deployment feature for Tomcat 9
    ([JENKINS-55333](https://issues.jenkins-ci.org/browse/JENKINS-55333))

-   Tomcat deploy transfer speed
    ([JENKINS-40428](https://issues.jenkins-ci.org/browse/JENKINS-40428))

-   Add support for Jenkins Pipeline
    ([JENKINS-44810](https://issues.jenkins-ci.org/browse/JENKINS-44810))

-   Allow expansion of environment variables in the configuration
    ([JENKINS-12825](https://issues.jenkins-ci.org/browse/JENKINS-12825))

##### Version 1.13 (August 7, 2017)

-   [Security
    fix](https://jenkins.io/security/advisory/2017-08-07/#deploy-to-container-plugin-stored-plain-text-passwords-in-job-configuration)

##### Version 1.10 (Jul 2, 2014)

-   Support deployment to multiple targets
    ([JENKINS-4949](https://issues.jenkins-ci.org/browse/JENKINS-4949))
-   Expand variable references in the context path
    ([JENKINS-5790](https://issues.jenkins-ci.org/browse/JENKINS-5790))
-   Added JBoss 6 and 7
    ([JENKINS-19256](https://issues.jenkins-ci.org/browse/JENKINS-19256))

##### Version 1.9 (Jun 11, 2012)

-   Password in config.xml is now scrambled ([pull
    \#6](https://github.com/jenkinsci/deploy-plugin/pull/6))
    -   This change is backward-compatible but is not forward-compatible
-   The context path can now also be spericied
    ([JENKINS-9093](https://issues.jenkins-ci.org/browse/JENKINS-9093))

##### Version 1.8 (Jun 28, 2011)

-   GlassFish v3 remote deployment ([pull
    \#3](https://github.com/jenkinsci/deploy-plugin/pull/3))

##### Version 1.7 (Mar 11, 2011)

-   Fix Tomcat 7 deployement url

##### Version 1.6 (Dec 10, 2010)

-   Added Tomcat 7 and GlassFish 3 support
-   Fixed bug in GlassFishAdapter, need to explicitly set the home on
    the container
-   Updated library to Cargo 1.0.4

##### Version 1.5 (Jan 16, 2010)

-   Support Ant style GLOBs for specifying war/ear files
    ([JENKINS-5166](https://issues.jenkins-ci.org/browse/JENKINS-5166))

##### Version 1.4 (Dec 30, 2009)

-   Update library to Cargo 1.0
-   Only deploy if the build was successful, unless "even when failed"
    option is checked
-   Check URL format when saving config
-   Update code for more recent Hudson
-   Add initial glassfish support

##### Version 1.3 (Aug 5, 2008)

-   This plugin didn't work on slaves
    ([report](http://www.nabble.com/Tomcat-deploy-fails-on-slave-agent-%28%27Deploy-war-to-container%27-action%29-td18747851.html),[JENKINS-2114](https://issues.jenkins-ci.org/browse/JENKINS-2114))

##### Version 1.2 (Jul 11, 2008)

-   Fixed the problem in submitting the configuration. Make sure to run
    this with 1.234 or later.
    ([report](http://www.nabble.com/Error-saving-on-%22Deploy-war-to-a-container%22-td18387294.html))

See [Deploy Plugin](https://plugins.jenkins.io/deploy) for more information.


License
-------

	The MIT License (MIT)

    Copyright (c) 2014-2019 <copyright holders>

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
