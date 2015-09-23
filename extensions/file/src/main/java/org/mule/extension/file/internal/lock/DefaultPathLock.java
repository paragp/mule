/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal.lock;

import org.mule.util.IOUtils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public final class DefaultPathLock implements PathLock
{

    private final FileChannel channel;
    private final FileLock lock;

    public static PathLock lock(Path path, OpenOption... openOptions) throws IOException, OverlappingFileLockException
    {
        FileChannel channel = FileChannel.open(path, openOptions);
        FileLock lock = channel.tryLock();
        return new DefaultPathLock(channel, lock);
    }

    private DefaultPathLock(FileChannel channel, FileLock lock)
    {
        this.channel = channel;
        this.lock = lock;
    }

    @Override
    public boolean isLocked()
    {
        return lock.isValid();
    }

    @Override
    public void release()
    {
        try
        {
            lock.release();
        }
        catch (IOException e)
        {
            // ignore
        }
        IOUtils.closeQuietly(channel);
    }


}
