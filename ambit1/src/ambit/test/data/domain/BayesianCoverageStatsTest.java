/*
 * Created on 2005-7-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ambit.test.data.domain;

import junit.framework.TestCase;
import ambit.domain.ADomainMethodType;
import ambit.domain.BayesianCoverageStats;
import ambit.domain.DataCoverageDescriptors;

/**
 * @author Vedina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BayesianCoverageStatsTest extends TestCase {

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Class under test for void BayesianCoverageStats()
	 */
	public void testBayesianCoverageStats() {
	}

	/*
	 * Class under test for void BayesianCoverageStats(String)
	 */
	public void testBayesianCoverageStatsString() {
	}

	/*
	 * Class under test for void BayesianCoverageStats(String, int)
	 */
	public void testBayesianCoverageStatsStringint() {
	}

	public void testSetMethod() {
		BayesianCoverageStats bc = new BayesianCoverageStats();
		bc.setMethod(new DataCoverageDescriptors());
		assertEquals(ADomainMethodType._modeRANGE,
				bc.getMethod().getAppDomainMethodType().getId());
		
	}

	public void testIsAssessed() {
	}

	public void testEstimate() {
	}

	public void testAssess() {
	}

}
