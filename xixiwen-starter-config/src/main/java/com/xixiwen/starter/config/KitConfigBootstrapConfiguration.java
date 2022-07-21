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

package com.xixiwen.starter.config;

import com.xixiwen.starter.core.ConditionalOnKitEnabled;
import com.xixiwen.starter.core.KitAutoConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Bootstrap Configuration for Zookeeper Configuration
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
@Configuration
@ConditionalOnKitEnabled
@Import(KitAutoConfiguration.class)
public class KitConfigBootstrapConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public KitPropertySourceLocator zookeeperPropertySourceLocator(
			CuratorFramework curator, KitConfigProperties properties) {
		return new KitPropertySourceLocator(curator, properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public KitConfigProperties zookeeperConfigProperties() {
		return new KitConfigProperties();
	}
}
