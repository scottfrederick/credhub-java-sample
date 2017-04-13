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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class CredHubController {
	static final String TEST_BROKER_NAME = "service-broker-name";
	static final String TEST_SERVICE_NAME = "service-instance-name";
	static final String TEST_BINDING_ID = "1111-1111-1111-1111";
	static final String TEST_CREDENTIAL_NAME = "credentials-json";

	private RestTemplate restTemplate;

	public CredHubController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/read")
	public String readData() {
		ReadRequest request = ReadRequest.builder()
				.serviceBrokerName(TEST_BROKER_NAME)
				.serviceOfferingName(TEST_SERVICE_NAME)
				.serviceBindingId(TEST_BINDING_ID)
				.credentialName(TEST_CREDENTIAL_NAME)
				.build();

		return restTemplate.getForObject("/api/v1/data?name={name}", String.class, request.getName());
	}

	@PutMapping(name = "/write", consumes = { "text/plain" })
	public String writePasswordData(@RequestBody String value) {
		return sendWriteRequest(value, ValueType.PASSWORD);
	}

	@PutMapping(name = "/write", consumes = { "application/json" })
	public String writeJsonData(@RequestBody Map<String, Object> value) {
		return sendWriteRequest(value, ValueType.JSON);
	}

	private String sendWriteRequest(Object value, ValueType valueType) {
		WriteRequest request = WriteRequest.builder()
				.overwrite(true)
				.serviceBrokerName(TEST_BROKER_NAME)
				.serviceOfferingName(TEST_SERVICE_NAME)
				.serviceBindingId(TEST_BINDING_ID)
				.credentialName(TEST_CREDENTIAL_NAME)
				.valueType(valueType)
				.value(value)
				.build();

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/data", HttpMethod.PUT, new HttpEntity<>(request), String.class);
		return response.getBody();
	}

}
