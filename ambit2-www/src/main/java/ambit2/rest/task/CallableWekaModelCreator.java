package ambit2.rest.task;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.UUID;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.UpdateExecutor;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.model.CreateModel;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.ModelURIReporter;


/** Creates a model, given an algorithm
 * 
 * @author nina
 *
 */
public class CallableWekaModelCreator extends CallableModelCreator<Instance> {
	protected ModelQueryResults model;
	protected Clusterer clusterer = null;
	protected Classifier classifier = null;
	protected String[] targetURI = null;
	/**
	 * 
	 * @param uri  Dataset URI
	 * @param applicationRootReference  URI of the application root (e.g. http://myhost:8080/ambit2)
	 * @param application AmbitApplication
	 * @param algorithm  Algorithm
	 * @param reporter ModelURIReporter (to generate model uri)
	 */
	public CallableWekaModelCreator(Form form,
			Reference applicationRootReference, AmbitApplication application,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter) {
		super(form, applicationRootReference, application,algorithm,reporter,alg_reporter);
		targetURI = OpenTox.params.target.getValuesArray(form);
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		return new RDFInstancesParser(applicationRootReference.toString());
	}


	
	protected ModelQueryResults createModel() throws Exception {

		Instances instances = ((RDFInstancesParser)batch).getInstances();
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
		else throw new Exception(String.format("Unknown algorithm %s",algorithm.toString()));

		
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
			logger.warn("Error setting algorithm parameters, assuming defaults");
		}	
		
		
		/*
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(new Filter[] {
                new ReplaceMissingValues()
                });
        multiFilter.setInputFormat(instances);
        */
		//remove firstCompoundID attribute
		String[] options = new String[2];
		options[0] = "-R";                                   
		options[1] = "1";                                   
		Remove remove = new Remove();                       
		remove.setOptions(options);                          
		remove.setInputFormat(instances);   
		 
		//Filter filter = new RemoveUseless();
		//filter.setInputFormat(instances);
		
        Instances newInstances = Filter.useFilter(instances, remove);	
        
		String name = String.format("%s.%s",UUID.randomUUID().toString(),weka.getClass().getName());
		ModelQueryResults m = new ModelQueryResults();
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.WEKA.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?weka.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		LiteratureEntry prediction = new LiteratureEntry(m.getName(),
				reporter.getURI(applicationRootReference.toString(),m));		
		
		Template predictors = null;
		Template predicted = null;
		if (clusterer!= null) {
			clusterer.buildClusterer(newInstances);
			predicted = new Template(name+"#Predicted");
			Property property = new Property("Cluster",prediction);
			predicted.add(property);

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

			classifier.buildClassifier(newInstances);
			predicted = new Template(name+"#Predicted");
			Property property = createPropertyFromReference(new Reference(newInstances.attribute(newInstances.classIndex()).name()), prediction); 

			predicted.add(property);
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
		}
		

		m.setPredictors(predictors);
		m.setDependent(predicted);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(clusterer==null?classifier:clusterer);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		m.setContent(Base64.encode(content));
		/*
		try {
		 InputStream in = new ByteArrayInputStream(content);
				 //m.getContent().getBytes("UTF-8"));
		ObjectInputStream ois =  new ObjectInputStream(in);
		
		clusterer = null; classifier = null;
	 	weka = ois.readObject();
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		return m;
	}

	@Override
	protected Reference createReference(Connection connection) throws Exception {
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			model = createModel();
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			x.process(update);
			return new Reference(reporter.getURI(model));
		} catch (WekaException e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			try {x.close();} catch (Exception xx){}
		}
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return reference;
	}
	


}
