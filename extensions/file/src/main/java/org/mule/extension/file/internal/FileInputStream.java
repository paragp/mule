/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.extension.file.internal.lock.PathLock;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.io.input.ReaderInputStream;

final class FileInputStream extends AutoCloseInputStream
{

    private final PathLock lock;

    public FileInputStream(Reader reader, PathLock lock)
    {
        super(new ReaderInputStream(reader));
        checkArgument(lock != null, "lock cannot be null");
        this.lock = lock;
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            super.close();
        }
        finally
        {
            lock.release();
        }
    }
}
