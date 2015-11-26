package ambit2.db.processors.test;

import ambit2.descriptors.processors.FPTable;

public class PC1024WriterTest extends FP1024WriterTest {
	@Override
	protected FPTable getMode() {
		return FPTable.cf1024;
	}

}