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

import hudson.model.Computer;
import hudson.model.Node;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/** Service handling Jenkins node representing Windows slave. */
public class WindowsSlaveService {
  /** The LOGGER to use. */
  private static final Logger LOGGER = Logger.getLogger(WindowsSlaveService.class.getName());

  private final Jenkins jenkinsInstance;
  private final PopcornConfigurationAsCodeService cascService;

  public WindowsSlaveService(
      Jenkins jenkinsInstance, PopcornConfigurationAsCodeService cascService) {
    this.jenkinsInstance = jenkinsInstance;
    this.cascService = cascService;
  }

  /**
   * Delete the Jenkins node corresponding to the given name.
   *
   * @param nodeName the node name
   * @return true if the node was found and its deletion was successful, false otherwise
   */
  public boolean delete(final String nodeName) {
    Node node = jenkinsInstance.getNode(nodeName);
    if (node != null) {
      try {
        jenkinsInstance.removeNode(node);
        if (cascService.deleteFiles(nodeName)) {
          return true;
        } else {
          LOGGER.log(Level.WARNING, "Unable to delete casc file with salve: " + nodeName);
        }
      } catch (IOException ioe) {
        LOGGER.log(Level.WARNING, "Jenkins could not delete " + nodeName, ioe);
      }
    }
    return false;
  }

  /**
   * Query the state of a given node to know if it is "running". A node is running if any of its
   * executor is active at the time of query.
   *
   * @param nodeName the node name
   * @return true if the node is found and running, false otherwise
   */
  public boolean isRunning(final String nodeName) {
    Computer computer = jenkinsInstance.getComputer(nodeName);
    if (computer != null) { // TODO what about not found ?
      return computer.countBusy() > 0;
    } else {
      LOGGER.log(Level.FINEST, "Unable to get state of unknown slave " + nodeName);
    }
    return false;
  }
}
