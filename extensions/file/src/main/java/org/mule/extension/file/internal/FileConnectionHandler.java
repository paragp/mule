/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import org.mule.extension.api.connection.ConnectionHandler;

public final class FileConnectionHandler implements ConnectionHandler<FileConnector, LocalFileSystem>
{

    @Override
    public LocalFileSystem connect(FileConnector fileConnector)
    {
        return new LocalFileSystem(fileConnector);
    }

    @Override
    public void disconnect(LocalFileSystem localFileSystem)
    {
        // no-op
    }
}
