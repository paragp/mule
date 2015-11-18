/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.connector.petstore;

import org.mule.api.connection.ManagementStrategy;
import org.mule.api.connection.ManagementStrategyFactory;
import org.mule.extension.annotation.api.Alias;

@Alias("poolable")
public class PoolablePetStoreConnectionProvider extends PetStoreConnectionProvider
{

    @Override
    public ManagementStrategy<PetStoreClient> getManagementStrategy(ManagementStrategyFactory managementStrategyFactory)
    {
        return managementStrategyFactory.supportsPooling(new PetStorePoolingProfile());
    }
}
