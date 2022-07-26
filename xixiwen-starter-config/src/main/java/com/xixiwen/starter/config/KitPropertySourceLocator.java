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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Zookeeper provides a <a href="https://zookeeper.apache.org/doc/current/zookeeperOver.html#sc_dataModelNameSpace">hierarchical namespace</a> that allows
 * clients to store arbitrary data, such as configuration data.  Spring Cloud Zookeeper Config is an alternative to the
 * <a href="https://github.com/spring-cloud/spring-cloud-config">Config Server and Client</a>.  Configuration is loaded into the Spring Environment during
 * the special "bootstrap" phase.  Configuration is stored in the {@code /config} namespace by default.  Multiple
 * {@code PropertySource} instances are created based on the application's name and the active profiles that mimicks the Spring Cloud Config
 * order of resolving properties.  For example, an application with the name "testApp" and with the "dev" profile will have the following property sources
 * created:
 *
 * <pre>{@code
 * config/testApp,dev
 * config/testApp
 * config/application,dev
 * config/application
 * }</pre>
 *
 * </p>
 * The most specific property source is at the top, with the least specific at the
 * bottom.  Properties is the {@code config/application} namespace are applicable to all applications
 * using zookeeper for configuration.  Properties in the {@code config/testApp} namespace are only available
 * to the instances of the service named "testApp".
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public class KitPropertySourceLocator implements PropertySourceLocator {

	private KitConfigProperties properties;

	private CuratorFramework curator;

	private List<String> contexts;

	private static final Log log = LogFactory.getLog(KitPropertySourceLocator.class);

	public KitPropertySourceLocator(CuratorFramework curator, KitConfigProperties properties) {
		this.curator = curator;
		this.properties = properties;
	}

	public List<String> getContexts() {
		return this.contexts;
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
			String appName = env.getProperty("spring.application.name");
			if (appName == null) {
				// use default "application" (which config client does)
				appName = "application";
				log.warn("spring.application.name is not set. Using default of 'application'");
			}
			List<String> profiles = Arrays.asList(env.getActiveProfiles());
			String root=env.getProperty("xixiwen.starter.kit.config.root");
            if(StringUtils.isEmpty(root)){
				 root = this.properties.getRoot();
			}
			this.contexts = new ArrayList<>();
            String propertiesDefaultContext = env.getProperty("xixiwen.starter.kit.config.defaultContext");
			if(StringUtils.isEmpty(propertiesDefaultContext)){
				propertiesDefaultContext=this.properties.getDefaultContext();
			}
			String defaultContext = root + "/" + propertiesDefaultContext;
			this.contexts.add(defaultContext);
			addProfiles(this.contexts, defaultContext, profiles);

			StringBuilder baseContext = new StringBuilder(root);
			if (!appName.startsWith("/")) {
				baseContext.append("/");
			}
			baseContext.append(appName);
			this.contexts.add(baseContext.toString());
			addProfiles(this.contexts, baseContext.toString(), profiles);

			CompositePropertySource composite = new CompositePropertySource("zookeeper");

			Collections.reverse(this.contexts);

			for (String propertySourceContext : this.contexts) {
				try {
					PropertySource propertySource = create(propertySourceContext);
					composite.addPropertySource(propertySource);
					// TODO: howto call close when /refresh
				} catch (Exception e) {
					if (this.properties.isFailFast()) {
						ReflectionUtils.rethrowRuntimeException(e);
					} else {
						log.warn("Unable to load zookeeper config from " + propertySourceContext, e);
					}
				}
			}

			return composite;
		}
		return null;
	}

	@PreDestroy
	public void destroy() {
	}

	private PropertySource<CuratorFramework> create(String context) {
		return new KitPropertySource(context, this.curator);
	}

	private void addProfiles(List<String> contexts, String baseContext,
			List<String> profiles) {
		for (String profile : profiles) {
			contexts.add(baseContext + this.properties.getProfileSeparator() + profile);
		}
	}
}
