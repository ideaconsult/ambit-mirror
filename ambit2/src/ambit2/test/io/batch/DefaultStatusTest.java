package ambit2.test.io.batch;

import junit.framework.TestCase;
import ambit2.io.batch.DefaultStatus;
import ambit2.io.batch.IJobStatus;

public class DefaultStatusTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DefaultStatusTest.class);
	}

	public DefaultStatusTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'ambit2.io.batch.DefaultStatus.DefaultStatus()'
	 */
	public void testDefaultStatus() {
		DefaultStatus status = new DefaultStatus();
		TestObserver observer = new TestObserver();
		status.addObserver(observer);
		assertTrue(status.isStatus(IJobStatus.STATUS_NOTSTARTED));
		assertTrue(status.isModified());
		status.setStatus(IJobStatus.STATUS_RUNNING); //this should fire update event 
		assertEquals(1,observer.getCount());
		assertTrue(status.isRunning());
		status.setModified(false);//this should fire update event 
		assertFalse(status.isModified());
		assertEquals(2,observer.getCount());
		status.setStatus(IJobStatus.STATUS_DONE); //this should fire update event
		assertEquals(3,observer.getCount());
		assertTrue(status.isStatus(IJobStatus.STATUS_DONE));
		status = null;
	}

}
