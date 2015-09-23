/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import java.io.InputStream;

public interface FileSystem
{

    FilePayload read(String filePath, boolean lock);

    void write(String filePath, InputStream content, FileWriteMode mode, boolean lock, boolean createParentFolder);

    void delete(String filePath);
}
