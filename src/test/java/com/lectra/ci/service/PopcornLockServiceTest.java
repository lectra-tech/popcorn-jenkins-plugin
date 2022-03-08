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
package com.lectra.ci.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** The type Master test. */
public class PopcornLockServiceTest {
  /** The Jenkins rule. */
  @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

  private PopcornLockService popcornLockService;

  /** Init. */
  @Before
  public void init() {
    popcornLockService = new PopcornLockService(jenkinsRule.jenkins);
    Assert.assertTrue(
        "lock service should be unlocked before each test", popcornLockService.unlock());
  }

  /** Lock master. */
  @Test
  public void lockMaster() {
    popcornLockService.lock();
    Assert.assertTrue(popcornLockService.isLocked());
  }

  /** Lock then unlock master. */
  @Test
  public void lockThenUnlockMaster() {
    popcornLockService.lock();
    Assert.assertTrue(popcornLockService.isLocked());
    popcornLockService.unlock();
    Assert.assertFalse(popcornLockService.isLocked());
  }

  /** Unlock master when not locked. */
  @Test
  public void unlockMasterWhenNotLocked() {
    Assert.assertFalse(popcornLockService.isLocked());
    popcornLockService.unlock();
    Assert.assertFalse(popcornLockService.isLocked());
  }

  /** Lock master twice. */
  @Test
  public void lockMasterTwice() {
    popcornLockService.lock();
    Assert.assertTrue(popcornLockService.isLocked());
    popcornLockService.lock();
    Assert.assertTrue(popcornLockService.isLocked());
  }
}
