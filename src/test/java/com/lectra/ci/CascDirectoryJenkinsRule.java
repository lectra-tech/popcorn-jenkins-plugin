package com.lectra.ci;

import io.jenkins.plugins.casc.ConfigurationAsCode;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.JenkinsRule;

public class CascDirectoryJenkinsRule extends JenkinsRule {
  File tempDirectory = null;
  String previousCascProperty = null;

  @Override
  public void before() throws Throwable {
    File tempFile = File.createTempFile("casc", "its");
    String tempFilePath = tempFile.toString();
    tempFile.delete();
    tempDirectory = new File(tempFilePath);
    tempDirectory.mkdirs();
    previousCascProperty =
        System.setProperty(
            ConfigurationAsCode.CASC_JENKINS_CONFIG_PROPERTY, tempDirectory.toString());

    super.before();
  }

  @Override
  public void after() throws Exception {
    super.after();
    if (previousCascProperty == null) {
      System.clearProperty(ConfigurationAsCode.CASC_JENKINS_CONFIG_PROPERTY);
    } else {
      System.setProperty(ConfigurationAsCode.CASC_JENKINS_CONFIG_PROPERTY, previousCascProperty);
      previousCascProperty = null;
    }

    if (tempDirectory != null && tempDirectory.exists()) {
      FileUtils.deleteDirectory(tempDirectory);
    }
  }
}
