package io.pivotal.sample.credhub.client;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReadRequest extends CredHubRequest {
	@Builder
	public ReadRequest(String serviceBrokerName, String serviceOfferingName, String serviceBindingId, String credentialName) {
		super(serviceBrokerName, serviceOfferingName, serviceBindingId, credentialName);
	}
}
