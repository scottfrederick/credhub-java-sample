package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.ReadRequest;
import io.pivotal.sample.credhub.client.ValueType;
import io.pivotal.sample.credhub.client.WriteRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static io.pivotal.sample.credhub.CredHubController.TEST_BINDING_ID;
import static io.pivotal.sample.credhub.CredHubController.TEST_BROKER_NAME;
import static io.pivotal.sample.credhub.CredHubController.TEST_CREDENTIAL_NAME;
import static io.pivotal.sample.credhub.CredHubController.TEST_SERVICE_NAME;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"CREDHUB_API=https://credhub.cf.example.com:8844",
		"CF_INSTANCE_CERT=/etc/cf-instance-credentials/instance.crt",
		"CF_INSTANCE_KEY=/etc/cf-instance-credentials/instance.key"
})
@AutoConfigureMockMvc
public class CredHubControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RestTemplate restTemplate;

	@Test
	public void readSucceeds() throws Exception {
		mockMvc
				.perform(get("/read"))
				.andDo(print())
				.andExpect(status().isOk());

		ReadRequest request = ReadRequest.builder()
				.serviceBrokerName(TEST_BROKER_NAME)
				.serviceOfferingName(TEST_SERVICE_NAME)
				.serviceBindingId(TEST_BINDING_ID)
				.credentialName(TEST_CREDENTIAL_NAME)
				.build();

		verify(restTemplate).getForObject("/api/v1/data?name={name}", String.class, request.getName());
	}

	@Test
	public void writeWithStringSucceeds() throws Exception {
		mockMvc
				.perform(put("/write").content("data").contentType("text/plain"))
				.andDo(print())
				.andExpect(status().isOk());

		verify(restTemplate).put("/api/v1/data",
				WriteRequest.builder()
						.serviceBrokerName(TEST_BROKER_NAME)
						.serviceOfferingName(TEST_SERVICE_NAME)
						.serviceBindingId(TEST_BINDING_ID)
						.credentialName(TEST_CREDENTIAL_NAME)
						.valueType(ValueType.PASSWORD)
						.value("secret")
						.build()
		);
	}

	@Test
	public void writeWithJsonSucceeds() throws Exception {
		mockMvc
				.perform(put("/write").content("data").contentType("application/json"))
				.andDo(print())
				.andExpect(status().isOk());

		verify(restTemplate).put("/api/v1/data",
				WriteRequest.builder()
						.serviceBrokerName(TEST_BROKER_NAME)
						.serviceOfferingName(TEST_SERVICE_NAME)
						.serviceBindingId(TEST_BINDING_ID)
						.credentialName(TEST_CREDENTIAL_NAME)
						.valueType(ValueType.JSON)
						.value(new HashMap<String, Object>() {{
							put("uri", "http://example.com");
							put("client_id", "id");
							put("client_secret", "secret");
						}})
						.build()
		);
	}
}