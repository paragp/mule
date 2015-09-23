/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.extension.file.internal.lock.NullPathLock;
import org.mule.extension.file.internal.lock.PathLock;
import org.mule.module.extension.file.FilePayload;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class LocalFilePayload implements FilePayload, Closeable
{

    private final Path path;
    private final PathLock lock;

    private BasicFileAttributes attributes = null;
    private InputStream content;

    LocalFilePayload(Path path)
    {
        this(path, new NullPathLock());
    }

    public LocalFilePayload(Path path, PathLock lock)
    {
        this.path = path;
        this.lock = lock;
    }

    @Override
    public LocalDateTime getLastModifiedTime()
    {
        return asDateTime(getAttributes().lastModifiedTime());
    }

    @Override
    public LocalDateTime getLastAccessTime()
    {
        return asDateTime(getAttributes().lastAccessTime());
    }

    @Override
    public LocalDateTime getCreationTime()
    {
        return asDateTime(getAttributes().creationTime());
    }

    @Override
    public long getSize()
    {
        return getAttributes().size();
    }

    @Override
    public boolean isRegularFile()
    {
        return getAttributes().isRegularFile();
    }

    @Override
    public boolean isDirectory()
    {
        return getAttributes().isDirectory();
    }

    @Override
    public boolean isSymbolicLink()
    {
        return getAttributes().isSymbolicLink();
    }

    @Override
    public String getPath()
    {
        return path.toString();
    }

    @Override
    public String getFilename()
    {
        return path.getFileName().toString();
    }

    @Override
    public synchronized InputStream getContent()
    {
        if (content == null)
        {
            try
            {
                content = new FileInputStream(Files.newBufferedReader(path), lock);
            }
            catch (Exception e)
            {
                throw new MuleRuntimeException(createStaticMessage("Could not open file " + path), e);
            }
        }

        return content;
    }

    @Override
    public synchronized void close() throws IOException
    {
        try
        {
            closeStream();
        }
        finally
        {
            lock.release();
        }
    }

    private void closeStream() throws IOException
    {
        if (content != null)
        {
            content.close();
        }
    }

    public boolean isLocked()
    {
        return lock.isLocked();
    }

    private synchronized BasicFileAttributes getAttributes()
    {
        if (attributes == null)
        {
            try
            {
                attributes = Files.readAttributes(path, BasicFileAttributes.class);
            }
            catch (Exception e)
            {
                throw new MuleRuntimeException(createStaticMessage("Could not read attributes for file " + path), e);
            }
        }

        return attributes;
    }

    private LocalDateTime asDateTime(FileTime fileTime)
    {
        return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
    }
}
