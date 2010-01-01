package ambit2.rest.task;

import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.io.WriterOutputStream;
import org.restlet.resource.ResourceException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveUseless;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Parameter;
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
	public CallableWekaModelCreator(Reference datasetURI,
			Reference applicationRootReference, AmbitApplication application,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI) {
		super(datasetURI, applicationRootReference, application,algorithm,reporter,alg_reporter);
		this.targetURI = targetURI;
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
		
		Filter filter = new RemoveUseless();
		filter.setInputFormat(instances);
		
        Instances newInstances = Filter.useFilter(instances, filter);	
        
		String name = String.format("%s.%s",UUID.randomUUID().toString(),weka.getClass().getName());
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?weka.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		
		Template predictors = null;
		Template predicted = null;
		if (clusterer!= null) {
			clusterer.buildClusterer(newInstances);
			predicted = new Template(name+"Predicted");
			for (int i=0; i < clusterer.numberOfClusters(); i++) {
				Property property = new Property(String.format("Cluster%d",i+1),entry);
				predicted.add(property);
			}
			predictors = new Template(name+"Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				Property property = new Property(newInstances.attribute(i).name(),entry);
				predictors.add(property);
			}				
		} else if (classifier != null) {
			
			for (String t : targetURI) 
				for (int i = 0; i< instances.numAttributes(); i++)
					if (instances.attribute(i).name().equals(t)) {
						newInstances.setClassIndex(i);
						break;
					}
			
			classifier.buildClassifier(newInstances);
			predicted = new Template(name+"Predicted");
			Property property = new Property(newInstances.attribute(newInstances.classIndex()).name(),entry);
			predicted.add(property);
			predictors = new Template(name+"Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = new Property(newInstances.attribute(i).name(),entry);
				predictors.add(property);
			}				
		}
		
		ModelQueryResults m = new ModelQueryResults();
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		m.setPredictors(predictors);
		m.setDependent(predicted);
		
		StringWriter writer = new StringWriter();
		WriterOutputStream wos = new WriterOutputStream(writer);
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(wos);
		oos.writeObject(clusterer==null?classifier:clusterer);
		oos.flush();
		oos.close();		
		m.setContent(writer.toString());
		System.out.println(m.getContent());
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
