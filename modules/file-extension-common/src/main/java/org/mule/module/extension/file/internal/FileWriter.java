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

public interface FileWriter
{
    void write(String content) throws Exception;

    void write(byte content) throws Exception;

    void write(byte[] content) throws Exception;

    void write(OutputHandler handler) throws Exception;

    void write(String[] content) throws Exception;

    void write(InputStream content) throws Exception;

    void write(Iterable<?> content) throws Exception;

    void write(Iterator<?> content) throws Exception;

}
