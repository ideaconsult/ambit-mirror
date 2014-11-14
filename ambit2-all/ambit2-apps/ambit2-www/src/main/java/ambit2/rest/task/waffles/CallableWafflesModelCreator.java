package ambit2.rest.task.waffles;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.WekaException;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.instance.SparseToNonSparse;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.UpdateExecutor;
import ambit2.db.update.model.CreateModel;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.CallableModelCreator;
import ambit2.rest.task.TaskResult;


/** Creates a model, given an algorithm by Waffles machine learning library
 * 
 * @author nina
 *
 */
public class CallableWafflesModelCreator<USERID> extends CallableModelCreator<File,String,WafflesModelBuilder,USERID> {
	

	/**
	 * 
	 * @param uri  Dataset URI
	 * @param applicationRootReference  URI of the application root (e.g. http://myhost:8080/ambit2)
	 * @param application AmbitApplication
	 * @param algorithm  Algorithm
	 * @param reporter ModelURIReporter (to generate model uri)
	 */
	public CallableWafflesModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			USERID token) {
		super(form, context,algorithm,
				new WafflesModelBuilder(applicationRootReference,
						reporter,
						alg_reporter,
						OpenTox.params.target.getValuesArray(form),
						OpenTox.params.parameters.getValuesArray(form)),
			token);
	}

	@Override
	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		return new RDFInstancesParser(builder.getApplicationRootReference().toString());
	}
	/**
	TODO download ARFF directly, instead of reading Weka instances in memory
	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("No dataset URI");
		
		File.createTempFile(prefix, suffix);
		DownloadTool.download(url, file);
		builder.setTrainingData(trainingData);
		return null;
	}
	 * 
	 */
	
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		File trainingData = File.createTempFile("wfltrain_", ".arff");
		trainingData.deleteOnExit();
		Instances instances = ((RDFInstancesParser)batch).getInstances();
		instances.deleteAttributeAt(0);
		//sort attributes by name (i.e. attribute URI). 
		List<Attribute> sorted = new ArrayList<Attribute>();
		for (int i=0; i < instances.numAttributes(); i++)
			sorted.add(instances.attribute(i));
		Collections.sort(sorted,new Comparator<Attribute>() {
			@Override
			public int compare(Attribute o1, Attribute o2) {
				return o1.name().compareTo(o2.toString());
			}
		});
		StringBuilder order = null;
		for (int i=0; i < sorted.size(); i++) {
			if (order == null) order = new StringBuilder();
			else order.append(",");
			order.append(sorted.get(i).index()+1);
		}
		Reorder reorder = new Reorder();
		String[] options = new String[2];
		options[0] = "-R";                                   
		options[1] = order.toString();   
		reorder.setOptions(options);
		reorder.setInputFormat(instances);
		instances = Filter.useFilter(instances,reorder);
		
	    SparseToNonSparse sp = new SparseToNonSparse();
	    sp.setInputFormat(instances);
	    Instances newInstances = Filter.useFilter(instances, sp);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(newInstances);
		saver.setFile(trainingData);
		saver.writeBatch();
		// Leave the header only
		newInstances.delete(); 
		builder.setHeader(newInstances);
	
		builder.setTrainingData(trainingData);
		
		
		
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			model = createModel();
			//trainingData.delete();
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			x.process(update);
			
			writeAnnotations(model.getPredicted(), x);
			
			return new TaskResult(builder.getModelReporter().getURI(model));
		} catch (WekaException e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
		} catch (Exception e) {
			Context.getCurrentLogger().severe(e.getMessage());
			throw e;
		} finally {
			try {x.close();} catch (Exception xx){}
		}
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return reference;
	}
	@Override
	protected ModelQueryResults createModel() throws Exception {
		ModelQueryResults model = super.createModel();
		if ((model != null) && (model.getTrainingInstances()==null) && (sourceReference!=null))
			model.setTrainingInstances(sourceReference.toString());
		return model;
	}


}
