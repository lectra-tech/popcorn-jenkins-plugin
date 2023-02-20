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

import com.lectra.ci.domain.model.Metrics;
import hudson.model.Job;
import jenkins.model.Jenkins;

/** Status service gather and expose information on the system. */
public class StatusService {
  private final Jenkins jenkinsInstance;
  private final PopcornLockService lockService;

  public StatusService(Jenkins jenkinsInstance, PopcornLockService lockService) {
    this.jenkinsInstance = jenkinsInstance;
    this.lockService = lockService;
  }

  /**
   * The boolean isQuietingDown.
   *
   * @return the isQuietingDown
   */
  private boolean isQuietingDown() {
    return jenkinsInstance.isQuietingDown();
  }

  /**
   * The string CASC_PATH.
   *
   * @return the url
   */
  private String getUrl() {
    return jenkinsInstance.getPrimaryView().getAbsoluteUrl();
  }

  /**
   * The int computeQueueSize.
   *
   * @return the computeQueueSize
   */
  private int computeQueueSize() {
    return jenkinsInstance.getQueue().getItems().length;
  }

  /**
   * The int computeRunningJobs.
   *
   * @return the computeRunningJobs
   */
  private int computeRunningJobs() {
    int cpt = 0;
    for (Job job : jenkinsInstance.getAllItems(Job.class)) {
      if (job.isBuilding()) {
        cpt++;
      }
    }
    return cpt;
  }

  /**
   * Gets metrics.
   *
   * @return the metrics
   */
  public Metrics getMetrics() {
    // TODO should we send status of CASC availability
    return new Metrics(
        computeQueueSize(),
        computeRunningJobs(),
        lockService.isLocked(),
        isQuietingDown(),
        getUrl());
  }
}
