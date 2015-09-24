/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import org.mule.api.MuleEvent;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.transport.NullPayload;

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
                      @Optional(defaultValue = "#[payload]") Object content,
                      @Optional(defaultValue = "OVERWRITE") FileWriteMode mode,
                      @Optional(defaultValue = "false") boolean lock,
                      @Optional(defaultValue = "true") boolean createParentFolder,
                      MuleEvent event)
    {
        if (content == null || content instanceof NullPayload)
        {
            throw new IllegalArgumentException("Cannot write a null content");
        }

        fileSystem.write(path, content, mode, event, lock, createParentFolder);
    }

    @Operation
    public void delete(@Connection FileSystem fileSystem, String path)
    {
        fileSystem.delete(path);
    }
}
