package ambit2.db.processors;

import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
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
