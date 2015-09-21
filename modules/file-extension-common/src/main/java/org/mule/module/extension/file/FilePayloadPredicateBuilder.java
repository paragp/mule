/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import org.mule.extension.annotation.api.Alias;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.module.extension.file.internal.matcher.PathMatcherPredicate;
import org.mule.module.extension.file.internal.matcher.TimeSinceFunction;
import org.mule.module.extension.file.internal.matcher.TimeUntilFunction;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Alias("matcher")
public class FilePayloadPredicateBuilder
{

    private static final TimeUntilFunction FILE_TIME_UNTIL = new TimeUntilFunction();
    private static final TimeSinceFunction FILE_TIME_SINCE = new TimeSinceFunction();

    @Parameter
    @Optional
    private String filenamePattern;

    @Parameter
    @Optional
    private String pathPattern;

    @Parameter
    @Optional
    private LocalDateTime createdSince;

    @Parameter
    @Optional
    private LocalDateTime createdUntil;

    @Parameter
    @Optional
    private LocalDateTime updatedSince;

    @Parameter
    @Optional
    private LocalDateTime updatedUntil;

    @Parameter
    @Optional
    private LocalDateTime accessedSince;

    @Parameter
    @Optional
    private LocalDateTime accessedUntil;

    @Parameter
    private Boolean directory;

    @Parameter
    @Optional
    private Boolean regularFile;

    @Parameter
    @Optional
    private Boolean symbolicLink;

    @Parameter
    @Optional
    private Long minSize;

    @Parameter
    @Optional
    private Long maxSize;


    public Predicate<FilePayload> build()
    {
        Predicate<FilePayload> predicate = (payload) -> true;
        if (filenamePattern != null)
        {
            PathMatcherPredicate pathMatcher = new PathMatcherPredicate(filenamePattern);
            predicate = predicate.and(payload -> pathMatcher.test(payload.getFilename()));
        }

        if (pathPattern != null)
        {
            PathMatcherPredicate pathMatcher = new PathMatcherPredicate(pathPattern);
            predicate = predicate.and(payload -> pathMatcher.test(payload.getPath()));
        }

        if (createdSince != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_SINCE.apply(createdSince, filePayload.getCreationTime()));
        }

        if (createdUntil != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_UNTIL.apply(createdUntil, filePayload.getCreationTime()));
        }

        if (updatedSince != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_SINCE.apply(updatedSince, filePayload.getLastModifiedTime()));
        }

        if (updatedUntil != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_UNTIL.apply(updatedUntil, filePayload.getLastModifiedTime()));
        }

        if (accessedSince != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_SINCE.apply(accessedSince, filePayload.getLastAccessTime()));
        }

        if (accessedUntil != null)
        {
            predicate = predicate.and(filePayload -> FILE_TIME_SINCE.apply(accessedUntil, filePayload.getLastAccessTime()));
        }

        if (directory != null)
        {
            predicate = predicate.and(filePayload -> directory.equals(filePayload.isDirectory()));
        }

        if (regularFile != null)
        {
            predicate = predicate.and(filePayload -> regularFile.equals(filePayload.isRegularFile()));
        }

        if (symbolicLink != null)
        {
            predicate = predicate.and(filePayload -> symbolicLink.equals(filePayload.isSymbolicLink()));
        }

        if (minSize != null)
        {
            predicate = predicate.and(filePayload -> filePayload.getSize() >= minSize);
        }

        if (maxSize != null)
        {
            predicate = predicate.and(filePayload -> filePayload.getSize() <= maxSize);
        }

        return predicate;
    }

    public FilePayloadPredicateBuilder filenamePattern(String filenamePattern)
    {
        this.filenamePattern = filenamePattern;
        return this;
    }

    public FilePayloadPredicateBuilder pathPattern(String pathPattern)
    {
        this.pathPattern = pathPattern;
        return this;
    }

    public FilePayloadPredicateBuilder createdSince(LocalDateTime createdSince)
    {
        this.createdSince = createdSince;
        return this;
    }

    public FilePayloadPredicateBuilder createdUntil(LocalDateTime createdUntil)
    {
        this.createdUntil = createdUntil;
        return this;
    }

    public FilePayloadPredicateBuilder updatedSince(LocalDateTime updatedSince)
    {
        this.updatedSince = updatedSince;
        return this;
    }

    public FilePayloadPredicateBuilder updatedUntil(LocalDateTime updatedUntil)
    {
        this.updatedUntil = updatedUntil;
        return this;
    }

    public FilePayloadPredicateBuilder accessedSince(LocalDateTime accessedSince)
    {
        this.accessedSince = accessedSince;
        return this;
    }

    public FilePayloadPredicateBuilder accessedUntil(LocalDateTime accessedUntil)
    {
        this.accessedUntil = accessedUntil;
        return this;
    }

    public FilePayloadPredicateBuilder isDirectory(Boolean directory)
    {
        this.directory = directory;
        return this;
    }

    public FilePayloadPredicateBuilder isRegularFile(Boolean regularFile)
    {
        this.regularFile = regularFile;
        return this;
    }

    public FilePayloadPredicateBuilder isSymbolicLink(Boolean symbolicLink)
    {
        this.symbolicLink = symbolicLink;
        return this;
    }

    public FilePayloadPredicateBuilder minSize(Long minSize)
    {
        this.minSize = minSize;
        return this;
    }

    public FilePayloadPredicateBuilder maxSize(Long maxSize)
    {
        this.maxSize = maxSize;
        return this;
    }
}
