package ambit2.db.reporters;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.search.structure.QueryPrescreenBitSet;

public abstract class SubstructureSearchReporter<Output> extends QueryReporter<IStructureRecord, QueryPrescreenBitSet, Output> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6404425391066863607L;

	public SubstructureSearchReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}
	protected void processItem(IStructureRecord item, Output output) {
		
	};
}
