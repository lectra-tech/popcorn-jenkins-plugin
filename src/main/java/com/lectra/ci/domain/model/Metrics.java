package com.lectra.ci.domain.model;

/** Metrics provide information on the state of the system. */
public class Metrics {
  /**
   * Size of the project queue size on the Jenkins instance at the time of the metrics were taken
   */
  final int queueSize;
  /** Number of jobs actively being built at the time of the metrics were taken */
  final int runningJobs;
  /** Was the Popcorn server lock at the time of the metrics were taken */
  final boolean locked;
  /**
   * Was the Jenkins server under a "waiting for restart" phase at the time of the metrics were
   * taken
   */
  final boolean quietingDown;
  /** The Jenkins server base URL */
  final String url;

  public Metrics(int queueSize, int runningJobs, boolean locked, boolean quietingDown, String url) {
    this.queueSize = queueSize;
    this.runningJobs = runningJobs;
    this.locked = locked;
    this.quietingDown = quietingDown;
    this.url = url;
  }

  public int getQueueSize() {
    return queueSize;
  }

  public int getRunningJobs() {
    return runningJobs;
  }

  public boolean isLocked() {
    return locked;
  }

  public boolean isQuietingDown() {
    return quietingDown;
  }

  public String getUrl() {
    return url;
  }
}
