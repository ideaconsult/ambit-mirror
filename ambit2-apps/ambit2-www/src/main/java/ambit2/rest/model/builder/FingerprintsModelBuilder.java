package ambit2.rest.model.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.rdf.ns.OT;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.model.structure.DataCoverageFingerprints;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

/**
 * {@link DataCoverageFingerprints}
 * @author nina
 *
 */
public class FingerprintsModelBuilder extends ModelBuilder<List<BitSet>,Algorithm,ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4281612770780436429L;
	protected String predictedFeatureURI;
	
	public FingerprintsModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters,
			String predictedFeatureURI //optional, can be null
			) {
		super(applicationRootReference,model_reporter,alg_reporter,targetURI,parameters);
		trainingData = new ArrayList<BitSet>();
		this.predictedFeatureURI = predictedFeatureURI;
	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {

		List<BitSet> instances = trainingData;
		if ((instances==null) || (instances.size()==0)) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset!");

		DataCoverageFingerprints coverage = null;
		try {
			Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
			coverage = (DataCoverageFingerprints) clazz.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
		String name = String.format("%s.%s",UUID.randomUUID().toString(),coverage.getName());
		ModelQueryResults m = new ModelQueryResults();
		m.setParameters(parameters);
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.COVERAGE_SERIALIZED.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		
		LiteratureEntry prediction = new LiteratureEntry(m.getName(),
				model_reporter.getURI(applicationRootReference.toString(),m));		
		prediction.setType(_type.Model);
		Template predictors = null;
		Template dependent = null;
		PredictedVarsTemplate predicted = null;
		
		if (coverage!= null) {
			coverage.build(instances);

			predicted = new PredictedVarsTemplate(name+"#ApplicabilityDomain");
			Property property = new Property(coverage.getMetricName(),prediction);
			property.setEnabled(true);
			property.setLabel(String.format("http://www.opentox.org/api/1.1#%s",coverage.getMetricName()));
			predicted.add(property);
			
			property = new Property(coverage.getDomainName(),prediction);
			property.setLabel(Property.opentox_ConfidenceFeature);
			property.setEnabled(true);

			//this is a confidence feature
			if (predictedFeatureURI!=null) {
				PropertyAnnotation<String> a = new PropertyAnnotation<String>();
				a.setType(OT.OTClass.ModelConfidenceFeature.name());
				a.setPredicate(OT.OTProperty.confidenceOf.name());
				a.setObject(predictedFeatureURI);
				PropertyAnnotations aa = new PropertyAnnotations();
				aa.add(a);
				property.setAnnotations(aa);
			}
			predicted.add(property);	

			dependent = new Template("Empty");
			
			predictors = new Template("Empty");
			
		} 

		m.setPredictors(predictors);
		m.setDependent(dependent);
		m.setPredicted(predicted);
		
		try {
			serializeModel(coverage, instances, m);
		} catch (IOException x) {
			throw new AmbitException(x);
		}
		m.setContentMediaType(AlgorithmFormat.COVERAGE_SERIALIZED.getMediaType());
		return m;
	}
	
	protected void serializeModel(Object predictor, List<BitSet> newInstances, ModelQueryResults m) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(predictor);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		
		Form form = new Form();
		
		form.add("model", Base64.encode(content));
		m.setContent(form.getWebRepresentation().getText());
	}

}
