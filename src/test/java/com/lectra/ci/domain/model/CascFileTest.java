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
package com.lectra.ci.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/** The type Casc file test. */
public class CascFileTest {

  /** Create entity. */
  @Test
  public void createEntity() {
    CascFile cascFile = new CascFile();
    cascFile.setFileName("file.props");
    cascFile.setFileContent("content");

    assertEquals("content", cascFile.getFileContent());
    assertEquals("file.props", cascFile.getFileName());
  }

  /** Decode is base 64. */
  @Test
  public void decodeIsBase64() {
    CascFile cascFile = new CascFile();
    cascFile.setFileContent("ZGVjb2RlZCB0ZXh0");

    assertEquals("decoded text", cascFile.getDecodedContent());
  }

  @Test()
  public void cannot_pass_illegal_arguments_to_setters() {
    CascFile cascFile = new CascFile();

    assertThrows(NullPointerException.class, () -> cascFile.setFileName(null));
    assertThrows(NullPointerException.class, () -> cascFile.setFileContent(null));

    assertThrows(IllegalArgumentException.class, () -> cascFile.setFileName(""));
    assertThrows(IllegalArgumentException.class, () -> cascFile.setFileContent(""));
    assertThrows(IllegalArgumentException.class, () -> cascFile.setFileContent("éùà"));
  }
}
