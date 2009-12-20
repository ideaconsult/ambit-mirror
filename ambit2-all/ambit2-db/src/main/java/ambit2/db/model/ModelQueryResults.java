package ambit2.db.model;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelWrapper;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryStoredResults;

/**
 * Training and test instances are available via {@link IQueryRetrieval}
 * @author nina
 *
 */
public class ModelQueryResults extends ModelWrapper<
			IQueryRetrieval<IStructureRecord>,
			QueryStoredResults,
			IQueryRetrieval<IStructureRecord>,String> {
	@Override
	public String toString() {
		return (getName()==null)||(getName().equals(""))?
				String.format("Model %d",getId()):
				String.format("Model %s",getName());
	}



}
