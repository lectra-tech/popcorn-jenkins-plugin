/*
 * Copyright © 2022 LECTRA (rd.opensource@lectra.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lectra.ci.ui;

import com.lectra.ci.service.PopcornLockService;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import hudson.util.FormValidation;
import javax.annotation.CheckForNull;
import jenkins.model.Jenkins;
import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.verb.POST;

/** The type Popcorn management link. */
@Extension
public class PopcornManagementLink extends ManagementLink
    implements Describable<PopcornManagementLink> {

  /** The constant resourceBundleHolder. */
  private static final ResourceBundleHolder RESOURCE_BUNDLE_HOLDER =
      ResourceBundleHolder.get(PopcornManagementLink.class);

  /**
   * Gets icon file name.
   *
   * @return the icon file name
   */
  @CheckForNull
  @Override
  public String getIconFileName() {
    // the default path for this kind og icon file is images 48x48
    return "/plugin/lectra-popcorn/images/48x48/popcorn.png";
  }

  /**
   * Gets display name.
   *
   * @return the display name
   */
  @CheckForNull
  @Override
  public String getDisplayName() {
    return RESOURCE_BUNDLE_HOLDER.format("display.name");
  }

  /**
   * Gets url name.
   *
   * @return the url name
   */
  @CheckForNull
  @Override
  public String getUrlName() {
    return "popcorn-management";
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  @Override
  public String getDescription() {
    return RESOURCE_BUNDLE_HOLDER.format("description");
  }

  /**
   * Gets descriptor.
   *
   * @return the descriptor
   */
  @Override
  public Descriptor<PopcornManagementLink> getDescriptor() {
    return Jenkins.get().getDescriptorOrDie(getClass());
  }

  /** The type Popcorn management link descriptor. */
  @Extension
  public static class PopcornManagementLinkDescriptor extends Descriptor<PopcornManagementLink> {

    /**
     * Do unlock form validation.
     *
     * @return the form validation
     */
    @POST
    public FormValidation doUnlock() {
      if (new PopcornLockService(Jenkins.get()).unlock()) {
        return FormValidation.ok("unlocked");
      } else {
        return FormValidation.error("Error unlocking master");
      }
    }

    /**
     * Do lock form validation.
     *
     * @return the form validation
     */
    @POST
    public FormValidation doLock() {
      if (new PopcornLockService(Jenkins.get()).lock()) {
        return FormValidation.ok("locked");
      } else {
        return FormValidation.error("Error locking master");
      }
    }
  }
}
