package ambit2.rest.task.dbpreprocessing;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.DbReaderStructure;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.dataset.RDFStructuresReader;
import ambit2.rest.model.predictor.AbstractStructureProcessor;

public class CallableFixPreferredStructure<USERID> extends	CallableDBProcessing<USERID>  {

	public CallableFixPreferredStructure(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,USERID token) {
		super(form,applicationRootReference,context,algorithm,token);
	}
	/*
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure(true);
			reader.setHandlePrescreen(true);
			return reader;
		} throw new Exception("Not supported");
	}
	*/
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		RetrieveStructure q = new RetrieveStructure(true);
		q.setPageSize(1);
		q.setPage(0);
		p.add(new ProcessorStructureRetrieval(q) {
			public IStructureRecord process(IStructureRecord target) throws ambit2.base.exceptions.AmbitException {
				if (target==null) return target;
				int idstructure = target.getIdstructure();
				IStructureRecord record = super.process(target);
				record.setIdstructure(idstructure);
				return record;
			};
		});
		
		p.add(new AbstractStructureProcessor(
				applicationRootReference,
				null,
				null,
				null,
				null						
				) {
			@Override
			public boolean isStructureRequired() {
				return true;
			}
			@Override
					public Object createPredictor(ModelQueryResults model)
							throws ResourceException {
						return null;
					}
		});		
		return p;
	}
	
}
