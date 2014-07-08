package ambit2.rest.task.tautomers;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.SimpleModelBuilder;

public class TautomersModelBuilder  extends SimpleModelBuilder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1576610802954465728L;
	public TautomersModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter)  {
		this(applicationRootReference,model_reporter,alg_reporter,false);
	}	
	public TautomersModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			boolean isModelHidden)  {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.modelHidden = isModelHidden;
	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
		try {
			ModelQueryResults mr = new ModelQueryResults();
			mr.setHidden(modelHidden);
			mr.setContentMediaType(AlgorithmFormat.TAUTOMERS.getMediaType());
			mr.setName(algorithm.getName());
			mr.setContent(algorithm.getContent().toString());
			mr.setAlgorithm(alg_reporter.getURI(algorithm));
			mr.setPredictors(algorithm.getInput());

			Template dependent = new Template("Empty");
			mr.setDependent(dependent);
			
			PredictedVarsTemplate predicted = new PredictedVarsTemplate("Empty");
			mr.setPredicted(predicted);
			
			return mr;			

		} catch (Exception x) {
			
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getCause()==null?x.getMessage():x.getCause().getMessage(),x);
		}
	}
}
