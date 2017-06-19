package io.spring;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pcc.cache")
public class DemoProperties {
	
	/**
	 * The region name.
	 */
	private String regionName;

	@NotBlank(message = "Region name is required")
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}

