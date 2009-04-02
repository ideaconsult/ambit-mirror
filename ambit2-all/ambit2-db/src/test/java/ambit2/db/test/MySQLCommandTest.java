/**
 * 
 */
package ambit2.db.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.base.config.Preferences;
import ambit2.db.processors.MySQLCommand;

/**
 * Test for {@link MySQLCommand}
 * @author nina
 *
 */
public class MySQLCommandTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link ambit2.db.processors.MySQLCommand#getInfo()}.
	 */
	@Test
	public void testGetInfo() {
		Preferences.setProperty(Preferences.USER, "root");
		Preferences.setProperty(Preferences.PORT, "33060");

		MySQLCommand command = new MySQLCommand();
		Assert.assertEquals("33060",command.getInfo().getPort());
		Assert.assertEquals("root",command.getInfo().getUser());
		Assert.assertEquals("/data/ambit.err",command.getErrFile());
	}

}
