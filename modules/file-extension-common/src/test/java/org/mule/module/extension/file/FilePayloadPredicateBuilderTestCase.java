/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

@SmallTest
public class FilePayloadPredicateBuilderTestCase extends AbstractMuleTestCase
{

    private static final String FILENAME = "Mule.java";
    private static final String PATH = "a/b/c/" + FILENAME;
    private static final LocalDateTime CREATION_TIME = LocalDateTime.of(1983, 4, 20, 21, 15);
    private static final LocalDateTime MODIFIED_TIME = LocalDateTime.of(2011, 2, 5, 22, 00);
    private static final LocalDateTime ACCESSED_TIME = LocalDateTime.of(2015, 4, 20, 00, 00);
    private static final long SIZE = 1024;

    private FilePayloadPredicateBuilder builder = new FilePayloadPredicateBuilder();
    private FilePayload payload;

    @Before
    public void before()
    {
        payload = mock(FilePayload.class);
        when(payload.getFilename()).thenReturn(FILENAME);
        when(payload.getPath()).thenReturn(PATH);
        when(payload.getCreationTime()).thenReturn(CREATION_TIME);
        when(payload.getLastModifiedTime()).thenReturn(MODIFIED_TIME);
        when(payload.getLastAccessTime()).thenReturn(ACCESSED_TIME);
        when(payload.getSize()).thenReturn(SIZE);
        when(payload.isRegularFile()).thenReturn(true);
        when(payload.isSymbolicLink()).thenReturn(false);
        when(payload.isDirectory()).thenReturn(false);
    }

    @Test
    public void matchesAll()
    {
        builder.filenamePattern("glob:*.{java, js}")
                .pathPattern("glob:**.{java, js}")
                .createdSince(LocalDateTime.of(1980, 1, 1, 0, 0))
                .createdUntil(LocalDateTime.of(1990, 1, 1, 0, 0))
                .updatedSince(LocalDateTime.of(2010, 9, 24, 0, 0))
                .updatedUntil(LocalDateTime.of(2013, 11, 3, 6, 0))
                .accessedSince(LocalDateTime.of(2013, 11, 3, 0, 0))
                .accessedUntil(LocalDateTime.of(2015, 4, 20, 0, 0))
                .isRegularFile(true)
                .isDirectory(false)
                .isSymbolicLink(false)
                .minSize(1L)
                .maxSize(1024L);

        assertMatch();
    }

    @Test
    public void matchesManyButFailsOne()
    {
        matchesAll();
        builder.maxSize(1L);

        assertReject();
    }

    @Test
    public void matchFilenameLiterally()
    {
        builder.filenamePattern(FILENAME);
        assertMatch();
    }

    @Test
    public void rejectFilenameLiterally()
    {
        builder.filenamePattern("fail.pdf");
        assertReject();
    }

    @Test
    public void matchFilenameByGlob()
    {
        builder.filenamePattern("glob:*.{java, js}");
        assertMatch();
    }

    @Test
    public void rejectFilenameByGlob()
    {
        builder.filenamePattern("glob:*.{pdf}");
        assertReject();
    }

    @Test
    public void matchFilenameByRegex()
    {
        when(payload.getFilename()).thenReturn("20060101_test.csv");
        builder.filenamePattern("regex:[0-9]*_test.csv");
        assertMatch();
    }

    @Test
    public void rejectFilenameByRegex()
    {
        when(payload.getFilename()).thenReturn("20060101_TEST.csv");
        builder.filenamePattern("regex:[0-9]*_test.csv");
        assertReject();
    }

    @Test
    public void matchPathLiterally()
    {
        builder.pathPattern(PATH);
        assertMatch();
    }

    @Test
    public void rejectPathLiterally()
    {
        builder.pathPattern("a/b/d/Mule.pdf");
        assertReject();
    }

    @Test
    public void matchPathByGlob()
    {
        builder.pathPattern("glob:**.{java, js}");
        assertMatch();
    }

    @Test
    public void rejectPathByGlob()
    {
        builder.pathPattern("glob:*.{java, js}");
        assertReject();
    }

    @Test
    public void matchPathByRegex()
    {
        when(payload.getPath()).thenReturn("a/b/c/20060101_test.csv");
        builder.pathPattern("regex:a/b/c/[0-9]*_test.csv");
        assertMatch();
    }

