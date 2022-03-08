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

import com.lectra.ci.PopcornPlugin;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 * Service allowing to handle a locking mechanism preventing several actions to occure while locked.
 */
public class PopcornLockService {
  /** The LOGGER to use. */
  private static final Logger LOGGER = Logger.getLogger(PopcornLockService.class.getName());

  /** The constant DATA_FOLDER. */
  private final File DATA_FOLDER;

  /** The constant LOCK_FILE. */
  private final File LOCK_FILE;

  public PopcornLockService(Jenkins jenkinsInstance) {
    DATA_FOLDER = new File(jenkinsInstance.getRootDir(), PopcornPlugin.class.getName());
    LOCK_FILE = new File(DATA_FOLDER, "popcorn.disabled");

    if (!DATA_FOLDER.exists()) {
      LOGGER.log(Level.INFO, "Creating folder " + DATA_FOLDER.getAbsolutePath());
      if (!DATA_FOLDER.mkdirs()) {
        LOGGER.log(Level.SEVERE, "Error creating folder " + DATA_FOLDER);
      }
    }
  }

  /**
   * Is locked boolean.
   *
   * @return the boolean
   */
  public boolean isLocked() {
    return LOCK_FILE.exists();
  }

  /**
   * Lock popcorn Jenkins instance.
   *
   * @return true if teh service is locked, false otherwise
   */
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  public boolean lock() {
    try {
      if (isLocked()) {
        return true;
      }

      // In unit tests, the directory is removed by the JenkinsRule
      LOCK_FILE.getParentFile().mkdirs();

      LOGGER.log(Level.INFO, "Creating lock file " + LOCK_FILE);
      return LOCK_FILE.createNewFile();
    } catch (IOException ioe) {
      LOGGER.log(Level.SEVERE, "Error creating lock file " + LOCK_FILE, ioe);
    }

    return false;
  }

  /**
   * Unlock popcorn Jenkins instance.
   *
   * @return true if the service is unlocked, false otherwise
   */
  public boolean unlock() {
    if (isLocked()) {
      LOGGER.log(Level.INFO, "Deleting lock file " + LOCK_FILE);
      boolean lockFileDeleted = LOCK_FILE.delete();
      if (!lockFileDeleted) {
        LOGGER.log(Level.SEVERE, "cannot delete lock file " + LOCK_FILE);
      }
      return lockFileDeleted;
    }

    return true;
  }
}
