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

import com.alibaba.fastjson.JSON;
import com.xixiwen.starter.core.ConditionalOnKitEnabled;
import com.xixiwen.starter.core.KitAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
@ConditionalOnKitEnabled
@Import(KitAutoConfiguration.class)
public class ZookeeperConfigBootstrapConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ZookeeperPropertySourceLocator zookeeperPropertySourceLocator(
			CuratorFramework curator, ZookeeperConfigProperties properties) {
		ZookeeperPropertySourceLocator locator = new ZookeeperPropertySourceLocator(curator, properties);
		log.info("创建的ZookeeperPropertySourceLocator实体信息{}", JSON.toJSONString(locator));
		return locator;
	}

	@Bean
	@ConditionalOnMissingBean
	public ZookeeperConfigProperties zookeeperConfigProperties() {
		ZookeeperConfigProperties properties = new ZookeeperConfigProperties();
		log.info("创建的ZookeeperConfigProperties实体信息{}", JSON.toJSONString(properties));
		return properties;
	}
}
