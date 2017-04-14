package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.AccessControlEntry;
import io.pivotal.sample.credhub.client.BaseResponse;
import io.pivotal.sample.credhub.client.ReadRequest;
import io.pivotal.sample.credhub.client.ValueType;
import io.pivotal.sample.credhub.client.WriteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static io.pivotal.sample.credhub.client.AccessControlEntry.Operation.READ;

@RestController
public class CredHubController {
	static final String DEFAULT_BROKER_NAME = "service-broker-name";
	static final String DEFAULT_SERVICE_NAME = "service-offering-name";
	static final String DEFAULT_BINDING_ID = "1111-1111-1111-1111";
	static final String DEFAULT_CREDENTIAL_NAME = "credential";

	@Value("${vcap.application.application_id:}")
	private String appId;

	private RestTemplate restTemplate;

	public CredHubController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/read")
	public BaseResponse readData(@RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
								 @RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
								 @RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
								 @RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		ReadRequest request = ReadRequest.builder()
				.serviceBrokerName(brokerName)
				.serviceOfferingName(serviceName)
				.serviceBindingId(bindingId)
				.credentialName(credentialName)
				.build();

		return restTemplate.getForObject("/api/v1/data?name={name}", BaseResponse.class, request.getName());
	}

	@PutMapping(name = "/write", consumes = {"text/plain"})
	public BaseResponse writePasswordData(@RequestBody String value,
										  @RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
										  @RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
										  @RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
										  @RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		return sendWriteRequest(brokerName, serviceName, bindingId, credentialName, value, ValueType.PASSWORD);
	}

	@PutMapping(name = "/write", consumes = {"application/json"})
	public BaseResponse writeJsonData(@RequestBody Map<String, Object> value,
									  @RequestParam(name = "brokerName", required = false, defaultValue = DEFAULT_BROKER_NAME) String brokerName,
									  @RequestParam(name = "serviceName", required = false, defaultValue = DEFAULT_SERVICE_NAME) String serviceName,
									  @RequestParam(name = "bindingId", required = false, defaultValue = DEFAULT_BINDING_ID) String bindingId,
									  @RequestParam(name = "credentialName", required = false, defaultValue = DEFAULT_CREDENTIAL_NAME) String credentialName) {
		return sendWriteRequest(brokerName, serviceName, bindingId, credentialName, value, ValueType.JSON);
	}

	private BaseResponse sendWriteRequest(String brokerName, String serviceName, String bindingId, String credentialName, Object value, ValueType valueType) {
		WriteRequest.WriteRequestBuilder requestBuilder = WriteRequest.builder()
				.overwrite(true)
				.serviceBrokerName(brokerName)
				.serviceOfferingName(serviceName)
				.serviceBindingId(bindingId)
				.credentialName(credentialName)
				.valueType(valueType)
				.value(value);

		if (StringUtils.hasText(appId)) {
			requestBuilder.accessControlEntry(AccessControlEntry.builder()
					.app(appId)
					.operation(READ)
					.build());
		}

		WriteRequest request = requestBuilder.build();

		ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/v1/data", HttpMethod.PUT, new HttpEntity<>(request), BaseResponse.class);
		return response.getBody();
	}

	@ExceptionHandler({ HttpClientErrorException.class })
	public ResponseEntity<ReflectedError> handleException(HttpClientErrorException e) {
		// if there is an error calling CredHub via RestTemplate, reflect the error in the response 
		return new ResponseEntity<>(new ReflectedError(e), e.getStatusCode());
	}

	private class ReflectedError {
		private final String errorMessage = "Error received from CredHub server";
		private final String detail;

		ReflectedError(HttpClientErrorException exception) {
			detail = exception.getResponseBodyAsString();
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public String getDetail() {
			return detail;
		}
	}
}