    @Test
    public void rejectPathByRegex()
    {
        when(payload.getFilename()).thenReturn("20060101_TEST.csv");
        builder.filenamePattern("regex:[0-9]*_test.csv");
        assertReject();
    }

    @Test
    public void createdSince()
    {
        builder.createdSince(LocalDateTime.of(1980, 1, 1, 0, 0));
        assertMatch();
    }

    @Test
    public void createdUntil()
    {
        builder.createdUntil(LocalDateTime.of(1990, 1, 1, 0, 0));
        assertMatch();
    }

    @Test
    public void rejectCreatedSince()
    {
        builder.createdSince(LocalDateTime.of(1984, 1, 1, 0, 0));
        assertReject();
    }

    @Test
    public void rejectCreatedUntil()
    {
        builder.createdUntil(LocalDateTime.of(1982, 4, 2, 0, 0));
        assertReject();
    }

    @Test
    public void updateSince()
    {
        builder.updatedSince(LocalDateTime.of(2010, 9, 24, 0, 0));
        assertMatch();
    }

    @Test
    public void updatedUntil()
    {
        builder.updatedUntil(LocalDateTime.of(2013, 11, 3, 6, 0));
        assertMatch();
    }

    @Test
    public void rejectUpdatedSince()
    {
        builder.updatedSince(LocalDateTime.of(2015, 1, 1, 0, 0));
        assertReject();
    }

    @Test
    public void rejectUpdatedUntil()
    {
        builder.updatedUntil(LocalDateTime.of(2010, 9, 24, 0, 0));
        assertReject();
    }

    @Test
    public void accessedSince()
    {
        builder.accessedSince(LocalDateTime.of(2013, 11, 3, 0, 0));
        assertMatch();
    }

    @Test
    public void accessedUntil()
    {
        builder.accessedUntil(LocalDateTime.of(2015, 4, 20, 0, 0));
        assertMatch();
    }

    @Test
    public void rejectAccessedSince()
    {
        builder.accessedSince(LocalDateTime.of(2016, 1, 1, 0, 0));
        assertReject();
    }

    @Test
    public void rejectAccessedUntil()
    {
        builder.updatedUntil(LocalDateTime.of(2010, 9, 24, 0, 0));
        assertReject();
    }

    @Test
    public void minSize()
    {
        builder.minSize(1L);
        assertMatch();
    }

    @Test
    public void maxSize()
    {
        builder.maxSize(1024L);
        assertMatch();
    }

    @Test
    public void rejectMinSize()
    {
        builder.minSize(2048L);
        assertReject();
    }

    @Test
    public void rejectMaxSize()
    {
        builder.maxSize(500L);
        assertReject();
    }

    @Test
    public void regularFile()
    {
        when(payload.isRegularFile()).thenReturn(true);
        builder.isRegularFile(true);
        assertMatch();
    }

    @Test
    public void rejectNotRegularFile()
    {
        when(payload.isRegularFile()).thenReturn(false);
        builder.isRegularFile(true);
        assertReject();
    }

    @Test
    public void rejectRegularFile()
    {
        when(payload.isRegularFile()).thenReturn(true);
        builder.isRegularFile(false);
        assertReject();
    }

    @Test
    public void isDirectory()
    {
        when(payload.isDirectory()).thenReturn(true);
        builder.isDirectory(true);
        assertMatch();
    }

    @Test
    public void rejectNotDirectory()
    {
        when(payload.isDirectory()).thenReturn(false);
        builder.isDirectory(true);
        assertReject();
    }

    @Test
    public void rejectDirectory()
    {
        when(payload.isDirectory()).thenReturn(true);
        builder.isDirectory(false);
        assertReject();
    }


    @Test
    public void isSymbolicLink()
    {
        when(payload.isSymbolicLink()).thenReturn(true);
        builder.isSymbolicLink(true);
        assertMatch();
    }

    @Test
    public void rejectNotSymbolicLink()
    {
        when(payload.isSymbolicLink()).thenReturn(false);
        builder.isSymbolicLink(true);
        assertReject();
    }

    @Test
    public void rejectSymbolicLink()
    {
        when(payload.isSymbolicLink()).thenReturn(true);
        builder.isSymbolicLink(false);
        assertReject();
    }

    private void assertMatch()
    {
        assertThat(builder.build().test(payload), is(true));
    }

    private void assertReject()
    {
        assertThat(builder.build().test(payload), is(false));
    }
}
