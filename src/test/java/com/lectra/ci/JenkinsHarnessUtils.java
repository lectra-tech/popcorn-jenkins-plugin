package com.lectra.ci;

import hudson.security.csrf.CrumbIssuer;
import jenkins.model.Jenkins;

public class JenkinsHarnessUtils {
  /* prevent instantiation */
  private JenkinsHarnessUtils() {}

  public static String addCrumb(Jenkins jenkins, String relativePath) {
    CrumbIssuer issuer = jenkins.getCrumbIssuer();
    String crumbName = issuer.getDescriptor().getCrumbRequestField();
    String crumb = issuer.getCrumb(null);
    if (relativePath.indexOf('?') == -1) {
      return relativePath + "?" + crumbName + "=" + crumb;
    }
    return relativePath + "&" + crumbName + "=" + crumb;
  }
}
