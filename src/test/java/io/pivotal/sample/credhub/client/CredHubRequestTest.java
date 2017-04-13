package io.pivotal.sample.credhub.client;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class CredHubRequestTest {
	@Test
	public void generateNameWithAllFields() {
		CredHubRequest request = new CredHubRequest("service-broker-name", "service-offering-name",
				"service-binding-id", "credential-name");

		assertThat(request.getName(), equalTo("/c/service-broker-name/service-offering-name/service-binding-id/credential-name"));
	}
}