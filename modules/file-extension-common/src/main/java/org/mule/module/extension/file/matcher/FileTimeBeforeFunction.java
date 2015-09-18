/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.matcher;

import java.nio.file.attribute.FileTime;

public class FileTimeBeforeFunction extends MatcherFunction<FileTime>
{

    @Override
    protected boolean test(FileTime criteria, FileTime value)
    {
        return value.compareTo(criteria) <= 0;
    }
}
