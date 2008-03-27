package ambit2.test.io.batch;

import junit.framework.TestCase;
import ambit2.io.FileInputState;
import ambit2.io.FileOutputState;
import ambit2.io.batch.BatchProcessingException;
import ambit2.io.batch.DefaultBatchConfig;
import ambit2.io.batch.DefaultBatchProcessing;
import ambit2.io.batch.IJobStatus;
import ambit2.processors.SkipHugeProcessor;

public class FilterHugeCompoundsTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FilterHugeCompoundsTest.class);
	}
	public void xtest() {
		try {
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					new FileInputState("E:/nina/LRI_Ambit/Chemical Databases/NCI/nciopen_3D_fixed.sdf"),
					new FileOutputState("data/misc/nciopen_3D_fixed_filtered.sdf"),
					new SkipHugeProcessor(),
					new DefaultBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());

			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			/*
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
					*/
		} catch (BatchProcessingException x) {
			x.printStackTrace();
			fail();
		}

	}
}
