package io.pivotal.sample.credhub.client;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
class AccessControlEntry {
	private String actor;

	@Singular
	private List<String> operations;
}
