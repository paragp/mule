/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.ParameterGroup;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;

public class FileSystemOperations
{

    @Operation
    public FilePayload read(@Connection FileSystem fileSystem,
                            String path,
                            @Optional(defaultValue = "false") Boolean lock,
                            FileNotExistsPolicy notExistsPolicy,
                            @ParameterGroup ContentType contentType) {

        return fileSystem.read(path, lock, notExistsPolicy, contentType);
    }
}
