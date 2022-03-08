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

import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/** Facility class to easily build http responses for stapler actions. */
public class PopcornHttpResponse implements HttpResponse {
  /** The DEFAULT_CONTENT_TYPE to use. */
  public static final String DEFAULT_CONTENT_TYPE = "application/json";

  /** The content-type to use in the response. */
  private String contentType;

  /**
   * Flag indicating to deactivate http caching, if true no-cache http functionalities will be
   * activated.
   */
  private boolean noCache;

  /** The http status to set on the response. */
  private int status;

  /** The content to write to the response body. */
  private String content;

  private PopcornHttpResponse(int status, String contentType, boolean noCache, String content) {
    this.status = status;
    this.noCache = noCache;
    this.content = content;
    this.contentType = Objects.requireNonNull(contentType);
  }

  /**
   * Access a PopcornHttpResponseBuilder object with the provided status
   *
   * @param status the http status to use for the future PopcornHttpResponse to be built
   * @return a builder for fluent calls
   */
  public static PopcornHttpResponseBuilder withStatus(int status) {
    if (status < 100 || status >= 600) {
      throw new IllegalArgumentException("invalid HTTP code status: " + status);
    }

    PopcornHttpResponseBuilder builder = new PopcornHttpResponseBuilder(status);
    return builder;
  }

  /**
   * Writes to the response object the accumulated values.
   *
   * @param staplerRequest the stapler request
   * @param staplerResponse the stapler response to update
   * @param unused unused parameter
   * @throws IOException the io exception
   * @throws ServletException the servlet exception
   */
  @Override
  public void generateResponse(
      final StaplerRequest staplerRequest,
      final StaplerResponse staplerResponse,
      final Object unused)
      throws IOException, ServletException {
    staplerResponse.setStatus(status);
    staplerResponse.setContentType(contentType);

    if (noCache) {
      staplerResponse.addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
    }
    if (content != null) {
      staplerResponse.getWriter().write(content);
    }
  }

  /** Builder class to control and drive how PopcornHttpResponse are built. */
  public static class PopcornHttpResponseBuilder {
    private int status;
    private boolean noCache;
    private String content;
    private String contentType = PopcornHttpResponse.DEFAULT_CONTENT_TYPE;

    private PopcornHttpResponseBuilder(int status) {
      this.status = status;
    }

    /**
     * Constructs a unmodifiable PopcornHttpResponse object. If no content-type was provided,
     * "application/json" will be used.
     *
     * @return the PopcornHttpResponse objects filled with the accumulated values
     */
    public PopcornHttpResponse build() {
      return new PopcornHttpResponse(this.status, this.contentType, this.noCache, this.content);
    }

    /**
     * If true, activates cache control http functionality with no-cache
     *
     * @return the builder itself for fluent calls
     */
    public PopcornHttpResponseBuilder noCache() {
      this.noCache = true;
      return this;
    }

    /**
     * Register the body to send in the response
     *
     * @param content a non null content to write
     * @return the builder itself for fluent calls
     */
    public PopcornHttpResponseBuilder withContent(String content) {
      this.content = Objects.requireNonNull(content);
      return this;
    }

    /**
     * Register the http content-type to use in the response.
     *
     * @param contentType the content-type to apply, non null
     * @return the builder itself for fluent calls
     */
    public PopcornHttpResponseBuilder withContentType(String contentType) {
      this.contentType = Objects.requireNonNull(contentType);
      return this;
    }
  }
}
