package io.pivotal.sample.credhub.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@JsonInclude(NON_EMPTY)
public class BaseResponse {
	private List<CredHubData> data;
	private String error;

	@Data
	@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class CredHubData {
		private String versionCreatedAt;
		private String id;
		private ValueType valueType;
		private Object value;

		private String serviceBrokerName;
		private String serviceOfferingName;
		private String serviceBindingId;
		private String credentialName;

		public void setType(String type) {
			this.valueType = ValueType.getTypeByString(type);
		}

		public void setName(String name) {
			String[] parts = name.split("/");
			serviceBrokerName = parts[2];
			serviceOfferingName = parts[3];
			serviceBindingId = parts[4];
			credentialName = parts[5];
		}
	}
}
