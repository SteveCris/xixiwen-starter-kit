/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xixiwen.starter.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration}
 * that sets up Zookeeper discovery.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
@Configuration
@ConditionalOnKitEnabled
@EnableConfigurationProperties
public class ZookeeperAutoConfiguration {

	private static final Log log = LogFactory.getLog(KitAutoConfiguration.class);

	@Autowired(required = false)
	private EnsembleProvider ensembleProvider;

	@Bean
	@ConditionalOnMissingBean
	public KitProperties zookeeperProperties() {
		return new KitProperties();
	}


	@Bean(destroyMethod = "close")
	@ConditionalOnMissingBean
	public CuratorFramework curatorFramework(RetryPolicy retryPolicy, KitProperties properties) throws Exception {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		if (this.ensembleProvider != null) {
			builder.ensembleProvider(this.ensembleProvider);
		} else {
			builder.connectString(properties.getConnectString());
		}
		CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
		curator.start();
		log.info("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait() + properties.getBlockUntilConnectedUnit());
		curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
		log.info("connected to zookeeper");
		return curator;
	}

	@Bean
	@ConditionalOnMissingBean
	public RetryPolicy exponentialBackoffRetry(KitProperties properties) {
		return new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(),
				properties.getMaxRetries(),
				properties.getMaxSleepMs());
	}

	@Configuration
	@ConditionalOnClass(Endpoint.class)
	protected static class ZookeeperHealthConfig {
		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnEnabledHealthIndicator("zookeeper")
		public KitHealthIndicator zookeeperHealthIndicator(CuratorFramework curator) {
			return new KitHealthIndicator(curator);
		}
	}
}
