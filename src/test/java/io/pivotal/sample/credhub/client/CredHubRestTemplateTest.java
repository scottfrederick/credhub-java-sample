package io.pivotal.sample.credhub.client;

import io.pivotal.sample.credhub.client.CredHubRestTemplate.CredHubRestTemplateInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CredHubRestTemplateTest {
	@Mock
	private ClientHttpRequestExecution execution;

	@Mock
	private KeyStoreBuilder keyStoreBuilder;

	@Test
	public void restTemplateIsConfigured() throws Exception {
		CredHubRestTemplate restTemplate =
				new CredHubRestTemplate(new URI("https://credhub.cf.example.com:8844"), keyStoreBuilder);

		assertThat(restTemplate.getInterceptors().size(), equalTo(1));
		assertThat(restTemplate.getInterceptors().get(0), instanceOf(CredHubRestTemplateInterceptor.class));
	}

	@Test
	public void interceptorFormatsUrl() throws Exception {
		byte[] contentBytes = "data".getBytes();

		CredHubRestTemplateInterceptor interceptor =
				new CredHubRestTemplateInterceptor(new URI("https://credhub.cf.example.com:8844"));

		MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, new URI("/api/v1/data"));

		interceptor.intercept(request, contentBytes, execution);

//		verify(execution).execute(
//				argThat(actualRequest -> actualRequest.getURI().toString.equals("https://credhub.cf.example.com:8844/api/v1/data")),
//				equalTo(contentBytes)
// 		);
	}
}