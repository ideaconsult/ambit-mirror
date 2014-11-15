package ambit2.rest.model.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.rdf.ns.OT;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;
import Jama.Matrix;
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
import ambit2.model.numeric.DataCoverage;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

/**
 * applicability domain
 * @author nina
 *
 * @param <A>
 * @param <Model>
 */
public class CoverageModelBuilder extends ModelBuilder<Instances,Algorithm,ModelQueryResults> {
	protected String predictedFeatureURI;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4281612770780436429L;
	public CoverageModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters,
			String predictedFeatureURI //optional, can be null
			) {
		super(applicationRootReference,model_reporter,alg_reporter,targetURI,parameters);
		this.predictedFeatureURI = predictedFeatureURI;
	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {

		Instances instances = trainingData;
		if ((instances==null) || (instances.numInstances()==0) || (instances.numAttributes()==0)) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset!");
		try {
			RemoveWithValues removeMissingValues = new RemoveWithValues();
			String[] options = new String[1];
			options[0] = "-M";
			removeMissingValues.setOptions(options);
			removeMissingValues.setInputFormat(instances);
			Instances newInstances = Filter.useFilter(instances, removeMissingValues);
			instances = newInstances;
		} catch (Exception x) {
			//use unfiltered
		}
		//int numAttr = 0;
		//for (int j=0; j < instances.numAttributes();j++)
			//if (instances.attribute(j).isNumeric()) numAttr++;
		
		//if (numAttr==0) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No numeric attributes!");
		
		Matrix matrix = new Matrix(instances.numInstances(),instances.numAttributes()-1);
		for (int i=0; i < instances.numInstances();i++)
			for (int j=1; j < instances.numAttributes();j++) 
				try {	
					double value = instances.instance(i).value(j);
					if (Double.isNaN(value)) 
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
									String.format("Missing value %s in record %s",instances.attribute(j),instances.instance(i)));
					matrix.set(i,j-1,value);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
				}
		
		DataCoverage coverage = null;
		try {
			Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
			coverage = (DataCoverage) clazz.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
		String name = String.format("%s.%s",UUID.randomUUID().toString(),coverage.getName());
		ModelQueryResults m = new ModelQueryResults();
		m.setParameters(parameters);
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.WEKA.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?coverage.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		LiteratureEntry prediction = new LiteratureEntry(m.getName(),
				model_reporter.getURI(applicationRootReference.toString(),m));		
		prediction.setType(_type.Model);
		Template predictors = null;
		Template dependent = null;
		PredictedVarsTemplate predicted = null;
		
		if (coverage!= null) {
			coverage.build(matrix);

			predicted = new PredictedVarsTemplate(name+"#ApplicabilityDomain");
			Property property = new Property(coverage.getMetricName(),prediction);
			property.setEnabled(true);
			property.setLabel(String.format("http://www.opentox.org/api/1.1#%s",coverage.getMetricName()));
			predicted.add(property);
			property = new Property(coverage.getDomainName(),prediction);
			property.setLabel(Property.opentox_ConfidenceFeature);
			property.setClazz(Number.class);
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
			
			predictors = new Template(name+"#Independent");
			for (int i=1; i < instances.numAttributes(); i++) {
				property = createPropertyFromReference(new Reference(instances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
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
	
	protected void serializeModel(Object predictor, Instances newInstances, ModelQueryResults m) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(predictor);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		
		Form form = new Form();
		
		newInstances.delete();
		
		String encoded = Base64.encode(content);
		form.add("model", encoded);
		
		if (newInstances.classIndex()>=0)
			form.add("classIndex",Integer.toString(newInstances.classIndex()));		
		newInstances.delete();
		form.add("header", newInstances.toString());
		m.setContent(form.getWebRepresentation().getText());
	}
	/*
	protected void serializeModel(DataCoverage coverage, Instances instances, ModelQueryResults m) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(coverage);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		
		Form form = new Form();
		
		form.add("model", Base64.encode(content));
		form.add("header", instances.toString());
		m.setContent(form.getWebRepresentation().getText());
	}
	*/
}
