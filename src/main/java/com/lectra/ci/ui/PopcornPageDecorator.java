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
import hudson.model.PageDecorator;
import jenkins.model.Jenkins;

/** The type Popcorn page decorator. */
@Extension
public class PopcornPageDecorator extends PageDecorator {

  /**
   * Gets should display.
   *
   * @return the should display
   */
  public boolean getShouldDisplay() {
    return new PopcornLockService(Jenkins.get()).isLocked();
  }
}
