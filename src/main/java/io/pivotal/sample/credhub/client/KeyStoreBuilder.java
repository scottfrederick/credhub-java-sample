package io.pivotal.sample.credhub.client;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoreBuilder {
	private static final char[] KEY_PASSWORD = "keystore".toCharArray();
	private static final String CERTIFICATE_NAME = "credhub-cert";
	private static final String KEY_NAME = "credhub-key";

	private String certificateLocation;
	private String keyLocation;

	public KeyStoreBuilder(String certificateLocation, String keyLocation) {
		this.certificateLocation = certificateLocation;
		this.keyLocation = keyLocation;
	}

	KeyStore buildKeyStore() throws Exception {
		Certificate cert = parseCertificate(certificateLocation);
		PrivateKey key = parsePrivateKey(keyLocation);
		return createKeyStore(cert, key);
	}

	char[] getKeyPassword() {
		return KEY_PASSWORD;
	}

	private Certificate parseCertificate(String certificateLocation) {
		try {
			PEMParser parser = new PEMParser(new FileReader(certificateLocation));
			X509CertificateHolder certHolder = (X509CertificateHolder) parser.readObject();
			JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
			return converter.getCertificate(certHolder);
		} catch (IOException | CertificateException e) {
			throw new IllegalArgumentException("Error parsing and loading certificate from location " + certificateLocation, e);
		}
	}

	private PrivateKey parsePrivateKey(String keyLocation) {
		try {
			PEMParser reader = new PEMParser(new FileReader(keyLocation));
			PEMKeyPair key = (PEMKeyPair) reader.readObject();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			return converter.getKeyPair(key).getPrivate();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error parsing and loading certificate from location " + keyLocation, e);
		}
	}

	private KeyStore createKeyStore(Certificate cert, PrivateKey key) {
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null);
			keystore.setCertificateEntry(CERTIFICATE_NAME, cert);
			keystore.setKeyEntry(KEY_NAME, key, KEY_PASSWORD, new Certificate[]{cert});
			return keystore;
		} catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Error creating keystore ", e);
		}
	}
}
