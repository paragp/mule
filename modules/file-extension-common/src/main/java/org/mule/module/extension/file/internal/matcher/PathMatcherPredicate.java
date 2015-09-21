/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.internal.matcher;

import static org.mule.util.Preconditions.checkArgument;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class PathMatcherPredicate implements Predicate<String>
{

    private static final String GLOB_PREFIX = "glob:";
    private static final String REGEX_PREFIX = "regex:";

    private final Predicate<String> delegate;

    public PathMatcherPredicate(String pattern)
    {
        delegate = getPredicateForFilename(pattern);
    }

    @Override
    public boolean test(String path)
    {
        checkArgument(!StringUtils.isBlank(path), "Cannot match a blank filename");
        return delegate.test(path);
    }

    private Predicate<String> getPredicateForFilename(String pattern)
    {
        if (pattern.startsWith(REGEX_PREFIX))
        {
            return Pattern.compile(stripRegexPrefix(pattern)).asPredicate();
        }
        else if (pattern.startsWith(GLOB_PREFIX))
        {
            return getGlobPredicate(pattern);
        }
        else
        {
            return getGlobPredicate(GLOB_PREFIX + pattern);
        }
    }

    private Predicate<String> getGlobPredicate(String pattern)
    {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);
        return path -> matcher.matches(Paths.get(path));
    }

    private String stripRegexPrefix(String pattern)
    {
        return pattern.replaceAll(REGEX_PREFIX, "");
    }
}
