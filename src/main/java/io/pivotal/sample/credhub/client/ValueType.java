package io.pivotal.sample.credhub.client;

public enum ValueType {
	PASSWORD("password"),
	JSON("json");

	private final String type;

	ValueType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

	public static ValueType getTypeByString(String type) {
		for (ValueType e : ValueType.values()) {
			if (e.type().equals(type)) {
				return e;
			}
		}
		return null;
	}
}
