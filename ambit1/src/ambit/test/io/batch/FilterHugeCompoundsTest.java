package ambit.test.io.batch;

import junit.framework.TestCase;
import ambit.io.FileInputState;
import ambit.io.FileOutputState;
import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchConfig;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.IJobStatus;
import ambit.processors.SkipHugeProcessor;

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
