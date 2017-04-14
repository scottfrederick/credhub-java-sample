package io.pivotal.sample.credhub.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class AccessControlEntry {
	private static final String APP_ACTOR_PREFIX = "mtls-app:";
	
	@Setter(AccessLevel.PRIVATE)
	private String actor;

	@Singular
	private List<Operation> operations;

	public List<String> getOperations() {
		return operations.stream().map(Operation::operation).collect(Collectors.toList());
	}

	public static class AccessControlEntryBuilder {
		public AccessControlEntryBuilder app(String appId) {
			this.actor = APP_ACTOR_PREFIX + appId;
			return this;
		}
	}

	public enum Operation {
		READ("read"),
		WRITE("write");

		private final String operation;

		Operation(String operation) {
			this.operation = operation;
		}

		public String operation() {
			return operation;
		}
	}
}
