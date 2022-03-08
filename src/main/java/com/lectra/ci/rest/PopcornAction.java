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
package com.lectra.ci.rest;

import com.lectra.ci.domain.model.CascFile;
import com.lectra.ci.domain.model.Metrics;
import com.lectra.ci.service.PopcornConfigurationAsCodeService;
import com.lectra.ci.service.PopcornLockService;
import com.lectra.ci.service.StatusService;
import com.lectra.ci.service.WindowsSlaveService;
import hudson.Extension;
import hudson.model.RootAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.kohsuke.stapler.json.JsonBody;
import org.kohsuke.stapler.verb.DELETE;
import org.kohsuke.stapler.verb.GET;
import org.kohsuke.stapler.verb.POST;

/** The type Popcorn action. */
@Extension
public class PopcornAction implements RootAction {

  /** The constant LOGGER. */
  private static final Logger LOGGER = Logger.getLogger(PopcornAction.class.getName());
  /** The string STATE_IS_RUNNING. */
  private static final String STATE_IS_RUNNING = "is-running";

  private final WindowsSlaveService windowsSlaveService;
  private final PopcornConfigurationAsCodeService cascService;
  private final PopcornLockService lockService;
  private final StatusService statusService;

  /**
   * Gets icon file name.
   *
   * @return the icon fileppp name
   */
  @Override
  public String getIconFileName() {
    return null;
  }

  /**
   * Gets display name.
   *
   * @return the display name
   */
  @Override
  public String getDisplayName() {
    return "Popcorn Exporter";
  }

  /**
   * Gets url name.
   *
   * @return the url name
   */
  @Override
  public String getUrlName() {
    return "popcorn";
  }

  public PopcornAction() {
    Jenkins jenkins = Jenkins.get();
    this.cascService = new PopcornConfigurationAsCodeService();
    this.windowsSlaveService = new WindowsSlaveService(jenkins, cascService);
    this.lockService = new PopcornLockService(jenkins);
    this.statusService = new StatusService(jenkins, lockService);
  }

  /**
   * Do casc http response.
   *
   * @param file the file
   * @return the http response
   */
  @RequirePOST
  @POST
  @WebMethod(name = "casc")
  public HttpResponse applyNewCasc(final @JsonBody CascFile file) {
    LOGGER.log(Level.INFO, "[popcorn-jenkins] casc injection called : " + file.getFileName());

    if (cascService.applyCasc(file)) {
      // TODO why 201, not 200 ? where is the link if 201 ?
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_CREATED).build();
    } else {
      // TODO why 500, should be a 400
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Expose popcorn status.
   *
   * @return the summary of the status of popcorn features &amp; jenkins server state
   */
  @GET
  @WebMethod(name = "status")
  public HttpResponse getStatus() {
    Metrics metrics = statusService.getMetrics();

    LOGGER.log(Level.FINEST, "popcorn status:\n" + metrics);
    return PopcornHttpResponse.withStatus(StaplerResponse.SC_OK)
        .noCache()
        .withContent(JSONSerializer.toJSON(metrics).toString())
        .build();
  }

  /**
   * Delete slave http response.
   *
   * @param staplerRequest the stapler request
   * @return the http response
   */
  @DELETE
  @WebMethod(name = "slave")
  // TODO shouldn't it be windows specific
  public HttpResponse deleteSlave(final StaplerRequest staplerRequest) {
    String slaveName = StringUtils.stripStart(staplerRequest.getRestOfPath(), "/");
    LOGGER.log(Level.INFO, "delete slave asked on " + slaveName);

    if (!windowsSlaveService.delete(slaveName)) {
      // TODO should we distinguish 400 and 404
      LOGGER.log(Level.SEVERE, "Unable to delete slave : " + slaveName);
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_NOT_FOUND)
          .noCache()
          .withContentType("text/plain")
          .withContent("")
          .build();
    }

    // TODO why SC_OK and not SC_GONE
    return PopcornHttpResponse.withStatus(StaplerResponse.SC_OK).noCache().build();
  }

  /**
   * Gets slave state.
   *
   * @param staplerRequest the stapler request
   * @return the slave state
   */
  @GET
  @WebMethod(name = "slave")
  public HttpResponse getSlaveState(final StaplerRequest staplerRequest) {
    String query = StringUtils.stripStart(staplerRequest.getRestOfPath(), "/");

    // Query is of the form SLAVENAME/QUERYSTATE
    String queryState = StringUtils.substringAfterLast(query, "/");
    String slaveName = StringUtils.substringBefore(query, "/");
    String ret = "{}";
    if (STATE_IS_RUNNING.equals(
        queryState)) { // TODO what about invalid state? just empty response?
      boolean nodeIsRunningState = windowsSlaveService.isRunning(slaveName);
      LOGGER.log(
          Level.FINEST, String.format("slave %s running: %s", slaveName, nodeIsRunningState));
      ret = "{" + "\"isRunning\": " + nodeIsRunningState + "}";
    }
    String finalRet = ret;
    return PopcornHttpResponse.withStatus(StaplerResponse.SC_OK)
        .noCache()
        .withContent(finalRet)
        .build();
  }

  /**
   * Gets lock.
   *
   * @return the lock
   */
  @GET
  @WebMethod(name = "lock")
  public HttpResponse getLock() {
    String responseContent = String.format("{ \"locked\" : %s}", lockService.isLocked());
    return PopcornHttpResponse.withStatus(StaplerResponse.SC_OK)
        .noCache()
        .withContent(responseContent)
        .build();
  }

  /**
   * Create lock http response.
   *
   * @return the http response
   */
  @POST
  @WebMethod(name = "lock")
  public HttpResponse createLock() {
    if (lockService.lock()) {
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_CREATED).build();
    } else {
      // TODO why 500, should be a 400
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Delete lock http response.
   *
   * @return the http response
   */
  @DELETE
  @WebMethod(name = "lock")
  public HttpResponse deleteLock() {
    if (lockService.unlock()) {
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_OK).build();
    } else {
      // TODO why 500, should be a 400
      return PopcornHttpResponse.withStatus(StaplerResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
  }
}
