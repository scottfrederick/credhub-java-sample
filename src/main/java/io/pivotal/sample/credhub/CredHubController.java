package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.ReadRequest;
import io.pivotal.sample.credhub.client.ValueType;
import io.pivotal.sample.credhub.client.WriteRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class CredHubController {
	static final String DEFAULT_BROKER_NAME = "service-broker-name";
	static final String DEFAULT_SERVICE_NAME = "service-instance-name";
	static final String DEFAULT_BINDING_ID = "1111-1111-1111-1111";
	static final String DEFAULT_CREDENTIAL_NAME = "credentials-json";

	private RestTemplate restTemplate;

	public CredHubController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/read")
	public String readData(@RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
						   @RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
						   @RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
						   @RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		ReadRequest request = ReadRequest.builder()
				.serviceBrokerName(brokerName)
				.serviceOfferingName(serviceName)
				.serviceBindingId(bindingId)
				.credentialName(credentialName)
				.build();

		return restTemplate.getForObject("/api/v1/data?name={name}", String.class, request.getName());
	}

	@PutMapping(name = "/write", consumes = {"text/plain"})
	public String writePasswordData(@RequestBody String value,
									@RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
									@RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
									@RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
									@RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		return sendWriteRequest(brokerName, serviceName, bindingId, credentialName, value, ValueType.PASSWORD);
	}

	@PutMapping(name = "/write", consumes = {"application/json"})
	public String writeJsonData(@RequestBody Map<String, Object> value,
								@RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
								@RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
								@RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
								@RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		return sendWriteRequest(brokerName, serviceName, bindingId, credentialName, value, ValueType.JSON);
	}

	private String sendWriteRequest(String brokerName, String serviceName, String bindingId, String credentialName, Object value, ValueType valueType) {
		WriteRequest request = WriteRequest.builder()
				.overwrite(true)
				.serviceBrokerName(brokerName)
				.serviceOfferingName(serviceName)
				.serviceBindingId(bindingId)
				.credentialName(credentialName)
				.valueType(valueType)
				.value(value)
				.build();

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/data", HttpMethod.PUT, new HttpEntity<>(request), String.class);
		return response.getBody();
	}
}
