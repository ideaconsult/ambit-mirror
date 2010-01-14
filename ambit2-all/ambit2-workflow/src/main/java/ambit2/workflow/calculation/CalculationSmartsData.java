package ambit2.workflow.calculation;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;

public class CalculationSmartsData extends CalculationSequence {
	public CalculationSmartsData() {
		this(10000);
	}
	public CalculationSmartsData(long maxsize) {
		super(maxsize);	

		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				MissingFingerprintsQuery q = new MissingFingerprintsQuery(FPTable.smarts_accelerator);
				q.setMaxRecords(batchSize);
				return q;
			}
		});
			addStep(query);		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new SMARTSPropertiesGenerator());
		p.add(new SMARTSAcceleratorWriter());
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> batchPrimitive = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false) {
			@Override
			public Continuation continuationWith(Continuation arg0) {
				return super.continuationWith(arg0);
			}
		};
		batchPrimitive.setName("SMARTS properties");
		
	    addStep(getBatchLoop(batchPrimitive));
     }		
}
