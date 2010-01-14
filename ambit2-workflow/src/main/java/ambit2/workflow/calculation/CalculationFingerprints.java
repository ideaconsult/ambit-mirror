package ambit2.workflow.calculation;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.BitSetGenerator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Fingerprints (fp1024)
 * @author nina
 *
 */
public class CalculationFingerprints extends CalculationSequence {
	
	public CalculationFingerprints() {
		this(10000);
	}
	public CalculationFingerprints(long maxsize) {
		super(maxsize);
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				MissingFingerprintsQuery q = new MissingFingerprintsQuery();
				q.setMaxRecords(batchSize);
				return q;
			}
		});
		addStep(query);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new BitSetGenerator(FPTable.fp1024));
		p.add(new FP1024Writer());
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> batchPrimitive = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
		batchPrimitive.setName("Fingerprint calculations");	
		addStep(getBatchLoop(batchPrimitive));
	}
}
