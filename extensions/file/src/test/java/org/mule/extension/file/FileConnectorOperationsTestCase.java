/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleEvent;
import org.mule.module.extension.file.FilePayload;
import org.mule.util.FileUtils;
import org.mule.util.IOUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

public class FileConnectorOperationsTestCase extends FileConnectorTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "file-operations-config.xml";
    }

    @Test
    public void read() throws Exception
    {
        FilePayload payload = getFilePayload();
        assertThat(IOUtils.toString(payload.getContent()), is(HELLO_WORLD));
    }

    @Test
    public void readUnexisting() throws Exception
    {
        expectedException.expectCause(instanceOf(IllegalArgumentException.class));
        runFlow("readUnexisting");
    }

    @Test
    public void getProperties() throws Exception
    {
        FilePayload filePayload = getFilePayload();
        Path file = Paths.get(baseDir.getValue()).resolve("files/hello.txt");
        assertExists(true, file.toFile());

        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        assertTime(filePayload.getCreationTime(), attributes.creationTime());
        assertThat(filePayload.getFilename(), equalTo(file.getFileName().toString()));
        assertTime(filePayload.getLastAccessTime(), attributes.lastAccessTime());
        assertTime(filePayload.getLastModifiedTime(), attributes.lastModifiedTime());
        assertThat(filePayload.getPath(), is(file.toAbsolutePath().toString()));
        assertThat(filePayload.getSize(), is(attributes.size()));
        assertThat(filePayload.isDirectory(), is(false));
        assertThat(filePayload.isSymbolicLink(), is(false));
        assertThat(filePayload.isRegularFile(), is(true));
        assertThat(filePayload.getFilename(), is(file.getFileName().toString()));
    }

    @Test
    public void deleteFile() throws Exception
    {
        File file = temporaryFolder.newFile();
        assertExists(true, file);

        MuleEvent event = getTestEvent("");
        event.setFlowVariable("delete", file.getAbsolutePath());
        runFlow("delete", event);

        assertExists(false, file);
    }

    @Test
    public void deleteFolder() throws Exception
    {
        File directory = temporaryFolder.newFolder();
        File child = new File(directory, "file");
        FileUtils.write(child, "child");

        File subFolder = new File(directory, "subfolder");
        subFolder.mkdir();
        File grandChild = new File(subFolder, "grandChild");
        FileUtils.write(grandChild, "grandChild");

        assertExists(true, child, subFolder, grandChild);

        MuleEvent event = getTestEvent("");
        event.setFlowVariable("delete", directory.getAbsolutePath());
        runFlow("delete", event);

        assertExists(false, directory, child, subFolder, grandChild);
    }

    private void assertExists(boolean exists, File... files)
    {
        for (File file : files)
        {
            assertThat(file.exists(), is(exists));
        }
    }

    private FilePayload getFilePayload() throws Exception
    {
        MuleEvent event = runFlow("read");
        return (FilePayload) event.getMessage().getPayload();
    }

    private void assertTime(LocalDateTime dateTime, FileTime fileTime)
    {
        assertThat(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), is(fileTime.toInstant().toEpochMilli()));
    }

}
