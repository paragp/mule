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

import java.nio.file.attribute.FileTime;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Alias("matcher")
public class FileMatcher
{

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private static final BiFunction<FileTime, FileTime, Boolean> FILE_TIME_SINCE = (since, value) -> value.compareTo(since) >=0;

    private static final BiFunction<FileTime, FileTime, Boolean> FILE_TIME_BEFORE = (since, value) -> value.compareTo(since) <=0;



    @Parameter
    @Optional
    private String filename;

    @Parameter
    @Optional
    private FileTime createdSince;

    @Parameter
    @Optional
    private FileTime createdUntil;

    @Parameter
    @Optional
    private FileTime updatedSince;

    @Parameter
    @Optional
    private FileTime updatedUntil;

    @Parameter
    @Optional
    private FileTime accessedSince;

    @Parameter
    @Optional
    private FileTime accessedUntil;

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

    public boolean matches(FilePayload filePayload) {

    }

    private <T> boolean evaluate(Supplier<T> criteriaSupplier, Supplier<T> valueSupplier, BiFunction<T, T, Boolean> testFunction) {
        T criteria = criteriaSupplier.get();
        if (criteria == null) {
            return true;
        }

        return testFunction.apply(criteria, valueSupplier.get());
    }


    public static class Builder
    {

        private final FileMatcher matcher = new FileMatcher();

        private Builder()
        {
        }

        public Builder setFilename(String filename)
        {
            matcher.filename = filename;
            return this;
        }

        public Builder setCreatedSince(FileTime createdSince)
        {
            matcher.createdSince = createdSince;
            return this;
        }

        public Builder setCreatedUntil(FileTime createdUntil)
        {
            matcher.createdUntil = createdUntil;
            return this;
        }

        public Builder setUpdatedSince(FileTime updatedSince)
        {
            matcher.updatedSince = updatedSince;
            return this;
        }

        public Builder setUpdatedUntil(FileTime updatedUntil)
        {
            matcher.updatedUntil = updatedUntil;
            return this;
        }

        public Builder setAccessedSince(FileTime accessedSince)
        {
            matcher.accessedSince = accessedSince;
            return this;
        }

        public Builder setAccessedUntil(FileTime accessedUntil)
        {
            matcher.accessedUntil = accessedUntil;
            return this;
        }

        public Builder setDirectory(Boolean directory)
        {
            matcher.directory = directory;
            return this;
        }

        public Builder setRegularFile(Boolean regularFile)
        {
            matcher.regularFile = regularFile;
            return this;
        }

        public Builder setSymbolicLink(Boolean symbolicLink)
        {
            matcher.symbolicLink = symbolicLink;
            return this;
        }

        public Builder setMinSize(Long minSize)
        {
            matcher.minSize = minSize;
            return this;
        }

        public Builder setMaxSize(Long maxSize)
        {
            matcher.maxSize = maxSize;
            return this;
        }

        public FileMatcher build()
        {
            return matcher;
        }
    }


}
