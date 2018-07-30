/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.test.starter.constructor.test;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ConnectionManager;
import com.hazelcast.nio.tcp.FirewallingConnectionManager;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.starter.constructor.FirewallingConnectionManagerConstructor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Set;

import static com.hazelcast.test.starter.ReflectionUtils.getFieldValueReflectively;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelTest.class})
public class FirewallingConnectionManagerConstructorTest {

    @Test
    public void testConstructor() throws Exception {
        ConnectionManager delegate = mock(ConnectionManager.class);
        Address address = new Address("172.16.16.1", 4223);
        Set<Address> blockedAddresses = Collections.singleton(address);

        FirewallingConnectionManager connectionManager = new FirewallingConnectionManager(delegate, blockedAddresses);

        FirewallingConnectionManagerConstructor constructor
                = new FirewallingConnectionManagerConstructor(FirewallingConnectionManager.class);
        FirewallingConnectionManager clonedConnectionManager
                = (FirewallingConnectionManager) constructor.createNew(connectionManager);

        assertEquals(delegate, getFieldValueReflectively(clonedConnectionManager, "delegate"));
        assertEquals(blockedAddresses, getFieldValueReflectively(clonedConnectionManager, "blockedAddresses"));
    }
}
