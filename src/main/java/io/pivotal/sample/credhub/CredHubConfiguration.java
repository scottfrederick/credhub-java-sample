package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.CredHubProperties;
import io.pivotal.sample.credhub.client.CredHubRestTemplate;
import io.pivotal.sample.credhub.client.KeyStoreBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CredHubConfiguration {
	@Bean
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}

	@Bean
	public RestTemplate credHubRestTemplate(CredHubProperties properties, KeyStoreBuilder keyStoreBuilder) {
		return new CredHubRestTemplate(properties.getApiUri(), keyStoreBuilder);
	}

	@Bean
	public KeyStoreBuilder keyStoreBuilder(CredHubProperties properties) {
		return new KeyStoreBuilder(properties.getInstanceCertLocation(), properties.getInstanceKeyLocation());
	}
}
