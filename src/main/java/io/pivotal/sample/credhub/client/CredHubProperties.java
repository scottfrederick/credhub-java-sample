package io.pivotal.sample.credhub.client;

import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

public class CredHubProperties {
	@Value("${CREDHUB_API}")
	private String api;

	@Value("${CF_INSTANCE_CERT}")
	private String instanceCert;

	@Value("${CF_INSTANCE_KEY}")
	private String instanceKey;

	public CredHubProperties() {
	}

	public CredHubProperties(String api, String instanceCert, String instanceKey) {
		this.api = api;
		this.instanceCert = instanceCert;
		this.instanceKey = instanceKey;
	}

	public String getApi() {
		return api;
	}

	public URI getApiUri() {
		try {
			return new URI(api);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid CredHub API URI '" + api + "'", e);
		}
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getInstanceCert() {
		return instanceCert;
	}

	public void setInstanceCert(String instanceCert) {
		this.instanceCert = instanceCert;
	}

	public String getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
	}
}
