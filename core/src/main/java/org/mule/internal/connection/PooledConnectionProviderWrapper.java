/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.internal.connection;

import org.mule.api.config.PoolingProfile;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ManagementStrategy;
import org.mule.api.connection.ManagementStrategyFactory;

/**
 * A {@link ConnectionProviderWrapper} which decorates the {@link #delegate}
 * with a user configured {@link PoolingProfile}.
 * <p>
 * The purpose of this wrapper is having the {@link #getManagementStrategy(ManagementStrategyFactory)}
 * method use the configured {@link #poolingProfile} instead of the default included
 * in the {@link #delegate}
 * <p>
 * If a {@link #poolingProfile} is not supplied (meaning, it is {@code null}), then the
 * default {@link #delegate} behavior is applied.
 *
 * @since 4.0
 */
public final class PooledConnectionProviderWrapper extends ConnectionProviderWrapper
{

    private final PoolingProfile poolingProfile;

    /**
     * Creates a new instance
     *
     * @param delegate       the {@link ConnectionProvider} to be wrapped
     * @param poolingProfile a nullable {@link PoolingProfile}
     */
    public PooledConnectionProviderWrapper(ConnectionProvider delegate, PoolingProfile poolingProfile)
    {
        super(delegate);
        this.poolingProfile = poolingProfile;
    }

    /**
     * If {@link #poolingProfile} is not {@code null} and the delegate wants to invoke
     * {@link ManagementStrategyFactory#requiresPooling(PoolingProfile)} or
     * {@link ManagementStrategyFactory#supportsPooling(PoolingProfile)}, then this method
     * makes those invokations using the supplied {@link #poolingProfile}.
     * <p>
     * In any other case, the default {@link #delegate} behavior is applied
     *
     * @param managementStrategyFactory a {@link ManagementStrategyFactory}
     * @return a {@link ManagementStrategy}
     */
    @Override
    public ManagementStrategy getManagementStrategy(ManagementStrategyFactory managementStrategyFactory)
    {
        ManagementStrategyFactory factoryDecorator = new ManagementStrategyFactory()
        {
            @Override
            public ManagementStrategy supportsPooling(PoolingProfile defaultPoolingProfile)
            {
                return managementStrategyFactory.supportsPooling(resolvePoolingProfile(defaultPoolingProfile));
            }

            @Override
            public ManagementStrategy requiresPooling(PoolingProfile defaultPoolingProfile)
            {
                return managementStrategyFactory.requiresPooling(resolvePoolingProfile(defaultPoolingProfile));
            }

            @Override
            public ManagementStrategy cached()
            {
                return managementStrategyFactory.cached();
            }

            @Override
            public ManagementStrategy none()
            {
                return managementStrategyFactory.none();
            }

            private PoolingProfile resolvePoolingProfile(PoolingProfile defaultPoolingProfile)
            {
                return poolingProfile != null ? poolingProfile : defaultPoolingProfile;
            }
        };

        return super.getManagementStrategy(factoryDecorator);
    }
}
