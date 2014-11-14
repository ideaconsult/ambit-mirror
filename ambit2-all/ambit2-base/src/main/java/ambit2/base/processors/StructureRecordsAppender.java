package ambit2.base.processors;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.interfaces.IStructureRecord;


public class StructureRecordsAppender extends DefaultAmbitProcessor<IStructureRecord, List<IStructureRecord>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8988481466533697624L;
	protected List<IStructureRecord> records;

	public List<IStructureRecord> getRecords() {
		return records;
	}

	public void setRecords(List<IStructureRecord> records) {
		this.records = records;
	}

	public List<IStructureRecord> process(IStructureRecord target)
			throws AmbitException {
		if (records == null) records = new ArrayList<IStructureRecord>();
		try {
			records.add((IStructureRecord)target.clone());

		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return records;
	}
}
