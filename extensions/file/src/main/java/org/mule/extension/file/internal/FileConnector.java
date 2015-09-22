/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal;

import static java.lang.String.format;
import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleContext;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.capability.Xml;
import org.mule.extension.annotation.api.connector.Connector;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.module.extension.file.FileSystemOperations;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

@Extension(name = "file", description = "File Connector")
@Operations(FileSystemOperations.class)
@Connector(FileConnectionHandler.class)
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/file", namespace = "file", schemaVersion = "4.0")
public class FileConnector implements Initialisable
{

    @Inject
    private MuleContext muleContext;

    @Parameter
    @Optional
    private String baseDir;

    @Parameter
    @Optional
    private String defaultEncoding;

    @Override
    public void initialise() throws InitialisationException
    {
        validateBaseDir();
        validateDefaultEncoding();
    }

    private void validateDefaultEncoding() throws InitialisationException
    {
        if (defaultEncoding == null)
        {
            defaultEncoding = muleContext.getConfiguration().getDefaultEncoding();
            if (defaultEncoding == null)
            {
                throw new InitialisationException(createStaticMessage("Could not obtain default encoding. Please provide a explicit value for the defaultEncoding parameter"), this);
            }
        }
        try
        {
            // Checks that the encoding is valid and supported
            Charset.forName(defaultEncoding);
        }
        catch (Exception e)
        {
            throw new InitialisationException(createStaticMessage(format("Supplied default encoding '%s' is not valid", defaultEncoding)), e, this);
        }
    }

    private void validateBaseDir() throws InitialisationException
    {
        if (baseDir == null)
        {
            baseDir = System.getProperty("user.home");
            if (baseDir != null)
            {
                throw new InitialisationException(createStaticMessage("Could not obtain user's home directory. Please provide a explicit value for the baseDir parameter"), this);
            }
        }
        Path baseDirPath = Paths.get(baseDir);
        if (Files.notExists(baseDirPath))
        {
            throw new InitialisationException(createStaticMessage(format("Provided baseDir '%s' does not exists", baseDirPath.toAbsolutePath())), this);
        }
        if (!Files.isDirectory(baseDirPath))
        {
            throw new InitialisationException(createStaticMessage(format("Provided baseDir '%s' is not a directory", baseDirPath.toAbsolutePath())), this);
        }
    }

    String getBaseDir()
    {
        return baseDir;
    }

    String getDefaultEncoding()
    {
        return defaultEncoding;
    }
}
