package io.pivotal.sample.credhub.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class BaseResponseDeserializationTest {
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void deserializationWithPasswordType() throws Exception {
		String json = "{\n" +
				"  \"data\": [\n" +
				"    {\n" +
				"      \"type\": \"password\",\n" +
				"      \"version_created_at\": \"2017-04-14T19:37:28Z\",\n" +
				"      \"id\": \"80cbb13f-7562-4e72-92de-f3ccf69eaa59\",\n" +
				"      \"name\": \"/c/service-broker-name/service-instance-name/binding-id/credentials-json\",\n" +
				"      \"value\": \"secret\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		BaseResponse response = parseResponse(json);

		assertThat(response.getData(), hasSize(1));
		assertThat(response.getData().get(0).getValueType(), equalTo(ValueType.PASSWORD));
		assertThat(response.getData().get(0).getVersionCreatedAt(), equalTo("2017-04-14T19:37:28Z"));
		assertThat(response.getData().get(0).getId(), equalTo("80cbb13f-7562-4e72-92de-f3ccf69eaa59"));

		assertThat(response.getData().get(0).getValue(), instanceOf(String.class));
		assertThat(response.getData().get(0).getValue(), equalTo("secret"));

		assertThat(response.getData().get(0).getServiceBrokerName(), equalTo("service-broker-name"));
		assertThat(response.getData().get(0).getServiceOfferingName(), equalTo("service-instance-name"));
		assertThat(response.getData().get(0).getServiceBindingId(), equalTo("binding-id"));
		assertThat(response.getData().get(0).getCredentialName(), equalTo("credentials-json"));

		assertThat(response.getError(), nullValue());
	}

	@Test
	public void deserializationWithJsonType() throws Exception {
		String json = "{\n" +
				"  \"data\": [\n" +
				"    {\n" +
				"      \"type\": \"json\",\n" +
				"      \"version_created_at\": \"2017-04-14T19:37:28Z\",\n" +
				"      \"id\": \"80cbb13f-7562-4e72-92de-f3ccf69eaa59\",\n" +
				"      \"name\": \"/c/service-broker-name/service-instance-name/binding-id/credentials-json\",\n" +
				"      \"value\": {\n" +
				"        \"client_id\": \"test-id\",\n" +
				"        \"client_secret\": \"test-secret\",\n" +
				"        \"uri\": \"https://example.com\"\n" +
				"      }\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		BaseResponse response = parseResponse(json);

		assertThat(response.getData(), hasSize(1));
		assertThat(response.getData().get(0).getValueType(), equalTo(ValueType.JSON));
		assertThat(response.getData().get(0).getVersionCreatedAt(), equalTo("2017-04-14T19:37:28Z"));
		assertThat(response.getData().get(0).getId(), equalTo("80cbb13f-7562-4e72-92de-f3ccf69eaa59"));

		assertThat(response.getData().get(0).getValue(), instanceOf(Map.class));

		Map<String, Object> valueMap = (Map<String, Object>) response.getData().get(0).getValue();
		assertThat(valueMap.get("client_id"), equalTo("test-id"));
		assertThat(valueMap.get("client_secret"), equalTo("test-secret"));
		assertThat(valueMap.get("uri"), equalTo("https://example.com"));

		assertThat(response.getData().get(0).getServiceBrokerName(), equalTo("service-broker-name"));
		assertThat(response.getData().get(0).getServiceOfferingName(), equalTo("service-instance-name"));
		assertThat(response.getData().get(0).getServiceBindingId(), equalTo("binding-id"));
		assertThat(response.getData().get(0).getCredentialName(), equalTo("credentials-json"));

		assertThat(response.getError(), nullValue());
	}

	@Test
	public void deserializationError() throws Exception {
		String json = "{\n" +
				"  \"error\": \"some error text\"" +
				"}";

		BaseResponse response = parseResponse(json);
		assertThat(response.getError(), equalTo("some error text"));

		assertThat(response.getData(), nullValue());
	}

	private BaseResponse parseResponse(String json) throws java.io.IOException {
		return objectMapper.readValue(json, BaseResponse.class);
	}
}
