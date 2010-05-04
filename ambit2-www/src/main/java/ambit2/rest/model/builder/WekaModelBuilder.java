package ambit2.rest.model.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveUseless;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

/**
 * Builds WEKA model from ModelQueryResults. Input variables and (optionally) target variables are required.
 * @author nina
 *
 */
public class WekaModelBuilder extends ModelBuilder<Instances,Algorithm, ModelQueryResults> {
	protected Clusterer clusterer = null;
	protected Classifier classifier = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4372904815135663667L;
	public WekaModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.targetURI = targetURI;
		this.parameters = parameters;

	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {

		Instances instances = trainingData;
		if ((instances==null) || (instances.numInstances()==0) || (instances.numAttributes()==0)) 
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset!");
		
		Object weka = null;
		try {
			Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
			weka = clazz.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
		clusterer = null; classifier = null;
		if (weka instanceof Clusterer) clusterer = (Clusterer) weka;
		else if (weka instanceof Classifier) {
			classifier = (Classifier) weka;
			if (targetURI == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No target variable! "+OpenTox.params.target);
		}
		else throw new AmbitException(String.format("Unknown algorithm %s",algorithm.toString()));

		
		String[] prm = algorithm.getParametersAsArray();
		if (prm!=null)
		try {
			if (classifier!= null) classifier.setOptions(prm);
			else if (clusterer != null) {
					clusterer.getClass().getMethod(
			                "setOptions",
			                new Class[] {}).
			        invoke(clusterer, prm);					
			}
		} catch (Exception x) {
			Context.getCurrentLogger().warning("Error setting algorithm parameters, assuming defaults" + x.getMessage());
		}	
		
		

		//remove firstCompoundID attribute
		String[] options = new String[2];
		options[0] = "-R";                                   
		options[1] = "1";                                   
		Remove remove = new Remove();          
		try {
			remove.setOptions(options);                          
			remove.setInputFormat(instances);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		 
		/*
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(new Filter[] {
        		remove
        //        new ReplaceMissingValues()
                });
        multiFilter.setInputFormat(instances);
        		*/
		//Filter filter = new RemoveUseless();
		//filter.setInputFormat(instances);
		Instances newInstances = null;
		try {
	        newInstances = Filter.useFilter(instances, remove);
	        //Filter filter = new RemoveUseless();
	        //filter.setInputFormat(newInstances);
	       // newInstances = Filter.useFilter(newInstances, filter);
	       // filter = new ReplaceMissingValues();
	       // filter.setInputFormat(newInstances);
	       // newInstances = Filter.useFilter(newInstances, filter);    
		} catch (Exception x) {
			throw new AmbitException(x);
		}
        
		String name = String.format("%s.%s",UUID.randomUUID().toString(),weka.getClass().getName());
		ModelQueryResults m = new ModelQueryResults();
		m.setParameters(parameters);
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.WEKA.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?weka.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		LiteratureEntry prediction = new LiteratureEntry(m.getName(),
				model_reporter.getURI(applicationRootReference.toString(),m));		
		
		Template predictors = null;
		Template dependent = null;
		Template predicted = null;
		//System.out.println("Build");
		
		if (clusterer!= null) {
			try {
				clusterer.buildClusterer(newInstances);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
			predicted = new Template(name+"#Predicted");
			Property property = new Property("Cluster",prediction);
			predicted.add(property);

			dependent = new Template("Empty");
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
		} else if (classifier != null) {
			
			for (String t : targetURI) 
				for (int i = 0; i< newInstances.numAttributes(); i++)
					if (newInstances.attribute(i).name().equals(t)) {
						newInstances.setClassIndex(i);
						break;
					}
			try {
				classifier.buildClassifier(newInstances);
			} catch (Exception x) {
				throw new AmbitException(x);
			}			
			dependent = new Template(name+"#Dependent");
			Property property = createPropertyFromReference(new Reference(newInstances.attribute(newInstances.classIndex()).name()), entry); 
			dependent.add(property);
			
			predicted = new Template(name+"#predicted");
			Property predictedProperty = new Property(property.getName(),prediction); 
			predictedProperty.setLabel(property.getLabel());
			predictedProperty.setUnits(property.getUnits());
			predicted.add(predictedProperty);
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
		}
		//System.out.println("Done");

		m.setPredictors(predictors);
		m.setDependent(dependent);
		m.setPredicted(predicted);
		
		try {
			serializeModel(clusterer==null?classifier:clusterer, newInstances, m);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
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
		
		form.add("model", Base64.encode(content));
		newInstances.delete();
		if (newInstances.classIndex()>=0)
			form.add("classIndex",Integer.toString(newInstances.classIndex()));		
		newInstances.delete();
		form.add("header", newInstances.toString());
		m.setContent(form.getWebRepresentation().getText());
	}
}
