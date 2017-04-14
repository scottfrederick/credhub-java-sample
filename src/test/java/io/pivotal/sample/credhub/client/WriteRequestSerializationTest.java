package io.pivotal.sample.credhub.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static io.pivotal.sample.credhub.client.AccessControlEntry.Operation.READ;
import static io.pivotal.sample.credhub.client.AccessControlEntry.Operation.WRITE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;

public class WriteRequestSerializationTest {

	private ObjectMapper mapper;

	private WriteRequest.WriteRequestBuilder requestBuilder;

	@Before
	public void setUp() {
		mapper = new ObjectMapper();

		requestBuilder = WriteRequest.builder()
				.serviceBrokerName("broker-name")
				.serviceOfferingName("service-name")
				.serviceBindingId("binding-id");
	}

	@Test
	public void typeIsSerializable() {
		assertTrue(mapper.canSerialize(WriteRequest.class));
	}

	@Test
	public void serializationWithJson() throws Exception {
		WriteRequest request = requestBuilder
				.credentialName("credentials")
				.valueType(ValueType.JSON)
				.value(new HashMap<String, Object>() {{
					put("data", "value");
					put("test", true);
				}})
				.build();

		String jsonValue = mapper.writeValueAsString(request);

		assertThat(jsonValue, isJson());

		assertThat(jsonValue, allOf(
				hasJsonPath("$.overwrite", equalTo(false)),
				hasJsonPath("$.name", equalTo("/c/broker-name/service-name/binding-id/credentials")),
				hasJsonPath("$.type", equalTo("json")),
				hasJsonPath("$.value.data", equalTo("value")),
				hasJsonPath("$.value.test", equalTo(true))
		));

		assertThat(jsonValue, hasNoJsonPath("$.access_control_entries"));
	}

	@Test
	public void serializationWithPassword() throws Exception {
		WriteRequest request = requestBuilder
				.overwrite(true)
				.credentialName("password")
				.valueType(ValueType.PASSWORD)
				.value("secret")
				.build();

		String jsonValue = mapper.writeValueAsString(request);

		assertThat(jsonValue, isJson());

		assertThat(jsonValue, allOf(
				hasJsonPath("$.overwrite", equalTo(true)),
				hasJsonPath("$.name", equalTo("/c/broker-name/service-name/binding-id/password")),
				hasJsonPath("$.type", equalTo("password")),
				hasJsonPath("$.value", equalTo("secret"))
		));

		assertThat(jsonValue, hasNoJsonPath("$.access_control_entries"));
	}

	@Test
	public void serializationWithOneAccessControl() throws Exception {
		WriteRequest request = requestBuilder
				.credentialName("password")
				.valueType(ValueType.PASSWORD)
				.value("secret")
				.accessControlEntry(AccessControlEntry.builder()
						.app("app-id")
						.operation(READ)
						.build())
				.build();

		String jsonValue = mapper.writeValueAsString(request);

		assertThat(jsonValue, allOf(
				hasJsonPath("$.access_control_entries", hasSize(1)),
				hasJsonPath("$.access_control_entries[0].actor", equalTo("mtls-app:app-id")),
				hasJsonPath("$.access_control_entries[0].operations", hasSize(1)),
				hasJsonPath("$.access_control_entries[0].operations[0]", equalTo("read"))
		));
	}

	@Test
	public void serializationWithTwoAccessControls() throws Exception {
		WriteRequest request = requestBuilder
				.credentialName("password")
				.valueType(ValueType.PASSWORD)
				.value("secret")
				.accessControlEntry(AccessControlEntry.builder()
						.app("app1-id")
						.operation(READ)
						.operation(WRITE)
						.build())
				.accessControlEntry(AccessControlEntry.builder()
						.app("app2-id")
						.operation(WRITE)
						.operation(READ)
						.build())
				.build();

		String jsonValue = mapper.writeValueAsString(request);

		assertThat(jsonValue, allOf(
				hasJsonPath("$.access_control_entries", hasSize(2)),
				hasJsonPath("$.access_control_entries[0].actor", equalTo("mtls-app:app1-id")),
				hasJsonPath("$.access_control_entries[0].operations", hasSize(2)),
				hasJsonPath("$.access_control_entries[0].operations[0]", equalTo("read")),
				hasJsonPath("$.access_control_entries[0].operations[1]", equalTo("write")),
				hasJsonPath("$.access_control_entries[1].actor", equalTo("mtls-app:app2-id")),
				hasJsonPath("$.access_control_entries[1].operations", hasSize(2)),
				hasJsonPath("$.access_control_entries[1].operations[0]", equalTo("write")),
				hasJsonPath("$.access_control_entries[1].operations[1]", equalTo("read"))
		));
	}

}