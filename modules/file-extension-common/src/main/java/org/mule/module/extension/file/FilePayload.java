/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import java.time.LocalDateTime;

public interface FilePayload
{
    LocalDateTime getLastModifiedTime();

    LocalDateTime getLastAccessTime();

    LocalDateTime getCreationTime();

    long getSize();

    boolean isRegularFile();

    boolean isDirectory();

    boolean isSymbolicLink();

    String getPath();

    FilePayload getDirectory();

    String getFilename();
}
