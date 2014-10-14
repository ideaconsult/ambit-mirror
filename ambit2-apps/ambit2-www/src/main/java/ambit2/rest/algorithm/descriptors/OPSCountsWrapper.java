package ambit2.rest.algorithm.descriptors;

import java.io.InputStream;
import java.util.Properties;

import net.idea.ops.cli.OPSClient;
import ambit2.loom.descriptors.MyOPSClient;
import ambit2.loom.descriptors.OPSCountsByCompound;

public class OPSCountsWrapper extends OPSCountsByCompound {
	
	@Override
	protected OPSClient createOPSClient() throws Exception {
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			Properties properties = new Properties();
			properties.load(in);
			MyOPSClient cli = new MyOPSClient(null);
			cli.setProperties(properties);
			return cli;
		} catch (Exception x) {
			throw x;
		} finally {
			try {in.close();} catch (Exception x) {}
		}
	}
}
