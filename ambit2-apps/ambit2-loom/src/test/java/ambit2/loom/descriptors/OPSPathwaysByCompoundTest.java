package ambit2.loom.descriptors;

import java.util.Properties;

import junit.framework.Assert;
import net.idea.ops.cli.OPSClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

public class OPSPathwaysByCompoundTest {
	public static OPSClient opscli;
	public final static String TEST_SERVER = config();
	//should be configured in the .m2/settings.xml 
	protected static Properties properties;

	@BeforeClass
	public static void setup() throws Exception {
		 opscli = new OPSClient(true);
	}
	

	@AfterClass
	public static void teardown() throws Exception {
		opscli.close();
	}
	
	public static String config()  {
		//String local = "https://beta.openphacts.org";
		String local = "https://beta.openphacts.org/1.3";
		try {
			properties = OPSClient.config();
			String testServer = properties.getProperty("ops.server_root");
			return testServer!=null?testServer.startsWith("http")?testServer:local:local;
		} catch (Exception x) {
			return local;
		}
	}
	
	@Test
	public void testProcess() throws Exception {
		config(); 
		OPSCountsByCompound group = new OPSCountsByCompound();

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer m = p.parseSmiles("CC(=O)NC1=CC=CC=C1"); //Acetanilide
		DescriptorValue result = group.calculate(m);
		Assert.assertEquals(1,((IntegerResult)(result.getValue())).intValue());

	}

}
