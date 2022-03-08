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

import hudson.util.FormValidation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** The type Popcorn management link test. */
public class PopcornManagementLinkTest {

  private PopcornManagementLink popcornManagementLink;

  /** The Jenkins rule. */
  @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

  /** Init. */
  @Before
  public void init() {
    this.popcornManagementLink = new PopcornManagementLink();
  }

  /** Default values are not null. */
  @Test
  public void defaultValuesAreNotNull() {
    Assert.assertNotNull(popcornManagementLink.getDisplayName());
    Assert.assertNotNull(popcornManagementLink.getUrlName());
    Assert.assertNotNull(popcornManagementLink.getDescription());
  }

  /** Gets descriptor from jenkins instance. */
  @Test
  public void getDescriptorFromJenkinsInstance() {
    Assert.assertNotNull(popcornManagementLink.getDescriptor());
    Assert.assertEquals(
        "PopcornManagementLink", popcornManagementLink.getDescriptor().getDisplayName());
    Assert.assertEquals(
        "descriptorByName/com.lectra.ci.ui" + ".PopcornManagementLink",
        popcornManagementLink.getDescriptor().getDescriptorUrl());
  }

  /** Should lock master. */
  @Test
  public void shouldLockMaster() {
    PopcornManagementLink.PopcornManagementLinkDescriptor descriptor =
        new PopcornManagementLink.PopcornManagementLinkDescriptor();
    Assert.assertEquals(FormValidation.ok("locked").toString(), descriptor.doLock().toString());
  }

  /** Should lock then unlock master. */
  @Test
  public void shouldLockThenUnlockMaster() {
    PopcornManagementLink.PopcornManagementLinkDescriptor descriptor =
        new PopcornManagementLink.PopcornManagementLinkDescriptor();
    descriptor.doLock();
    Assert.assertEquals(FormValidation.ok("unlocked").toString(), descriptor.doUnlock().toString());
  }
}
