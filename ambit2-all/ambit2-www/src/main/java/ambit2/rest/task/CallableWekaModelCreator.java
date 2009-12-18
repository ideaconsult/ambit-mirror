package ambit2.rest.task;

import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.engine.io.WriterOutputStream;

import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Parameter;
import ambit2.db.UpdateExecutor;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.model.CreateModel;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.ModelURIReporter;


/** Creates a model, given an algorithm
 * 
 * @author nina
 *
 */
public class CallableWekaModelCreator extends CallableQueryProcessor<Object, Instance> {
	protected Algorithm algorithm;
	protected ModelQueryResults model;
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelUriReporter;
	protected Clusterer clusterer = null;
	
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
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter) {
		super(datasetURI, applicationRootReference, application);
		this.modelUriReporter = reporter;
		this.algorithm = algorithm;
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		return new RDFInstancesParser(applicationRootReference.toString());
	}

	@Override
	protected ProcessorsChain<Instance, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}
	
	protected ModelQueryResults createModel() throws Exception {
		clusterer = new SimpleKMeans();
		//Clusterer clusterer = new MakeDensityBasedClusterer();
		try {
			String[] prm = new String[algorithm.getParameters().size()];
			List<Parameter> param = algorithm.getParameters();
			for (int i=0; i < param.size();i++) { 
				prm[i] = param.get(i).getValue().toString();
			}
			((SimpleKMeans)clusterer).setOptions(prm);
		} catch (Exception x) {
			logger.warn("Error setting algorithm parameters, assuming defaults");
		}		
		Instances instances = ((RDFInstancesParser)batch).getInstances();
		/*
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(new Filter[] {
                new ReplaceMissingValues()
                });
        multiFilter.setInputFormat(instances);
        */
		/*
		Filter filter = new RemoveUseless();
		filter.setInputFormat(instances);
		
        Instances newInstances = Filter.useFilter(instances, filter);	
        */	
		String name = String.format("%s.%s",UUID.randomUUID(),clusterer.getClass().getName());
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?clusterer.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		Template predicted = new Template();
		clusterer.buildClusterer(instances);
		for (int i=0; i < clusterer.numberOfClusters(); i++) {
			Property property = new Property(String.format("Cluster%d",i+1),entry);
			predicted.add(property);
		}
		ModelQueryResults m = new ModelQueryResults();
		m.setName(name);
		m.setPredictors(predicted);
		m.setDependent(new Template("Empty"));
		
		StringWriter writer = new StringWriter();
		WriterOutputStream wos = new WriterOutputStream(writer);
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(wos);
		oos.writeObject(clusterer);
		oos.flush();
		oos.close();		
		m.setContent(writer.toString());
		System.out.println(m.getContent());
		return m;
	}

	@Override
	protected Reference createReference(Connection connection) throws Exception {
		try {
			model = createModel();
			CreateModel update = new CreateModel(model);
			UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
			x.setConnection(connection);
			x.process(update);
			return new Reference(modelUriReporter.getURI(model));
		} catch (Exception x) {
			logger.error(x);
			throw x;
		}
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return reference;
	}

	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return null;
	}
}
