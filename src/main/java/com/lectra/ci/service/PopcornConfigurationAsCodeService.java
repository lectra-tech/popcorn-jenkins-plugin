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

import com.lectra.ci.domain.model.CascFile;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import io.jenkins.plugins.casc.ConfiguratorException;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/** The type Casc. */
public class PopcornConfigurationAsCodeService {
  /** The constant LOGGER. */
  private static final Logger LOGGER =
      Logger.getLogger(PopcornConfigurationAsCodeService.class.getName());

  private final String cascPath;
  private final ConfigurationAsCode jenkinsCascService;

  public PopcornConfigurationAsCodeService() {
    this(ConfigurationAsCode.get());
  }

  protected PopcornConfigurationAsCodeService(ConfigurationAsCode jenkinsCascService) {
    this(
        System.getProperty(
            ConfigurationAsCode.CASC_JENKINS_CONFIG_PROPERTY,
            System.getenv(ConfigurationAsCode.CASC_JENKINS_CONFIG_ENV)),
        jenkinsCascService);
  }

  protected PopcornConfigurationAsCodeService(
      String cascPath, ConfigurationAsCode jenkinsCascService) {
    this.cascPath = cascPath;
    this.jenkinsCascService = jenkinsCascService;
  }

  /**
   * The boolean isAvailable.
   *
   * @return the boolean
   */
  private boolean isCascAvailable() {
    if (cascPath != null) {
      File cascFolder = new File(cascPath);
      if (cascFolder.exists() && cascFolder.isDirectory()) {
        LOGGER.log(Level.FINE, "Casc reloading is available");
        return true;
      }
      // TODO why not creating directory ? shall the directory be created by startup/external
      // initialization ?
    }
    LOGGER.log(
        Level.WARNING,
        "Casc reloading is not available due to "
            + "missing configuration. The environment variable "
            + ConfigurationAsCode.CASC_JENKINS_CONFIG_ENV
            + " is not set or the folder does not exist");
    return false;
  }

  /**
   * The boolean isAvailable.
   *
   * @return the boolean
   */
  public boolean deleteFiles(String slaveName) {
    if (cascPath != null) {
      File CascFolder = new File(cascPath);
      if (CascFolder.exists() && CascFolder.isDirectory()) {
        final File[] files =
            CascFolder.listFiles(
                (dir, name) -> name.matches(String.format(".*-%s.yaml", slaveName)));
        if (null != files && (files.length) > 0) {
          Arrays.asList(files).stream().forEach(File::delete);
        }
        return true;
      }
    }
    LOGGER.log(
        Level.WARNING,
        "Casc reloading is not available due to missing configuration. The environment variable "
            + ConfigurationAsCode.CASC_JENKINS_CONFIG_ENV
            + " is not set or the folder does not exist");
    return false;
  }

  /**
   * Dump file boolean.
   *
   * @param parentPath the parent path
   * @return the boolean
   */
  protected boolean dumpFile(final String parentPath, CascFile cascFile) {
    File localFile = new File(parentPath, cascFile.getFileName());

    try (BufferedWriter writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localFile), "UTF-8"))) {
      writer.write(cascFile.getDecodedContent()); // do something with the file we've opened
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Apply the provided CASC file by reloading Jenkins CASC subsystem if it is correctly setup.
   *
   * @param cascFile the casc file to apply before reloading
   * @return true if reload was effective, false otherwise
   */
  public boolean applyCasc(final CascFile cascFile) {
    if (isCascAvailable()) {
      LOGGER.log(Level.FINEST, "Dumping file into " + cascPath);
      if (dumpFile(cascPath, cascFile)) {
        try {
          LOGGER.log(Level.FINEST, "Reload casc configuration " + cascPath);
          jenkinsCascService.configure();
          return true;
        } catch (ConfiguratorException e) {
          LOGGER.log(Level.SEVERE, "Error loading casc configuration", e);
        }
      }
    } else {
      LOGGER.log(Level.WARNING, "casc configuration is not available");
    }

    return false;
  }
}
