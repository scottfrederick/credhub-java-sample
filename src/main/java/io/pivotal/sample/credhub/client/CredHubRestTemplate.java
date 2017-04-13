package io.pivotal.sample.credhub.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;

public class CredHubRestTemplate extends RestTemplate {

	public CredHubRestTemplate(URI baseUri, KeyStoreBuilder keyStoreBuilder) {
		addInterceptor(baseUri);
		setRequestFactory(keyStoreBuilder);
	}

	private void addInterceptor(URI baseUri) {
		List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
		interceptors.add(new CredHubRestTemplateInterceptor(baseUri));
		setInterceptors(interceptors);
	}

	private void setRequestFactory(KeyStoreBuilder keyStoreBuilder) {
		HttpClient httpClient = buildHttpClient(keyStoreBuilder);
		setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
	}

	private HttpClient buildHttpClient(KeyStoreBuilder keyStoreBuilder) {
		try {
			KeyStore keyStore = keyStoreBuilder.buildKeyStore();

			SSLContext sslcontext = SSLContexts.custom()
					.loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
					.loadKeyMaterial(keyStore, keyStoreBuilder.getKeyPassword())
					.build();

			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
					sslcontext,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());

			return HttpClients.custom()
					.setSSLSocketFactory(socketFactory)
					.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class CredHubRestTemplateInterceptor implements ClientHttpRequestInterceptor {
		private final URI baseUri;

		CredHubRestTemplateInterceptor(URI baseUri) {
			this.baseUri = baseUri;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			ReplaceUriClientHttpRequest replacedRequest = new ReplaceUriClientHttpRequest(request, baseUri);
			return execution.execute(replacedRequest, body);
		}
	}

	public static class ReplaceUriClientHttpRequest extends HttpRequestWrapper {
		private final URI baseUri;

		ReplaceUriClientHttpRequest(HttpRequest request, URI baseUri) {
			super(request);
			this.baseUri = baseUri;
		}

		@Override
		public URI getURI() {
			return UriComponentsBuilder.fromUri(baseUri)
					.path(getRequest().getURI().getRawPath())
					.query(getRequest().getURI().getRawQuery())
					.build()
					.toUri();
		}
	}
}
