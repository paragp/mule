/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.internal.matcher;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public final class TimeSinceFunction implements BiFunction<LocalDateTime, LocalDateTime, Boolean>
{

    @Override
    public Boolean apply(LocalDateTime criteria, LocalDateTime value)
    {
        return value.compareTo(criteria) >=0;
    }
}
