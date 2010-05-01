package ambit2.rest.model.builder;

import java.util.List;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

/**
 * Builds a model from ModelQueryResults. The model is based on IMolecularDescriptor class,  without input variables. 
 * @author nina
 *
 * @param <A>
 * @param <Model>
 */
public class SimpleModelBuilder extends ModelBuilder<Object,Algorithm, ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2827461619547962205L;
	protected boolean modelHidden = false;
	public SimpleModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter)  {
		this(applicationRootReference,model_reporter,alg_reporter,false);
	}	
	public SimpleModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			boolean isModelHidden)  {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.modelHidden = isModelHidden;
	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
		try {
			List<Property> p = DescriptorsFactory.createDescriptor2Properties(algorithm.getContent().toString());
			if ((p == null)||(p.size()==0)) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"Can't create a model from "+algorithm);

			ModelQueryResults mr = new ModelQueryResults();
			mr.setHidden(modelHidden);
			mr.setContentMediaType(AlgorithmFormat.JAVA_CLASS.getMediaType());
			mr.setName(algorithm.getName());
			mr.setContent(algorithm.getContent().toString());
			mr.setAlgorithm(alg_reporter.getURI(algorithm));
			mr.setPredictors(algorithm.getInput());

			Template dependent = new Template("Empty");
			mr.setDependent(dependent);
			
			Template predicted = new Template();
			predicted.setName(String.format("%s",algorithm.getName()));		
			mr.setPredicted(predicted);
			
			for (Property property:p) {
				property.setEnabled(true);
				if (property.getName()==null) continue;
				predicted.add(property);
				if (algorithm.getEndpoint()!=null) property.setLabel(algorithm.getEndpoint());
				property.getReference().setType(modelHidden?_type.Algorithm:_type.Model);
			}

			return mr;			
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getCause()==null?x.getMessage():x.getCause().getMessage(),x);
		}
	}
}
