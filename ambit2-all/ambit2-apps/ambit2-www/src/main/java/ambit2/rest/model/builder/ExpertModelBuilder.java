package ambit2.rest.model.builder;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

public class  ExpertModelBuilder extends SimpleModelBuilder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2375350593328937076L;
	protected boolean modelHidden = false;
	protected String userName = "guest";
	protected String datasetURI = null;
	
	public ExpertModelBuilder(
			String datasetURI,
			String userName,
			Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter)  {
		this(datasetURI,userName,applicationRootReference,model_reporter,alg_reporter,false);
	}	
	public ExpertModelBuilder(
			String datasetURI,
			String userName, 
			Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			boolean isModelHidden)  {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.modelHidden = isModelHidden;
		this.userName = userName;
		this.datasetURI=datasetURI;
	}	

	@Override
	protected ModelQueryResults createModel(Algorithm algorithm)
			throws AmbitException {
		ModelQueryResults model = super.createModel(algorithm);
		model.setTrainingInstances(datasetURI);
		return model;
	}
	@Override
	protected String getName(Algorithm algorithm) throws AmbitException {
		return String.format("%s_%s", userName,datasetURI);
	}
	@Override
	protected String getMediaType() throws AmbitException {
		return MediaType.APPLICATION_WWW_FORM.toString();
	}
	@Override
	protected String getContent(Algorithm algorithm) throws AmbitException {
		return super.getContent(algorithm);
	}

	@Override
	protected List<Property> createProperties(Algorithm algorithm)
			throws Exception {
		List<Property> p = new ArrayList<Property>();
		
		LiteratureEntry le = LiteratureEntry.getInstance(Reference.encode(getName(algorithm)),"value");
		Property property = new Property("value",le);

		property.setLabel(algorithm.getName());
		property.setClazz(String.class);
		p.add(property);
		return p;
	}
	
	protected PredictedVarsTemplate createPredictedTemplate(Algorithm algorithm) throws Exception {
		PredictedVarsTemplate predicted = new PredictedVarsTemplate(String.format("%s",getName(algorithm)));
		return predicted;
	}
}