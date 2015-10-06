/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal.walker;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.file.internal.LocalFileSystem;
import org.mule.module.extension.file.FilePayload;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileWalker
{

    @Operation
    public FilePayload walk(@Connection LocalFileSystem fileSystem,
                            String path,
                            @Optional(defaultValue = "true") boolean recursive)
    {

        Path directory = fileSystem.getExistingPath(path);
        if (!Files.isDirectory(directory))
        {
            throw new IllegalArgumentException(String.format("Path '%s' does not point to a directory. Only directories can be walked", directory.toAbsolutePath()));
        }

        return null;

    }
}
