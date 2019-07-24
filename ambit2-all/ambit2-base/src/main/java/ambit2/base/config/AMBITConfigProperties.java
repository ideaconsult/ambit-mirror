package ambit2.base.config;

import java.io.File;

import net.idea.modbcum.i.config.ConfigProperties;

public class AMBITConfigProperties extends ConfigProperties {
	
	public static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	public static final String templateProperties = "ambit2/rest/config/ambit2.assay.properties";
	public static final String configProperties = "ambit2/rest/config/config.prop";
	public static final String loggingProperties = "ambit2/rest/config/logging.prop";
	

	public AMBITConfigProperties() {
		super(null);
	}


	public AMBITConfigProperties(File overridePath) {
		super(overridePath);
	}

}
