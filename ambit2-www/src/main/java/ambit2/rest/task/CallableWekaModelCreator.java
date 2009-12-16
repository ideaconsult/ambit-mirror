package ambit2.rest.task;

import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.List;

import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.engine.io.WriterOutputStream;

import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Parameter;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
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
	
	public CallableWekaModelCreator(Reference uri,
			Reference applicationRootReference, AmbitApplication application,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter) {
		super(uri, applicationRootReference, application);
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
			x.printStackTrace();
		}		
		Instances instances = ((RDFInstancesParser)batch).getInstances();
		clusterer.buildClusterer(instances);
		ModelQueryResults m = new ModelQueryResults();
		
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
	protected Reference createReference() throws Exception {
		model = createModel();
		return new Reference(modelUriReporter.getURI(model));
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
