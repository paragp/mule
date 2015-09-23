package org.mule.extension.file.internal.lock;

public interface PathLock
{

    boolean isLocked();

    void release();
}
