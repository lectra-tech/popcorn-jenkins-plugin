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

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;

/**
 * Transfer object representing a CASC (Configuration AS Code) file. Used at the stapler boundaries
 * to exchange/receive data.
 */
public class CascFile {

  /** The fileName of the CASC object. */
  private String fileName;

  /** The content encoded as base64. */
  private String fileContent;

  /** Content extracted/decoded from fileContent. */
  private transient String decodedContent;

  /**
   * Gets file name.
   *
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets file name.
   *
   * @param fileName the file name
   * @throws IllegalArgumentException if the fileName is empty
   * @throws NullPointerException if fileName is null
   */
  public void setFileName(final String fileName) {
    this.fileName = Objects.requireNonNull(fileName, "filename cannot be null");
    if (fileName.isEmpty()) {
      throw new IllegalArgumentException("filename cannot be empty");
    }
  }

  /**
   * Gets file content.
   *
   * @return the file content
   */
  public String getFileContent() {
    return fileContent;
  }

  /**
   * Sets file content.
   *
   * @param fileContent the file content
   * @throws IllegalArgumentException if the content is empty or not base64 encoded
   * @throws NullPointerException if fileContent is null
   */
  public void setFileContent(final String fileContent) {
    this.fileContent = Objects.requireNonNull(fileContent, "fileContent cannot be null");
    if (this.fileContent.isEmpty()) {
      throw new IllegalArgumentException("fileContent cannot be empty");
    }
    this.decodedContent =
        new String(Base64.getDecoder().decode(fileContent), Charset.forName("UTF-8"));
  }

  /**
   * Gets the content decoded as base64.
   *
   * @return the decoded content
   */
  public String getDecodedContent() {
    return decodedContent;
  }
}
