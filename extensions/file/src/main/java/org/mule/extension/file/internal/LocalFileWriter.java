/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static org.mule.extension.file.internal.LocalFileSystem.exception;
import org.mule.api.MuleEvent;
import org.mule.api.transport.OutputHandler;
import org.mule.module.extension.file.internal.FileWriter;
import org.mule.module.extension.file.internal.VisitableContentWrapper;
import org.mule.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

final class LocalFileWriter implements FileWriter
{

    private final OutputStream out;
    private final MuleEvent event;

    public LocalFileWriter(OutputStream outputStream, MuleEvent event)
    {
        this.out = outputStream;
        this.event = event;
    }

    @Override
    public void write(String content) throws Exception
    {
        IOUtils.write(content, out);
    }

    @Override
    public void write(byte content) throws Exception
    {
        out.write(content);
    }

    @Override
    public void write(byte[] content) throws Exception
    {
        IOUtils.write(content, out);
    }

    @Override
    public void write(OutputHandler handler) throws Exception
    {
        handler.write(event, out);
    }

    @Override
    public void write(String[] content) throws Exception
    {
        for (String line : content)
        {
            IOUtils.write(line, out);
        }
    }

    @Override
    public void write(InputStream content) throws Exception
    {
        IOUtils.copy(content, out);
    }

    @Override
    public void write(Iterable<?> content) throws Exception
    {
        write(content.iterator());
    }

    @Override
    public void write(Iterator<?> content) throws Exception
    {
        content.forEachRemaining(item -> {
            if (item == null)
            {
                throw new IllegalArgumentException("Cannot write a null value into a file");
            }
            try
            {
                new VisitableContentWrapper(item).accept(LocalFileWriter.this);
            }
            catch (Exception e)
            {
                throw exception("Could not write item of type " + item.getClass().getName(), e);
            }
        });
    }
}