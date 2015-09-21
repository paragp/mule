package org.mule.module.extension.file;

public interface FileSystem
{

    FilePayload read(String path, boolean lock, ContentType contentType);
}
