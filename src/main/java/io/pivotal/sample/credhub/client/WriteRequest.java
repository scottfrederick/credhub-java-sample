package io.pivotal.sample.credhub.client;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WriteRequest extends BaseRequest {
	@Getter
	private boolean overwrite;

	private ValueType valueType;

	@Getter
	private Object value;

	@Getter
	@JsonInclude(NON_EMPTY)
	private List<AccessControlEntry> accessControlEntries;

	@Builder
	private WriteRequest(String serviceBrokerName, String serviceOfferingName, String serviceBindingId, String credentialName,
						 boolean overwrite, ValueType valueType, Object value,
						 @Singular List<AccessControlEntry> accessControlEntries) {
		super(serviceBrokerName, serviceOfferingName, serviceBindingId, credentialName);
		this.overwrite = overwrite;
		this.valueType = valueType;
		this.value = value;
		this.accessControlEntries = accessControlEntries;
	}

	public String getType() {
		return valueType.type();
	}

}
