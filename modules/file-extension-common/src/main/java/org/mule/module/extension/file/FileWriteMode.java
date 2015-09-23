/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public enum FileWriteMode
{
    OVERWRITE
            {
                @Override
                public OpenOption[] getOpenOptions()
                {
                    return new OpenOption[] {CREATE, WRITE, TRUNCATE_EXISTING};
                }
            },
    APPEND
            {
                @Override
                public OpenOption[] getOpenOptions()
                {
                    return new OpenOption[] {CREATE, WRITE, StandardOpenOption.APPEND};
                }
            },
    CREATE_NEW
            {
                @Override
                public OpenOption[] getOpenOptions()
                {
                    return new OpenOption[] {WRITE, StandardOpenOption.CREATE_NEW};
                }
            };

    public abstract OpenOption[] getOpenOptions();
}
