package io.pivotal.sample.credhub;

import io.pivotal.sample.credhub.client.CredHubProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"CREDHUB_API=https://credhub.cf.example.com:8844",
		"CF_INSTANCE_CERT=/etc/cf-instance-credentials/instance.crt",
		"CF_INSTANCE_KEY=/etc/cf-instance-credentials/instance.key"
})
public class CredHubConfigurationTest {
	@Autowired
	private CredHubProperties credHubProperties;

	@MockBean
	@SuppressWarnings("unused") // prevent the real bean from being loaded into the app context
	private RestTemplate restTemplate;

	@Test
	public void propertiesPopulated() {
		assertThat(credHubProperties.getApiUrlBase(), equalTo("https://credhub.cf.example.com:8844"));
		assertThat(credHubProperties.getInstanceCertLocation(), equalTo("/etc/cf-instance-credentials/instance.crt"));
		assertThat(credHubProperties.getInstanceKeyLocation(), equalTo("/etc/cf-instance-credentials/instance.key"));
	}
}