package ambit2.rest.task.dbpreprocessing;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.BitSetGenerator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * Dataset fingerprints
 * @author nina
 *
 */
public class CallableFingerprintsCalculator extends	CallableQueryProcessor<Object, IStructureRecord> {
	public CallableFingerprintsCalculator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm) {
		super(form, context);
	}
	protected long batchSize = 10000;
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		RetrieveStructure r = new RetrieveStructure(true);
		r.setMaxRecords(1);
		p.add(new ProcessorStructureRetrieval(r));
	
		p.add(new BitSetGenerator(FPTable.fp1024));
		p.add(new FP1024Writer());
		return p;
	}

	@Override
	protected Reference createReference(Connection connection) throws Exception {
		return null;
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		//can have combined query with a dataset query if dataset_uri is present
		MissingFingerprintsQuery q =  new MissingFingerprintsQuery();
		q.setMaxRecords(batchSize);
		return q;
	}
	protected AbstractBatchProcessor createBatch(Object target) throws Exception{

		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else throw new Exception("Can't process "+ target.toString());
	}
}
