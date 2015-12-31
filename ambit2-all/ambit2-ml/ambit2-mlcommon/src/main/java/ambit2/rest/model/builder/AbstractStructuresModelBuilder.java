package ambit2.rest.model.builder;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

public abstract class AbstractStructuresModelBuilder<DATA> extends	ModelBuilder<DATA, Algorithm, ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5510875688817299687L;

	public AbstractStructuresModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter) {
		this(applicationRootReference,model_reporter,alg_reporter,null,null);
	}
	
	public AbstractStructuresModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super(applicationRootReference,model_reporter,alg_reporter,targetURI,parameters);

	}

	public void transform(IStructureRecord[] records) {
		
	}
}
