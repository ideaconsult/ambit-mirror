package ambit2.db.model;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelWrapper;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryStoredResults;

public class ModelQueryResults extends ModelWrapper<
			IQueryRetrieval<IStructureRecord>,
			QueryStoredResults,
			IQueryRetrieval<IStructureRecord>,String> {
	@Override
	public String toString() {
		return String.format("Model %d",getId());
	}



}
