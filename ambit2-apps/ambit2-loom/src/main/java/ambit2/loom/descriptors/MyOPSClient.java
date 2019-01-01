package ambit2.loom.descriptors;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.idea.ops.cli.OPSClient;

public class MyOPSClient extends OPSClient {
	
	public MyOPSClient(File config)  throws Exception  {
		super(true);
		this.properties = config==null?null:loadProperties(config);
	}
	
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Properties loadProperties(File config)  {
		FileInputStream in = null;
		try {
			in = new FileInputStream(config);
			Properties properties = new Properties();
			properties.load(in);
			return properties;
		} catch (Exception x) {
			return null;
		} finally {
			try {in.close();} catch (Exception x) {}
		}
	}
}