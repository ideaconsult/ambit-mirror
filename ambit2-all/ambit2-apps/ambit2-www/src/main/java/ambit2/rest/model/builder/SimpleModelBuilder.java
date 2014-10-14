package ambit2.rest.model.builder;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
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
	protected List<Property> createProperties(Algorithm algorithm) throws Exception {
		return DescriptorsFactory.createDescriptor2Properties(algorithm.getContent().toString());
	}
	
	protected String getMediaType() throws AmbitException {
		return AlgorithmFormat.JAVA_CLASS.getMediaType();
	}
	protected String getContent(Algorithm algorithm) throws AmbitException {
		return algorithm.getContent().toString();
	}
	protected String getName(Algorithm algorithm) throws AmbitException {
		return algorithm.getName();
	}
	protected ModelQueryResults createModel(Algorithm algorithm) throws AmbitException {
		ModelQueryResults mr = new ModelQueryResults();
		mr.setHidden(modelHidden);
		mr.setContentMediaType(getMediaType());
		mr.setName(getName(algorithm));
		mr.setContent(getContent(algorithm));
		mr.setAlgorithm(alg_reporter.getURI(algorithm));
		mr.setPredictors(algorithm.getInput());
		return mr;
	}
	protected PredictedVarsTemplate createPredictedTemplate(Algorithm algorithm) throws Exception {
		PredictedVarsTemplate predicted = new PredictedVarsTemplate(String.format("%s",algorithm.getName()));
		return predicted;
	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
		try {
			List<Property> p = createProperties(algorithm);
			if ((p == null)||(p.size()==0)) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"Can't create a model from "+algorithm);

			ModelQueryResults mr = createModel(algorithm);

			Template dependent = new Template("Empty");
			mr.setDependent(dependent);
			
			PredictedVarsTemplate predicted = createPredictedTemplate(algorithm);
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
