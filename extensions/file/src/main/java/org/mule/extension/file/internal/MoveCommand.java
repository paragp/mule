/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static org.mule.extension.file.internal.LocalFileSystem.exception;
import org.mule.util.FileUtils;

import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

final class MoveCommand extends CopyCommand
{

    MoveCommand(Path source, Path targetPath, boolean overwrite, boolean createParentFolder)
    {
        super(source, targetPath, overwrite, createParentFolder);
    }

    @Override
    protected void doExecute(CopyOption[] options)
    {
        try
        {
            if (Files.isDirectory(source))
            {
                if (Files.exists(targetPath)) {
                    if (overwrite) {
                        FileUtils.deleteTree(targetPath.toFile());
                    } else {
                        alreadyExistsException();
                    }
                }
                FileUtils.moveDirectory(source.toFile(), targetPath.toFile());
            }
            else
            {
                Files.move(source, targetPath, options);
            }
        }
        catch (Exception e)
        {
            throw exception(String.format("Found exception copying file '%s' to '%s'", source, targetPath), e);
        }
    }
}
