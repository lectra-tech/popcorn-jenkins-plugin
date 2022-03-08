package com.lectra.ci.rest;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mockito;

public class PopcornHttpResponseTest {
  @Test
  public void can_create_an_http_response_with_specific_status()
      throws ServletException, IOException {
    PopcornHttpResponse httpResponse =
        PopcornHttpResponse.withStatus(StaplerResponse.SC_OK).build();
    assertNotNull(httpResponse);

    StaplerRequest request = Mockito.mock(StaplerRequest.class);
    StaplerResponse response = Mockito.mock(StaplerResponse.class);

    httpResponse.generateResponse(request, response, any());

    verify(response).setStatus(StaplerResponse.SC_OK);
    verify(response).setContentType(PopcornHttpResponse.DEFAULT_CONTENT_TYPE);
    verify(response, never()).getWriter();
    verify(response, never()).addHeader(any(), any());
  }

  @Test
  public void can_activate_cache_control_on_http_response() throws ServletException, IOException {
    PopcornHttpResponse httpResponse =
        PopcornHttpResponse.withStatus(StaplerResponse.SC_OK).noCache().build();

    assertNotNull(httpResponse);

    StaplerRequest request = Mockito.mock(StaplerRequest.class);
    StaplerResponse response = Mockito.mock(StaplerResponse.class);

    httpResponse.generateResponse(request, response, any());

    verify(response).setStatus(StaplerResponse.SC_OK);
    verify(response).setContentType(PopcornHttpResponse.DEFAULT_CONTENT_TYPE);
    verify(response, never()).getWriter();
    verify(response).addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
  }

  @Test
  public void can_specify_content_type_http_response() throws ServletException, IOException {
    String textPlainContentType = "text/plain";
    PopcornHttpResponse httpResponse =
        PopcornHttpResponse.withStatus(StaplerResponse.SC_OK)
            .withContentType(textPlainContentType)
            .build();

    assertNotNull(httpResponse);

    StaplerRequest request = Mockito.mock(StaplerRequest.class);
    StaplerResponse response = Mockito.mock(StaplerResponse.class);

    httpResponse.generateResponse(request, response, any());

    verify(response).setStatus(StaplerResponse.SC_OK);
    verify(response).setContentType(textPlainContentType);
    verify(response, never()).getWriter();
    verify(response, never()).addHeader(any(), any());
  }

  @Test
  public void can_add_content_on_http_response() throws ServletException, IOException {
    String content = "some content";
    PopcornHttpResponse httpResponse =
        PopcornHttpResponse.withStatus(StaplerResponse.SC_OK).withContent(content).build();

    assertNotNull(httpResponse);

    StaplerRequest request = Mockito.mock(StaplerRequest.class);
    StaplerResponse response = Mockito.mock(StaplerResponse.class);
    PrintWriter writer = Mockito.mock(PrintWriter.class);

    when(response.getWriter()).thenReturn(writer);
    httpResponse.generateResponse(request, response, any());

    verify(response).setStatus(StaplerResponse.SC_OK);
    verify(response).setContentType(PopcornHttpResponse.DEFAULT_CONTENT_TYPE);
    verify(writer).write(content);
    verify(response, never()).addHeader(any(), any());
  }

  @Test
  public void can_add_content_with_no_cache_on_http_response()
      throws ServletException, IOException {
    String content = "some content";
    PopcornHttpResponse httpResponse =
        PopcornHttpResponse.withStatus(StaplerResponse.SC_OK)
            .withContent(content)
            .noCache()
            .build();

    assertNotNull(httpResponse);

    StaplerRequest request = Mockito.mock(StaplerRequest.class);
    StaplerResponse response = Mockito.mock(StaplerResponse.class);
    PrintWriter writer = Mockito.mock(PrintWriter.class);

    when(response.getWriter()).thenReturn(writer);
    httpResponse.generateResponse(request, response, any());

    verify(response).setStatus(StaplerResponse.SC_OK);
    verify(response).setContentType(PopcornHttpResponse.DEFAULT_CONTENT_TYPE);
    verify(writer).write(content);
    verify(response).addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
  }
}
