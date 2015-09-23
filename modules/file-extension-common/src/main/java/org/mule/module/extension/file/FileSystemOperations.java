/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;

import java.io.InputStream;

public class FileSystemOperations
{

    @Operation
    public FilePayload read(@Connection FileSystem fileSystem,
                            String path,
                            @Optional(defaultValue = "false") boolean lock)
    {
        //TODO: support encoding and mimeType
        return fileSystem.read(path, lock);
    }

    @Operation
    public void write(@Connection FileSystem fileSystem,
                      String path,
                      @Optional(defaultValue = "#[payload]") InputStream content,
                      @Optional(defaultValue = "THROW_EXCEPTION") FileWriteMode mode,
                      @Optional(defaultValue = "false") boolean lock,
                      @Optional(defaultValue = "false") boolean createParentFolder)
    {
        fileSystem.write(path, content, mode, lock, createParentFolder);
    }

    @Operation
    public void delete(@Connection FileSystem fileSystem, String path)
    {
        fileSystem.delete(path);
    }
}
