/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.internal.connection;

import static org.mule.api.lifecycle.LifecycleUtils.assertNotStopping;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ManagedConnection;
import org.mule.api.connection.ManagementStrategy;
import org.mule.api.connector.ConnectionManager;
import org.mule.api.lifecycle.Stoppable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ConnectionManager} which manages connections
 * opened on a specific application.
 *
 * @since 4.0
 */
public final class DefaultConnectionManager implements ConnectionManager, Stoppable
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionManager.class);

    private final Map<ConnectionKey, ManagementStrategyAdapter> connections = new HashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final MuleContext muleContext;

    /**
     * Creates a new instance
     *
     * @param muleContext the {@link MuleContext} of the owned application
     */
    @Inject
    public DefaultConnectionManager(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if invoked while the {@link #muleContext} is stopped or stopping
     */
    public <Config, Connection> void bind(Config owner, ConnectionProvider<Config, Connection> connectionProvider)
    {
        assertNotStopping(muleContext, "Mule is shutting down... cannot bind new connections");

        connectionProvider = new LifecycleAwareConnectionProviderWrapper<>(connectionProvider, muleContext);

        ManagementStrategyAdapter<Config, Connection> managementStrategy = getManagementStrategy(owner, connectionProvider);
        ManagementStrategyAdapter<Config, Connection> previous = null;

        writeLock.lock();
        try
        {
            previous = connections.put(new ConnectionKey(owner), managementStrategy);
        }
        finally
        {
            writeLock.unlock();
        }

        if (previous != null)
        {
            close(previous);
        }
    }

    /**
     * {@inheritDoc}
     */
    //TODO: MULE-9082
    @Override
    public void unbind(Object config)
    {
        ManagementStrategyAdapter managementStrategy;
        writeLock.lock();
        try
        {
            managementStrategy = connections.remove(new ConnectionKey(config));
        }
        finally
        {
            writeLock.unlock();
        }

        if (managementStrategy != null)
        {
            close(managementStrategy);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Config, Connection> ManagedConnection<Connection> getConnection(Config config) throws ConnectionException
    {
        ManagementStrategy<Connection> managementStrategy = null;
        readLock.lock();
        try
        {
            managementStrategy = connections.get(new ConnectionKey(config));
        }
        finally
        {
            readLock.unlock();
        }

        if (managementStrategy == null)
        {
            throw new ConnectionException("No ConnectionProvider has been registered for owner " + config);
        }

        return managementStrategy.getManagedConnection();
    }

    /**
     * Breaks all bindings and closes all connections
     *
     * @throws MuleException in case of error.
     */
    @Override
    public void stop() throws MuleException
    {
        writeLock.lock();
        try
        {
            connections.values().stream().forEach(this::close);
            connections.clear();
        }
        finally
        {
            writeLock.unlock();
        }
    }

    //TODO: MULE-9082
    private void close(ManagementStrategyAdapter managementStrategy)
    {
        try
        {
            managementStrategy.close();
        }
        catch (Exception e)
        {
            LOGGER.warn("An error was found trying to release connections", e);
        }
    }

    private <Config, Connection> ManagementStrategyAdapter<Config, Connection> getManagementStrategy(Config config, ConnectionProvider<Config, Connection> connectionProvider)
    {
        return (ManagementStrategyAdapter<Config, Connection>) connectionProvider.getManagementStrategy(new DefaultManagementStrategyFactory(config, connectionProvider, muleContext));
    }

    private class ConnectionKey
    {

        private final Object key;

        private ConnectionKey(Object key)
        {
            this.key = key;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof ConnectionKey)
            {
                return key == ((ConnectionKey) obj).key;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode(key);
        }
    }
}
