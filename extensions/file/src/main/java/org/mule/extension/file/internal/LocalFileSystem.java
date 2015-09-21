/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import org.mule.module.extension.file.ContentType;
import org.mule.module.extension.file.FileNotExistsPolicy;
import org.mule.module.extension.file.FilePayload;
import org.mule.module.extension.file.FileSystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileSystem implements FileSystem
{

    private final FileConnector config;

    public LocalFileSystem(FileConnector config)
    {
        this.config = config;
    }

    @Override
    public FilePayload read(String path, boolean lock, ContentType contentType)
    {
        Path filePath = Paths.get(config.getBaseDir(), path);
        if (Files.notExists(filePath))
        {
            throw new IllegalArgumentException(String.format("Cannot read file '%s' because it doesn't exists", filePath.toAbsolutePath()));
        }

        Files.new
    }
}
