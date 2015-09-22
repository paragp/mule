/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file;

import static org.junit.rules.ExpectedException.none;
import static org.mule.util.ClassUtils.getPathURL;
import org.mule.extension.file.internal.FileConnector;
import org.mule.tck.junit4.ExtensionFunctionalTestCase;
import org.mule.tck.junit4.rule.SystemProperty;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public abstract class FileConnectorTestCase extends ExtensionFunctionalTestCase
{
    protected static final String HELLO_WORLD = "Hello World!";

    @Rule
    public SystemProperty baseDir = new SystemProperty("baseDir", getPathURL(FileConnector.class).getPath() + "/target/test-classes/");

    @Rule
    public ExpectedException expectedException = none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    protected Class<?>[] getAnnotatedExtensionClasses()
    {
        return new Class<?>[] {FileConnector.class};
    }
}
