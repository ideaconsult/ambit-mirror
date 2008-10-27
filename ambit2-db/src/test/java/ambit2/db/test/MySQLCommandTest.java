/**
 * 
 */
package ambit2.db.test;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
		MySQLCommand command = new MySQLCommand();
		Assert.assertEquals("33060",command.getInfo().getPort());
		Assert.assertEquals("root",command.getInfo().getUser());
		Assert.assertEquals("/data/ambit.err",command.getErrFile());
	}

}
