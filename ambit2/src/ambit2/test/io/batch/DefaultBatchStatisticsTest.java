package ambit2.test.io.batch;

import junit.framework.TestCase;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;

public class DefaultBatchStatisticsTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DefaultBatchStatisticsTest.class);
	}

	public DefaultBatchStatisticsTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'ambit2.io.batch.DefaultBatchStatistics.DefaultBatchStatistics()'
	 */
	public void testDefaultBatchStatistics() {
		DefaultBatchStatistics stats = new DefaultBatchStatistics();
		long count = 0;
		assertEquals(count,stats.getRecords(IBatchStatistics.RECORDS_ERROR));
		assertEquals(count,stats.getRecords(IBatchStatistics.RECORDS_READ));
		assertEquals(count,stats.getRecords(IBatchStatistics.RECORDS_WRITTEN));
		assertEquals(count,stats.getRecords(IBatchStatistics.RECORDS_PROCESSED));
		TestObserver observer = new TestObserver();
		stats.addObserver(observer);
		stats.increment(IBatchStatistics.RECORDS_READ);
		stats.increment(IBatchStatistics.RECORDS_PROCESSED);
		stats.increment(IBatchStatistics.RECORDS_PROCESSED);
		stats.increment(IBatchStatistics.RECORDS_WRITTEN);
		stats.increment(IBatchStatistics.RECORDS_ERROR);
		
		assertEquals(1,stats.getRecords(IBatchStatistics.RECORDS_READ));
		assertEquals(2,stats.getRecords(IBatchStatistics.RECORDS_PROCESSED));
		assertEquals(1,stats.getRecords(IBatchStatistics.RECORDS_WRITTEN));
		assertEquals(1,stats.getRecords(IBatchStatistics.RECORDS_ERROR));
		
		assertEquals(5,observer.getCount());
		stats.setRecords(IBatchStatistics.RECORDS_ERROR,0);
		assertEquals(0,stats.getRecords(IBatchStatistics.RECORDS_ERROR));
		assertEquals(6,observer.getCount());
	}

	/*
	 * Test method for 'ambit2.io.batch.DefaultBatchStatistics.getRecords(int)'
	 */
	public void testGetRecords() {

	}

	/*
	 * Test method for 'ambit2.io.batch.DefaultBatchStatistics.setRecords(int, long)'
	 */
	public void testSetRecords() {

	}

	/*
	 * Test method for 'ambit2.io.batch.DefaultBatchStatistics.increment(int)'
	 */
	public void testIncrement() {

	}

}
