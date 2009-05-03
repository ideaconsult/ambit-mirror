package ambit2.db.processors;

import java.util.Iterator;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

public class BatchStructuresListProcessor extends AbstractBatchProcessor<List<IStructureRecord>,IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 772067703553971396L;

	public Iterator<IStructureRecord> getIterator(List<IStructureRecord> target)
			throws AmbitException {
		return target.iterator();
	}


}
