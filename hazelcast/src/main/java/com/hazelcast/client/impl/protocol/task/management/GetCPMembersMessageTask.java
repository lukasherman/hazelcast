/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.client.impl.protocol.task.management;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MCGetCPMembersCodec;
import com.hazelcast.client.impl.protocol.codec.MCGetCPMembersCodec.RequestParameters;
import com.hazelcast.client.impl.protocol.task.AbstractAsyncMessageTask;
import com.hazelcast.cluster.Address;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.nio.Connection;

import java.security.Permission;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GetCPMembersMessageTask extends AbstractAsyncMessageTask<RequestParameters, List<SimpleEntry<UUID, Address>>> {

    public GetCPMembersMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CompletableFuture<List<SimpleEntry<UUID, Address>>> processInternal() {
        CPSubsystemManagementService cpService =
                nodeEngine.getHazelcastInstance().getCPSubsystem().getCPSubsystemManagementService();
        return cpService.getCPMembers().toCompletableFuture()
                .thenApply(cpMembers -> {
                    List<SimpleEntry<UUID, Address>> result = new ArrayList<>(cpMembers.size());
                    for (CPMember cpMember : cpMembers) {
                        result.add(new SimpleEntry<>(cpMember.getUuid(), cpMember.getAddress()));
                    }
                    return result;
                });
    }

    @Override
    protected RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MCGetCPMembersCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MCGetCPMembersCodec.encodeResponse((List<Map.Entry<UUID, Address>>) response);
    }

    @Override
    public String getServiceName() {
        return ManagementCenterService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "getCPMembers";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}
