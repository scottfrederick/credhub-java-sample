package io.pivotal.sample.credhub.client;

import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

public class CredHubProperties {
	@Value("${CREDHUB_API}")
	private String apiUrlBase;

	@Value("${CF_INSTANCE_CERT}")
	private String instanceCertLocation;

	@Value("${CF_INSTANCE_KEY}")
	private String instanceKeyLocation;

	public CredHubProperties() {
	}

	public CredHubProperties(String apiUrlBase, String instanceCertLocation, String instanceKeyLocation) {
		this.apiUrlBase = apiUrlBase;
		this.instanceCertLocation = instanceCertLocation;
		this.instanceKeyLocation = instanceKeyLocation;
	}

	public String getApiUrlBase() {
		return apiUrlBase;
	}

	public URI getApiUri() {
		try {
			return new URI(apiUrlBase);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid CredHub API URI '" + apiUrlBase + "'", e);
		}
	}

	public String getInstanceCertLocation() {
		return instanceCertLocation;
	}

	public String getInstanceKeyLocation() {
		return instanceKeyLocation;
	}
}
