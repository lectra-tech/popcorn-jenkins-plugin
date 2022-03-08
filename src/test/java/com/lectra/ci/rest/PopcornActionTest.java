package com.lectra.ci.rest;

import static com.lectra.ci.JenkinsHarnessUtils.addCrumb;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lectra.ci.CascDirectoryJenkinsRule;
import com.lectra.ci.domain.model.CascFile;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class PopcornActionTest {
  /** The Jenkins rule. */
  @Rule public JenkinsRule jenkinsRule = new CascDirectoryJenkinsRule();

  @Test
  public void can_evaluate_lock_status_on_api() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse jsonWebResponse = wc.getJSON("popcorn/lock");

    assertEquals(HttpStatus.SC_OK, jsonWebResponse.getStatusCode());
    Boolean isLocked = (Boolean) jsonWebResponse.getJSONObject().get("locked");
    Assert.assertFalse("system should not be locked by default", isLocked);
  }

  @Test
  public void can_lock_unlock_via_api() throws IOException {
    // by default system shoud be unlock
    verifyLockStatus(false);

    // when we lock it
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse jsonWR =
        wc.postJSON(addCrumb(jenkinsRule.jenkins, "popcorn/lock"), new JSONObject());

    assertEquals(HttpStatus.SC_CREATED, jsonWR.getStatusCode());
    // it shoudl be locked
    verifyLockStatus(true);

    // then when we unlock it
    // here we use another http client supporting DELETE
    URL crumbedUrl = wc.createCrumbedUrl("popcorn/lock");
    HttpClient client = HttpClientBuilder.create().build();
    HttpUriRequest request =
        RequestBuilder.delete(crumbedUrl.toString())
            .setHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
    HttpResponse httpResponse = client.execute(request);
    assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

    // it should now be unlocked
    verifyLockStatus(false);
  }

  @Test
  public void can_call_status() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse jsonWebResponse = wc.getJSON("popcorn/status");

    assertEquals(HttpStatus.SC_OK, jsonWebResponse.getStatusCode());

    JSONObject jsonResponse = jsonWebResponse.getJSONObject();

    assertEquals(0, jsonResponse.get("queueSize"));
    assertEquals(0, jsonResponse.get("runningJobs"));
    assertEquals(false, jsonResponse.get("locked"));
    assertEquals(false, jsonResponse.get("quietingDown"));
    assertEquals(jenkinsRule.getURL().toString(), jsonResponse.get("url"));
  }

  @Test
  public void can_retrieve_slave_status_of_unknown_slave() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse jsonWebResponse =
        wc.getJSON("popcorn/slave/does-not-exist/is-running");
    assertEquals(HttpStatus.SC_OK, jsonWebResponse.getStatusCode());

    JSONObject jsonResponse = jsonWebResponse.getJSONObject();
    assertEquals(false, jsonResponse.get("isRunning"));
  }

  @Test
  public void calling_slave_status_with_invalid_query_returns_empty_object() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse jsonWebResponse =
        wc.getJSON("popcorn/slave/ignored/is-unknown-command");
    assertEquals(HttpStatus.SC_OK, jsonWebResponse.getStatusCode());

    JSONObject jsonResponse = jsonWebResponse.getJSONObject();

    assertTrue("an unknown command should return an empty object", jsonResponse.isEmpty());
  }

  @Test
  public void can_create_a_slave_machine_via_casc() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();

    CascFile slave = buildCascFromResource("/ws-virtual-server.yaml");
    JSON cfAsJSON = JSONSerializer.toJSON(slave);
    JenkinsRule.JSONWebResponse jsonCascResponse =
        wc.postJSON(addCrumb(jenkinsRule.jenkins, "popcorn/casc"), cfAsJSON);
    assertEquals(HttpStatus.SC_CREATED, jsonCascResponse.getStatusCode());
  }

  @Test
  public void can_query_a_slave_machine() throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();

    // When creating a slave
    CascFile slave = buildCascFromResource("/ws-virtual-server.yaml");
    JSON cfAsJSON = JSONSerializer.toJSON(slave);
    JenkinsRule.JSONWebResponse jsonCascResponse =
        wc.postJSON(addCrumb(jenkinsRule.jenkins, "popcorn/casc"), cfAsJSON);
    assertEquals(HttpStatus.SC_CREATED, jsonCascResponse.getStatusCode());

    // Accessing it should give a response
    JenkinsRule.JSONWebResponse jsonWebResponse =
        wc.getJSON("popcorn/slave/virtual-server/is-running");
    assertEquals(HttpStatus.SC_OK, jsonWebResponse.getStatusCode());

    // The slave shall not be running
    JSONObject jsonResponse = jsonWebResponse.getJSONObject();
    assertEquals(false, jsonResponse.get("isRunning"));
  }

  private CascFile buildCascFromResource(String resourceLocation) {
    try {
      String content =
          new String(
              Files.readAllBytes(Paths.get(getClass().getResource(resourceLocation).toURI())),
              StandardCharsets.UTF_8);
      CascFile cf = new CascFile();
      cf.setFileName(StringUtils.substringAfterLast(resourceLocation, "/"));
      cf.setFileContent(
          new String(
              Base64.getEncoder().encode(content.getBytes(StandardCharsets.UTF_8)),
              StandardCharsets.UTF_8));
      return cf;
    } catch (Exception ex) {
      throw new IllegalStateException("cannot create CascFile from " + resourceLocation, ex);
    }
  }

  private void verifyLockStatus(boolean lockExpected) throws IOException {
    JenkinsRule.WebClient wc = jenkinsRule.createWebClient();
    JenkinsRule.JSONWebResponse lockResponse = wc.getJSON("popcorn/lock");
    assertEquals(HttpStatus.SC_OK, lockResponse.getStatusCode());
    Boolean isLocked = (Boolean) lockResponse.getJSONObject().get("locked");
    if (lockExpected) {
      Assert.assertTrue("system should be locked", isLocked);
    } else {
      Assert.assertFalse("system should not be locked", isLocked);
    }
  }
}
