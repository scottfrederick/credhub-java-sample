package io.pivotal.sample.credhub.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseRequest {
	private String serviceBrokerName;
	private String serviceOfferingName;
	private String serviceBindingId;
	private String credentialName;

	public String getName() {
		return "/c" +
				'/' + serviceBrokerName +
				'/' + serviceOfferingName +
				'/' + serviceBindingId +
				'/' + credentialName;
	}
}
