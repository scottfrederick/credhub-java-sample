package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.CredHubProperties;
import io.pivotal.sample.credhub.client.CredHubRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CredHubConfiguration {
	@Bean
	public RestTemplate credHubRestTemplate(CredHubProperties properties) {
		return new CredHubRestTemplate(properties);
	}

	@Bean
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}
}
