package org.ideaconsult.iuclid5client.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.session.SessionEngine;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * The <code>SessionEngineTest</code> is used to demonstrate and test the
 * SessionEngine Web Service
 */
public class SessionEngineTest extends TestCase {
	protected final static Logger LOGGER = Logger.getLogger(SessionEngineTest.class);

	/**
	 * Constructor.
	 */
	public SessionEngineTest() {

		super(SessionEngineTest.class.getSimpleName());
	}

	/**
	 * Test case to login logout
	 * 
	 * @throws Exception
	 *             for all occurred problems
	 */
	public void testLoginLogout() throws Exception {
		LOGGER.info("execute login for " + PropertiesUtil.getUsername());

		final Session session = SessionEngine.login();
		LOGGER.info("received session with id " + session.getId());

		LOGGER.info("execute logout for " + session.getId());
		SessionEngine.logout(session);
		try {
			LOGGER.info("validate whether the logout was successful " + session.getId());
			SessionEngine.logout(session);
		} catch (final SessionEngineNotAvailableFault e) {
			LOGGER.info("Expected a " + SessionEngineNotAvailableFault.class.getSimpleName()
					+ " ######## successfully logged out");
		}
	}

}
