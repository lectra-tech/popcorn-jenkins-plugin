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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** The type Popcorn page decorator test. */
public class PopcornPageDecoratorTest {

  /** The Jenkins rule. */
  @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

  /** Popcorn plugin should not be locked. */
  @Test
  public void popcornPluginShouldNotBeLocked() {
    Assert.assertFalse(
        "lock should not be active", new PopcornLockService(jenkinsRule.jenkins).isLocked());
    PopcornPageDecorator popcornPageDecorator = new PopcornPageDecorator();
    Assert.assertFalse(
        "lock status should not be reported in UI", popcornPageDecorator.getShouldDisplay());
  }

  /** Popcorn plugin should be locked. */
  @Test
  public void popcornPluginShouldBeLocked() {
    PopcornPageDecorator popcornPageDecorator = new PopcornPageDecorator();
    Assert.assertTrue("lock should be active", new PopcornLockService(jenkinsRule.jenkins).lock());
    Assert.assertTrue(
        "lock status should be reported in UI", popcornPageDecorator.getShouldDisplay());
  }
}
