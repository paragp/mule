/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.internal.connection;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ManagedConnection;

/**
 * A {@link ManagedConnectionAdapter} which adds no behavior.
 * <p>
 * Each invokation to {@link #getManagedConnection()} creates a new connection which is
 * closed when {@link ManagedConnection#release()} is invoked.
 *
 * @param <Config>     the generic type of the config that owns {@code this} managed connection
 * @param <Connection> the generic type of the connection being wrapped
 * @since 4.0
 */
final class NullManagementStrategy<Config, Connection> extends ManagementStrategyAdapter<Config, Connection>
{

    public NullManagementStrategy(Config config, ConnectionProvider<Config, Connection> connectionProvider, MuleContext muleContext)
    {
        super(config, connectionProvider, muleContext);
    }

    /**
     * Creates a new {@code Connection} by invoking {@link ConnectionProvider#connect(Object)} on
     * {@link #connectionProvider} using {@link #config} as argument.
     * <p>
     * The connection will be closed when {@link ManagedConnection#release()} is invoked on
     * the returned {@link ManagedConnection}
     *
     * @return a {@link ManagedConnection}
     * @throws ConnectionException if the connection could not be established
     */
    @Override
    public ManagedConnection<Connection> getManagedConnection() throws ConnectionException
    {
        Connection connection = connectionProvider.connect(config);
        return new NotManagedConnection<>(connection, connectionProvider);
    }

    /**
     * This method does nothing for this implementation. The connection will be closed
     * via {@link ManagedConnection#release()} or {@link ManagedConnectionAdapter#close()}
     */
    @Override
    public void close() throws MuleException
    {

    }
}
