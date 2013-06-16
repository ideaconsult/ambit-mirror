package net.idea.ambit2.rest.nano;

import java.io.InputStream;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;

public class NanoCMLRawReader extends NanoCMLIteratingReader  implements IRawReader<IStructureRecord> {

	public NanoCMLRawReader(InputStream in) throws Exception {
		super(in);
	}

	@Override
	public Object next() {
		return record;
	}
}
