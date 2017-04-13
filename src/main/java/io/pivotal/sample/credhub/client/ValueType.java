package io.pivotal.sample.credhub.client;

public enum ValueType {
	PASSWORD,
	JSON;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
