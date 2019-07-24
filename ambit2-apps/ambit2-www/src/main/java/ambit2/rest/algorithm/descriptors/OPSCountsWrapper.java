package ambit2.rest.algorithm.descriptors;

import java.util.Properties;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.loom.descriptors.MyOPSClient;
import ambit2.loom.descriptors.OPSCountsByCompound;
import net.idea.ops.cli.OPSClient;

public class OPSCountsWrapper extends OPSCountsByCompound {

	@Override
	protected OPSClient createOPSClient() throws Exception {
		try {
			AMBITConfigProperties config = new AMBITConfigProperties();
			Properties properties = config.getProperties(AMBITConfigProperties.ambitProperties);
			MyOPSClient cli = new MyOPSClient(null);
			cli.setProperties(properties);
			return cli;
		} catch (Exception x) {
			throw x;
		}
	}
}
