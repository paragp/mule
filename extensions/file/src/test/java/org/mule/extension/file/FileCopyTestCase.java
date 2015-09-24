/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleEvent;
import org.mule.util.FileUtils;

import java.io.File;

import org.junit.Test;

public class FileCopyTestCase extends FileConnectorTestCase
{

    private static final String SOURCE_FILE_NAME = "test.txt";

    private String sourcePath;

    @Override
    protected String getConfigFile()
    {
        return "file-copy-config.xml";
    }


    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        File sourceFile = temporaryFolder.newFile(SOURCE_FILE_NAME);
        FileUtils.write(sourceFile, HELLO_WORLD);
        sourcePath = sourceFile.getAbsolutePath();
    }

    @Test
    public void copyToExistingFolder() throws Exception
    {
        String target = temporaryFolder.newFolder().getAbsolutePath();
        doCopy(target, false, false);

        assertCopy(String.format("%s/%s", target, SOURCE_FILE_NAME));
    }

    @Test
    public void copyToNonExistingFolder() throws Exception {
        String target = String.format("%s/%s", temporaryFolder.newFolder().getAbsolutePath(), "a/b/c");
        doCopy(target, false, true);

        assertCopy(String.format("%s/%s", target, SOURCE_FILE_NAME));
    }

    @Test
    public void copyToNonExistingFolderWithoutCreateParent() throws Exception {
        String target = temporaryFolder.newFile().getAbsolutePath() + "a/b/c";
        expectedException.expectCause(instanceOf(IllegalArgumentException.class));
        doCopy(target, false, false);
    }

    @Test
    public void copyAndOverwrite() throws Exception {
        File existingFile = temporaryFolder.newFile();
        FileUtils.write(existingFile, "I was here first!");

        final String target = existingFile.getAbsolutePath();

        doCopy(target, true, false);
        assertCopy(target);
    }

    @Test
    public void copyWithoutOverwrite() throws Exception {
        File existingFile = temporaryFolder.newFile();
        FileUtils.write(existingFile, "I was here first!");

        expectedException.expectCause(instanceOf(IllegalArgumentException.class));
        doCopy(existingFile.getAbsolutePath(), false, false);
    }

    private void doCopy(String target, boolean overwrite, boolean createParentFolder) throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("source", sourcePath);
        event.setFlowVariable("target", target);
        event.setFlowVariable("overwrite", overwrite);
        event.setFlowVariable("createParent", createParentFolder);

        runFlow("copy", event);
    }

    private void assertCopy(String target) throws Exception
    {
        assertThat(readPathAsString(target), equalTo(HELLO_WORLD));
    }
}
