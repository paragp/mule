/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static java.nio.file.StandardOpenOption.WRITE;
import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleRuntimeException;
import org.mule.extension.file.internal.lock.DefaultPathLock;
import org.mule.extension.file.internal.lock.NullPathLock;
import org.mule.extension.file.internal.lock.PathLock;
import org.mule.module.extension.file.FilePayload;
import org.mule.module.extension.file.FileSystem;
import org.mule.module.extension.file.FileWriteMode;
import org.mule.module.extension.file.internal.VisitableContentWrapper;
import org.mule.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LocalFileSystem implements FileSystem
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileSystem.class);

    static RuntimeException exception(String message, Exception cause)
    {
        return new MuleRuntimeException(createStaticMessage(message), cause);
    }

    private final FileConnector config;

    public LocalFileSystem(FileConnector config)
    {
        this.config = config;
    }

    @Override
    public FilePayload read(String filePath, boolean lock)
    {
        Path path = getExistingPath(filePath);
        if (Files.isDirectory(path))
        {
            throw new IllegalArgumentException(String.format("Cannot read path '%s' since it's a directory", path));
        }

        if (lock)
        {
            return new LocalFilePayload(path, lock(path));
        }
        else
        {
            verifyNotLocked(path);
            return new LocalFilePayload(path);
        }
    }

    @Override
    public void write(String filePath, Object content, FileWriteMode mode, MuleEvent event, boolean lock, boolean createParentFolder)
    {
        Path path = getPath(filePath);

        assureParentFolderExists(path, createParentFolder);

        final OpenOption[] openOptions = mode.getOpenOptions();
        PathLock pathLock;
        if (lock)
        {
            pathLock = lock(path, openOptions);
        }
        else
        {
            pathLock = new NullPathLock();
            verifyNotLocked(path);
        }

        if (mode == FileWriteMode.OVERWRITE && Files.exists(path))
        {

        }

        try (OutputStream out = Files.newOutputStream(path, openOptions))
        {
            new VisitableContentWrapper(content).accept(new LocalFileWriter(out, event));
        }
        catch (Exception e)
        {
            throw exception(String.format("Exception was found writing to file '%s'", path), e);
        }
        finally
        {
            pathLock.release();
        }
    }

    @Override
    public void delete(String filePath)
    {
        Path path = getExistingPath(filePath);

        LOGGER.debug("Preparing to delete '{}'", path);

        verifyNotLocked(path);

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
        Path path = getPath(filePath);
        if (Files.notExists(path))
        {
            throw pathNotFoundException(path);
        }

        return path;
    }

    private Path getPath(String filePath)
    {
        return Paths.get(config.getBaseDir()).resolve(filePath).toAbsolutePath();
    }

    private PathLock lock(Path path)
    {
        return lock(path, WRITE);
    }

    private PathLock lock(Path path, OpenOption... openOptions)
    {
        try
        {
            return DefaultPathLock.lock(path, openOptions);
        }
        catch (Exception e)
        {
            throw exception(String.format("Could not lock file '%s' because it's already owner by another process", path), e);
        }
    }

    /**
     * Try to acquire a lock on a file and release it immediately. Usually used as a
     * quick check to see if another process is still holding onto the file, e.g. a
     * large file (more than 100MB) is still being written to.
     */
    private boolean isLocked(Path path)
    {
        PathLock lock = null;
        try
        {
            lock = DefaultPathLock.lock(path, StandardOpenOption.WRITE);
            return !lock.isLocked();
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
                lock.release();
            }
        }
    }

    private void verifyNotLocked(Path path)
    {
        if (isLocked(path))
        {
            throw new IllegalStateException(String.format("File '%s' is locked by another process", path));
        }
    }

    private void assureParentFolderExists(Path path, boolean createParentFolder)
    {
        if (Files.exists(path))
        {
            return;
        }

        File parentFolder = path.getParent().toFile();
        if (!parentFolder.exists())
        {
            if (createParentFolder)
            {
                parentFolder.mkdirs();
            }
            else
            {
                throw new IllegalArgumentException(String.format("Cannot write to file '%s' because path to it doesn't exist", path));
            }
        }
    }


    private RuntimeException pathNotFoundException(Path path)
    {
        return new IllegalArgumentException(String.format("File '%s' doesn't exists", path));
    }

}
