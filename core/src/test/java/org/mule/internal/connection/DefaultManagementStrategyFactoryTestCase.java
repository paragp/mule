/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.internal.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleContext;
import org.mule.api.config.PoolingProfile;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ManagementStrategy;
import org.mule.api.connection.ManagementStrategyFactory;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultManagementStrategyFactoryTestCase extends AbstractMuleTestCase
{

    @Mock
    private Apple config;

    @Mock
    private Banana connection;

    @Mock
    private ConnectionProvider<Apple, Banana> connectionProvider;

    @Mock
    private MuleContext muleContext;

    private PoolingProfile poolingProfile = new PoolingProfile();

    private ManagementStrategyFactory factory;

    @Before
    public void before()
    {
        factory = new DefaultManagementStrategyFactory<>(config, connectionProvider, muleContext);
    }

    @Test
    public void none()
    {
        assertType(factory.none(), NullManagementStrategy.class);
    }

    @Test
    public void supportsPooling()
    {
        assertType(factory.supportsPooling(poolingProfile), PoolingManagementStrategy.class);
    }

    @Test
    public void disabledPoolingSupport()
    {
        poolingProfile.setDisabled(true);
        assertType(factory.supportsPooling(poolingProfile), NullManagementStrategy.class);
    }

    @Test
    public void requiresPooling()
    {
        assertType(factory.requiresPooling(poolingProfile), PoolingManagementStrategy.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresPoolingWithInvalidProfile()
    {
        poolingProfile.setDisabled(true);
        factory.requiresPooling(poolingProfile);
    }

    @Test
    public void cached()
    {
        assertType(factory.cached(), CachedManagementStrategy.class);
    }

    private void assertType(ManagementStrategy<Banana> strategy, Class<? extends ManagementStrategy> expectedClass)
    {
        assertThat(strategy, is(instanceOf(expectedClass)));
        assertThat(ManagementStrategyAdapter.class.isAssignableFrom(expectedClass), is(true));
    }
}
