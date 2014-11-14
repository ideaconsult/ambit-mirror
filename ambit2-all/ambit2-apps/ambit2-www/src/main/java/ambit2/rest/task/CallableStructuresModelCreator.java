package ambit2.rest.task;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.ModelBuilder;

public abstract class CallableStructuresModelCreator<DATA,Builder extends ModelBuilder<DATA,Algorithm,ModelQueryResults>,USERID> extends	CallableModelCreator<DATA, IStructureRecord, Builder,USERID> {
	protected Reference applicationRootReference;
	
	public CallableStructuresModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			Builder builder,
			USERID token) {

		super(form, context,algorithm,builder,token);
		this.applicationRootReference =applicationRootReference;
	
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return getQueryObject(reference, applicationRootReference,context);
	}

	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {

		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		p1.add(new ProcessorStructureRetrieval());
		//p1.add(new MoleculeReader());
		p1.setAbortOnError(true);
		
		return p1;
	}
}
