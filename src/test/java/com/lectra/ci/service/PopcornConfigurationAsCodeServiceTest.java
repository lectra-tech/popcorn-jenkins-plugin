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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.lectra.ci.domain.model.CascFile;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import io.jenkins.plugins.casc.ConfiguratorException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;

/** The type Casc test. */
public class PopcornConfigurationAsCodeServiceTest {

  /** Reload does notwork when missing env variable. */
  @Test
  public void reloadDoesNotworkWhenMissingEnvVariable() throws ConfiguratorException {
    ConfigurationAsCode jenkinsCascService = Mockito.mock(ConfigurationAsCode.class);
    PopcornConfigurationAsCodeService cascService =
        new PopcornConfigurationAsCodeService(jenkinsCascService);

    CascFile cascFile = new CascFile();
    cascFile.setFileName("file.props");
    cascFile.setFileContent("content");

    boolean reloaded = cascService.applyCasc(cascFile);

    assertFalse(reloaded);
    verify(jenkinsCascService, never()).configure();
  }

  /**
   * Write decoded content on file.
   *
   * @throws IOException the io exception
   */
  @Test
  public void writeDecodedContentOnFile() throws IOException {
    ConfigurationAsCode jenkinsCascService = Mockito.mock(ConfigurationAsCode.class);
    PopcornConfigurationAsCodeService cascService =
        new PopcornConfigurationAsCodeService("target", jenkinsCascService);
    CascFile cascFile = new CascFile();
    cascFile.setFileName("file.props");
    cascFile.setFileContent("ZGVjb2RlZCB0ZXh0");

    cascService.applyCasc(cascFile);

    assertEquals(
        "decoded text", FileUtils.readFileToString(new File("target/file.props"), "utf-8"));
    verify(jenkinsCascService).configure();
  }
}
