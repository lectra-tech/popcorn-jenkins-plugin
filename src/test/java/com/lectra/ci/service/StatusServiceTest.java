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
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import java.io.IOException;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** The type Status test. */
public class StatusServiceTest {
  private StatusService statusService;

  /** The Jenkins rule. */
  @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

  /** Init. */
  @Before
  public void init() {
    PopcornLockService lockService = new PopcornLockService(jenkinsRule.jenkins);
    this.statusService = new StatusService(jenkinsRule.jenkins, lockService);
  }

  /** Metrics should be unlocked. */
  @Test
  public void metricsShouldBeUnlocked() {
    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.isLocked(), false);
  }

  /**
   * Metrics should contain jenkins url.
   *
   * @throws IOException the io exception
   */
  @Test
  public void metricsShouldContainJenkinsUrl() throws IOException {
    Metrics metrics = this.statusService.getMetrics();
    String url = metrics.getUrl();
    Assert.assertEquals(url, jenkinsRule.getURL().toString());
  }

  /** Metrics should not quieting down. */
  @Test
  public void metricsShouldNotQuietingDown() {
    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.isQuietingDown(), false);
  }

  /** Metrics should not be locked. */
  @Test
  public void metricsShouldNotBeLocked() {
    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.isLocked(), false);
  }

  /** Metrics should be zero. */
  @Test
  public void metricsShouldBeZero() {
    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 0);
    Assert.assertEquals(metrics.getRunningJobs(), 0);
  }

  /**
   * Length queue should be one.
   *
   * @throws IOException the io exception
   */
  @Test
  public void lengthQueueShouldBeOne() throws IOException {
    WorkflowJob wJob = jenkinsRule.createProject(WorkflowJob.class, "job");
    wJob.scheduleBuild2(0);

    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 1);
    Assert.assertEquals(metrics.getRunningJobs(), 0);
  }

  /**
   * Length queue should be two.
   *
   * @throws IOException the io exception
   */
  @Test
  public void lengthQueueShouldBeTwo() throws IOException {
    WorkflowJob wJob = jenkinsRule.createProject(WorkflowJob.class, "job");
    WorkflowJob wJob2 = jenkinsRule.createProject(WorkflowJob.class, "job2");
    wJob.scheduleBuild2(0);
    wJob2.scheduleBuild2(0);

    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 2);
    Assert.assertEquals(metrics.getRunningJobs(), 0);
  }

  /**
   * Running job should be one.
   *
   * @throws Exception the exception
   */
  @Test
  public void RunningJobShouldBeOne() throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    project.setAssignedLabel(jenkinsRule.createSlave().getSelfLabel());
    project.scheduleBuild2(0).waitForStart();

    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 0);
    Assert.assertEquals(metrics.getRunningJobs(), 1);
  }

  /**
   * Running job should be zero after job finish.
   *
   * @throws Exception the exception
   */
  @Test
  public void RunningJobShouldBeZeroAfterJobFinish() throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).waitForStart();

    System.out.println(jenkinsRule.jenkins.getComputers()[0].countBusy());

    Metrics metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 0);
    Assert.assertEquals(metrics.getRunningJobs(), 1);

    build.doStop();
    jenkinsRule.waitForCompletion(build);

    metrics = this.statusService.getMetrics();
    Assert.assertEquals(metrics.getQueueSize(), 0);
    Assert.assertEquals(metrics.getRunningJobs(), 0);
  }
}
