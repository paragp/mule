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
import java.nio.file.StandardCopyOption;

class CopyCommand
{

    protected final Path source;
    protected Path targetPath;
    protected final boolean overwrite;
    protected final boolean createParentFolder;

    public CopyCommand(Path source, Path targetPath, boolean overwrite, boolean createParentFolder)
    {
        this.source = source;
        this.targetPath = targetPath;
        this.overwrite = overwrite;
        this.createParentFolder = createParentFolder;
    }

    final void execute()
    {
        CopyOption copyOption = null;
        if (Files.exists(targetPath))
        {
            if (Files.isDirectory(targetPath))
            {
                if (Files.isDirectory(source) && source.getFileName().equals(targetPath.getFileName()) && !overwrite)
                {
                    throw alreadyExistsException();
                }
                else
                {
                    targetPath = targetPath.resolve(source.getFileName());
                }
            }
            else if (overwrite)
            {
                copyOption = StandardCopyOption.REPLACE_EXISTING;
            }
            else
            {
                throw new IllegalArgumentException(String.format("'%s' already exists. Set the 'overwrite' parameter to 'true' to perform the operation anyway", targetPath));
            }
        }
        else
        {
            if (createParentFolder)
            {
                targetPath.toFile().mkdirs();
                targetPath = targetPath.resolve(source.getFileName());
            }
            else
            {
                throw alreadyExistsException();
            }
        }

        doExecute(copyOption != null ? new CopyOption[] {copyOption} : new CopyOption[] {});
    }

    protected void doExecute(CopyOption[] options)
    {
        try
        {
            if (Files.isDirectory(source))
            {
                //if (source.getFileName().equals(targetPath.getFileName()) && !overwrite)
                //{
                //    throw alreadyExistsException();
                //}

                FileUtils.copyDirectory(source.toFile(), targetPath.toFile());
            }
            else
            {
                Files.copy(source, targetPath, options);
            }
        }
        catch (Exception e)
        {
            throw exception(String.format("Found exception copying file '%s' to '%s'", source, targetPath), e);
        }
    }

    protected IllegalArgumentException alreadyExistsException()
    {
        return new IllegalArgumentException(String.format("'%s' already exists. Set the 'overwrite' parameter to 'true' to perform the operation anyway", targetPath));
    }

}
