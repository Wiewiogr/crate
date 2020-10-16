/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.netty;

import java.util.concurrent.ThreadFactory;

import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.netty4.Netty4Transport;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import static org.elasticsearch.common.util.concurrent.EsExecutors.daemonThreadFactory;

@Singleton
public class EventLoopGroups {

    private EventLoopGroup worker;

    public EventLoopGroup getEventLoopGroup(Settings settings) {
        if (worker == null) {
            ThreadFactory workerThreads = daemonThreadFactory(settings, "netty-worker");
            if (Epoll.isAvailable()) {
                worker = new EpollEventLoopGroup(Netty4Transport.WORKER_COUNT.get(settings), workerThreads);
            } else {
                worker = new NioEventLoopGroup(Netty4Transport.WORKER_COUNT.get(settings), workerThreads);
            }
        }
        return worker;
    }
}
