/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.matcher;

import java.util.function.BiFunction;

public abstract class MatcherFunction<T> implements BiFunction<T, T, Boolean>
{

    @Override
    public final Boolean apply(T criteria, T value)
    {
        if (criteria == null) {
            return true;
        }

        return test(criteria, value);
    }

    protected abstract boolean test(T criteria, T value);
}
