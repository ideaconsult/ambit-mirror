package ambit2.db.processors.test;

import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class SK1024WriterTest  extends FP1024WriterTest {
	@Override
	protected FPTable getMode() {
		return FPTable.sk1024;
	}

}