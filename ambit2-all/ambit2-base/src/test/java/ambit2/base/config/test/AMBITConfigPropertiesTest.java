package ambit2.base.config.test;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import ambit2.base.config.AMBITConfigProperties;
import junit.framework.Assert;

public class AMBITConfigPropertiesTest {
	protected static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	protected static final String overrideProperties = "ambit2/rest/config/override";
	protected static final String key4test="custom.title";
	@Test
	public void test() throws Exception {
		AMBITConfigProperties p = new AMBITConfigProperties(null,null);
		Assert.assertEquals("TEST", p.getProperty(key4test,ambitProperties));
	}
	
	@Test
	public void testContextOverride() throws Exception {
		String overridePath = this.getClass().getClassLoader().getResource(overrideProperties).getFile();
		Assert.assertNotNull(overridePath);
		AMBITConfigProperties p = new AMBITConfigProperties("context",new File(overridePath));
		Assert.assertEquals("TEST_CONTEXT", p.getProperty(key4test,ambitProperties));
	}
	
	@Test
	public void testOverride() throws Exception {
		String overridePath = this.getClass().getClassLoader().getResource(overrideProperties).getFile();
		Assert.assertNotNull(overridePath);
		AMBITConfigProperties p = new AMBITConfigProperties(null,new File(overridePath));
		Assert.assertEquals("TEST123", p.getProperty(key4test,ambitProperties));
	}	
	@Test
	public void testPropertiesDefault() {
		Properties  defaults = new Properties();
		defaults.put("test", "defaults");
		Assert.assertEquals("defaults",defaults.get("test"));
		Properties p = new Properties(defaults);
		Assert.assertEquals("defaults",p.getProperty("test"));
		Assert.assertNull(p.get("test"));
	}

	
}
