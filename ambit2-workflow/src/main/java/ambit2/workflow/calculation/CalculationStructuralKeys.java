package ambit2.workflow.calculation;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.descriptors.processors.BitSetGenerator;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;

/**
 * Structural keys (sk1024)
 * @author nina
 *
 */
public class CalculationStructuralKeys extends CalculationSequence {
	public CalculationStructuralKeys() {
		this(10000);
	}
	public CalculationStructuralKeys(long maxsize) {
		super(maxsize);	
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				MissingFingerprintsQuery q = new MissingFingerprintsQuery(FPTable.sk1024);
				q.setPageSize(batchSize);
				q.setPage(0);
				return q;
			}
		});
		addStep(query);		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new BitSetGenerator(FPTable.sk1024));
		p.add(new FP1024Writer(FPTable.sk1024));
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("Structural keys calculations");
	    addStep(getBatchLoop(ap));
	}
}
