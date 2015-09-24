/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import org.mule.api.MuleEvent;

public interface FileSystem
{

    FilePayload read(String filePath, boolean lock);

    void write(String filePath, Object content, FileWriteMode mode, MuleEvent event, boolean lock, boolean createParentFolder);

    void copy(String sourcePath, String targetDirectory, boolean overwrite, boolean createParentFolder);

    void move(String sourcePath, String targetDirectory, boolean overwrite, boolean createParentFolder);

    void delete(String filePath);
}
