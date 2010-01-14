package ambit2.workflow.calculation;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Primitive;

/**
 * Descriptor calculation
 * @author nina
 *
 */
public class CalculationDescriptors extends CalculationSequence {
	protected static final String DESCRIPTORS_NEWQUERY = "ambit2.plugin.dbtools.DBUtilityWorkflow.DESCRIPTORS_NEWQUERY";
	public CalculationDescriptors() {
		this(10000);
	}
	public CalculationDescriptors(long maxsize) {	
		super(maxsize);
	    
	    Primitive descriptorFactory = new Primitive(DBWorkflowContext.DESCRIPTORS,DBWorkflowContext.DESCRIPTORS,new Performer() {
        	@Override
        	public Object execute() throws Exception {
        		 Object target = getTarget();
        		 if ((target !=null) && (target instanceof Profile)) return target;
        		 
        		 DescriptorsFactory factory = new DescriptorsFactory();
        		    Profile<Property> descriptors;
        		    try {
        		    	descriptors = factory.process(null);
        		    } catch (Exception x) {
        		    	x.printStackTrace();
        		    	descriptors = new Profile<Property>();
        		    }
        		    return descriptors;
        	}
        	@Override
        	public String toString() {
        
        		return "Load descriptors";
        	}
        	
        });
	    addStep(descriptorFactory);
	    UserInteraction<Profile<Property>> defineDescriptors = new UserInteraction<Profile<Property>>(
        		null,
        		DBWorkflowContext.DESCRIPTORS,
        		"Select descriptor(s)");
	    
	    addStep(defineDescriptors);
        Primitive q = new Primitive(DBWorkflowContext.DESCRIPTORS,DESCRIPTORS_NEWQUERY,new Performer() {
        	@Override
        	public Object execute() throws Exception {
                //QueryDataset q = new QueryDataset("Default");
        		ProcessorMissingDescriptorsQuery p = new ProcessorMissingDescriptorsQuery(batchSize);
        		
        		//TODO set scope - dataset
        		//scope - entire db, query, dataset!! to be used elsewhere
        		
        		return p.process((Profile)getTarget());
        	}
        	@Override
        	public String toString() {
        
        		return "Select descriptors";
        	}
        	
        });
        q.setName("Select descriptor(s)");
        addStep(q);	    
        addStep(addCalculationD());  
	}
	
	//TODO extract in a class and reuse in other workflows
	protected Activity addCalculationD() {

		final DescriptorsCalculator calculator = new DescriptorsCalculator();
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p1.add(new ProcessorStructureRetrieval());		
		p1.add(calculator);
		
		DbReader<IStructureRecord> batch1 = new DbReader<IStructureRecord>();
		batch1.setProcessorChain(p1);
		
		DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics> performer = 
			new DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics>(batch1,false) {
			public IBatchStatistics execute() throws Exception {
				Object o = getContext().get(DBWorkflowContext.DESCRIPTORS);
				calculator.setDescriptors((Profile)o);
				return super.execute();
			}
		};	
		
		Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap1 = 
			new Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DESCRIPTORS_NEWQUERY,
				DBWorkflowContext.BATCHSTATS,
				performer);
	    ap1.setName("Descriptor calculations");		
	    return getBatchLoop(ap1);
	}
	@Override
	public String toString() {
	return "Database utility";
	}
}
