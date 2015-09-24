/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import org.mule.api.MuleEvent;
import org.mule.extension.file.internal.FileConnector;
import org.mule.extension.file.internal.LocalFilePayload;
import org.mule.module.extension.file.FileWriteMode;
import org.mule.tck.junit4.ExtensionFunctionalTestCase;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.util.IOUtils;

import java.io.File;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public abstract class FileConnectorTestCase extends ExtensionFunctionalTestCase
{

    protected static final String HELLO_WORLD = "Hello World!";

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = none();

    @Rule
    public SystemProperty baseDir = new SystemProperty("baseDir", temporaryFolder.getRoot().getAbsolutePath());

    @Override
    protected void doSetUpBeforeMuleContextCreation() throws Exception
    {
        if (!temporaryFolder.getRoot().exists())
        {
            temporaryFolder.getRoot().mkdir();
        }
    }

    @Override
    protected void doTearDownAfterMuleContextDispose() throws Exception
    {
        temporaryFolder.delete();
    }

    @Override
    protected Class<?>[] getAnnotatedExtensionClasses()
    {
        return new Class<?>[] {FileConnector.class};
    }

    protected void assertExists(boolean exists, File... files)
    {
        for (File file : files)
        {
            assertThat(file.exists(), is(exists));
        }
    }

    protected LocalFilePayload readHelloWorld() throws Exception
    {
        return readPath("files/hello.txt");
    }

    protected LocalFilePayload readPath(String path) throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("path", path);
        MuleEvent response = runFlow("read", event);

        return (LocalFilePayload) response.getMessage().getPayload();
    }

    protected String readPathAsString(String path) throws Exception
    {
        return IOUtils.toString(readPath(path).getContent());
    }

    protected void doWrite(String path, Object content, FileWriteMode mode, boolean createParent) throws Exception
    {
        MuleEvent event = getTestEvent(content);
        event.setFlowVariable("path", path);
        event.setFlowVariable("createParent", createParent);
        event.setFlowVariable("mode", mode);

        runFlow("write", event);
    }
}
