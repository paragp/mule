/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.module.extension.file.FilePayload;
import org.mule.module.extension.file.FileSystem;
import org.mule.util.FileUtils;
import org.mule.util.IOUtils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystem implements FileSystem
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileSystem.class);

    private final FileConnector config;

    public LocalFileSystem(FileConnector config)
    {
        this.config = config;
    }

    @Override
    public FilePayload read(String filePath)
    {
        Path path = getExistingPath(filePath);
        if (Files.isDirectory(path))
        {
            throw new IllegalArgumentException(String.format("Cannot read path '%s' since it's a directory", path));
        }

        return new LocalFilePayload(path);
    }

    @Override
    public void delete(String filePath)
    {
        Path path = getExistingPath(filePath);

        LOGGER.debug("Preparing to delete '{}'", path);
        if (isLocked(path, READ, WRITE))
        {
            throw new IllegalStateException(String.format("Cannot delete '%s' because it's locked by another process", path));
        }

        LOGGER.debug("file '{}' deleted", path);
        try
        {
            if (Files.isDirectory(path))
            {
                FileUtils.deleteTree(path.toFile());
            }
            else
            {
                Files.deleteIfExists(path);
            }

            LOGGER.debug("Successfully deleted '{}'", path);
        }
        catch (IOException e)
        {
            throw exception(String.format("Could not delete '%s'", path), e);
        }
    }

    private Path getExistingPath(String filePath)
    {
        Path path = Paths.get(config.getBaseDir()).resolve(filePath);
        if (Files.notExists(path))
        {
            throw pathNotFoundException(path);
        }

        return path.toAbsolutePath();
    }

    /**
     * Try to acquire a lock on a file and release it immediately. Usually used as a
     * quick check to see if another process is still holding onto the file, e.g. a
     * large file (more than 100MB) is still being written to.
     */
    private boolean isLocked(Path path, OpenOption... openOptions)
    {
        FileChannel channel = null;
        FileLock lock = null;
        try
        {
            channel = FileChannel.open(path, openOptions);
            lock = channel.tryLock();
            return lock != null ? !lock.isValid() : true;
        }
        catch (IOException e)
        {
            // this means that we don't have write permissions over the file. We assume it not
            // to be locked
            return false;
        }
        catch (OverlappingFileLockException e)
        {
            return true;
        }
        finally
        {
            if (lock != null)
            {
                try
                {
                    lock.release();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
            IOUtils.closeQuietly(channel);
        }
    }

    private RuntimeException exception(String message, Exception e)
    {
        return new MuleRuntimeException(createStaticMessage(message), e);
    }

    private RuntimeException pathNotFoundException(Path path)
    {
        return new IllegalArgumentException(String.format("File '%s' doesn't exists", path));
    }

}
