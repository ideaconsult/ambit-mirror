package ambit2.base.config;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public class AMBITConfigProperties {
	protected Hashtable<String, Properties> properties = new Hashtable<String, Properties>();
	
	public synchronized String getProperty(String name, String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				Properties defaults = new Properties();
				try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(config)) {
					defaults.load(in);
				}
				p = new Properties(defaults);
				properties.put(config, p);
			}
			return p.getProperty(name);

		} catch (Exception x) {
			return null;
		}
	}
	public synchronized String getPropertyWithDefault(String name, String config, String defaultValue) {
		try {
			String value = getProperty(name, config);
			if (value == null)
				return defaultValue;
			else if (value != null && value.startsWith("${"))
				return defaultValue;
			else
				return value;
		} catch (Exception x) {
			return defaultValue;
		}
	}
	
}
