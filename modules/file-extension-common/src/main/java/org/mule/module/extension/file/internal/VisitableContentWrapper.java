/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.internal;

import org.mule.api.transport.OutputHandler;

import java.io.InputStream;
import java.util.Iterator;

public final class VisitableContentWrapper
{

    private final Object content;

    public VisitableContentWrapper(Object content)
    {
        this.content = content;
    }

    public void accept(FileWriter writer) throws Exception
    {
        if (content instanceof String)
        {
            writer.write((String) content);
        }
        else if (content instanceof InputStream)
        {
            writer.write((InputStream) content);
        }
        else if (content instanceof OutputHandler)
        {
            writer.write((OutputHandler) content);
        }
        else if (content instanceof String[])
        {
            writer.write((String[]) content);
        }
        else if (content instanceof Iterable)
        {
            writer.write((Iterable) content);
        }
        else if (content instanceof Iterator)
        {
            writer.write((Iterator) content);
        }
        else if (content instanceof byte[])
        {
            writer.write((byte[]) content);
        }
        else if (byte.class.isAssignableFrom(content.getClass()))
        {
            writer.write((byte) content);
        }
        else if (content instanceof Byte)
        {
            writer.write((Byte) content);
        }
        else
        {
            writer.write(content.toString());
        }
    }

}
