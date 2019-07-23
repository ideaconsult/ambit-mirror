package ambit2.base.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public class AMBITConfigProperties {
	protected static String AMBIT_CONFIG_OVERRIDE_DIR = "AMBIT_CONFIG_OVERRIDE_DIR";
	protected Hashtable<String, Properties> properties = new Hashtable<String, Properties>();
	protected File overridePath = null;
	protected String context = null;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public static File getConfigOverrideDir() {
		try {
			String path = System.getenv(AMBIT_CONFIG_OVERRIDE_DIR);
			if (path != null) {
				File dir = new File(path);
				if (dir.exists() && dir.isDirectory())
					return dir;
			}
			return null;
		} catch (Exception x) {
			return null;
		}

	}

	public AMBITConfigProperties() {
		this(null, null);
	}

	public AMBITConfigProperties(String context) {
		this(context, getConfigOverrideDir());
	}

	public AMBITConfigProperties(String context, File overridePath) {
		this.overridePath = overridePath;
		setContext(context);
	}

	public synchronized Properties getProperties(String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				// Load the internal config as defaults
				Properties defaults = new Properties();
				try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(config)) {
					defaults.load(in);
				}
				if (overridePath != null) {
					p = new Properties(defaults);
					String[] segments = config.split("/");
					File contextdir = overridePath;
					if (context != null) {
						contextdir = new File(overridePath, context.replace("/", ""));
					}
					File file = new File(contextdir, segments[segments.length - 1]);
					if (file.exists())
						try (InputStream in = new FileInputStream(file)) {
							p.load(in);
						}
				} else
					p = defaults;
				properties.put(config, p);
			}
			return p;

		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String getProperty(String name, String config) {
		try {
			Properties p = getProperties(config);
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
