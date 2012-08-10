package ambit2.rest.task.waffles;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
import weka.filters.unsupervised.instance.SparseToNonSparse;
import ambit2.core.data.model.Algorithm;
import ambit2.db.UpdateExecutor;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
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
		File trainingData = File.createTempFile("waffles_train_", ".arff");
		trainingData.deleteOnExit();
		Instances instances = ((RDFInstancesParser)batch).getInstances();
		instances.deleteAttributeAt(0);
	    SparseToNonSparse sp = new SparseToNonSparse();
	    sp.setInputFormat(instances);
	    Instances newInstances = Filter.useFilter(instances, sp);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(newInstances);
		saver.setFile(trainingData);
		saver.writeBatch();
		/**
		 * Variables
		 */
		List<Attribute> dependent_URI = new ArrayList<Attribute>();
		List<Attribute> predictors_URI = new ArrayList<Attribute>();
		StringBuilder dataOptions = null;
		 
		for (int i = 0; i< newInstances.numAttributes(); i++) {
			boolean isTarget = false;
			for (String t : builder.getTargetURI()) 
				if (newInstances.attribute(i).name().equals(t)) {
					if (dataOptions==null) dataOptions = new StringBuilder();
					else dataOptions.append(",");
					dataOptions.append(i);
					dependent_URI.add(newInstances.attribute(i));
					isTarget = true;
					break;
				}
			if (!isTarget) 
				predictors_URI.add(newInstances.attribute(i));
		}
		builder.setDependent_URI(dependent_URI);
		builder.setPredictors_URI(predictors_URI);
		if (dataOptions!=null) builder.setDataOptions(String.format("-labels %s", dataOptions));
		builder.setTrainingData(trainingData);
		
		
		
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			model = createModel();
			trainingData.delete();
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
