package ambit2.base.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public class AMBITConfigProperties {
	
	protected static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	protected static final String templateProperties = "ambit2/rest/config/ambit2.assay.properties";
	public static final String configProperties = "ambit2/rest/config/config.prop";
	public static final String loggingProperties = "ambit2/rest/config/logging.prop";
	
	protected String AMBIT_CONFIG_OVERRIDE_VAR = "AMBIT_CONFIG_OVERRIDE_VAR";
	public String getConfigOverrideVar() {
		return AMBIT_CONFIG_OVERRIDE_VAR;
	}

	public void setConfigOverrideVar(String configOverrideVar) {
		this.AMBIT_CONFIG_OVERRIDE_VAR = configOverrideVar;
	}

	protected Hashtable<String, Properties> properties = new Hashtable<String, Properties>();
	protected File overridePath = null;
	protected String context = null;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public File getConfigOverrideDir(String override_var) {
		try {
			String path = System.getenv(override_var);
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
		this(context, null);
	}

	public AMBITConfigProperties(String context, File overridePath) {
		this.overridePath = overridePath;
		setContext(context);
	}

	protected synchronized Properties getProperties(String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				// Load the internal config as defaults
				Properties defaults = new Properties();
				try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(config)) {
					defaults.load(in);
				}
				if (getConfigOverrideVar() !=null && defaults.get(getConfigOverrideVar()) != null) {
					File dir = getConfigOverrideDir(defaults.get(getConfigOverrideVar()).toString());
					if (dir != null && !"".equals(dir.getAbsolutePath()))
						overridePath = dir;
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
							//no overriding of the var
							p.remove(AMBIT_CONFIG_OVERRIDE_VAR);
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

	protected synchronized String getProperty(String name, String config) {
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

	public synchronized Long getPropertyWithDefaultLong(String name, String config, Long defaultValue) {
		try {
			String value = getProperty(name, config);
			if (value == null)
				return defaultValue;
			else if (value != null && value.startsWith("${"))
				return defaultValue;
			else
				return Long.parseLong(value);
		} catch (Exception x) {
			return defaultValue;
		}
	}

	public synchronized boolean getBooleanPropertyWithDefault(String name, String config, boolean defaultValue) {
		try {
			String attach = getProperty(name, config);
			if (attach != null && attach.startsWith("${"))
				return defaultValue;
			return attach == null ? defaultValue : Boolean.parseBoolean(attach);
		} catch (Exception x) {
			return defaultValue;
		}
	}
}
