package ambit2.rest.task.weka;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;
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


/** Creates a model, given an algorithm
 * 
 * @author nina
 *
 */
public class CallableWekaModelCreator<USERID> extends CallableModelCreator<Instances,Instance,FilteredWekaModelBuilder,USERID> {
	

	/**
	 * 
	 * @param uri  Dataset URI
	 * @param applicationRootReference  URI of the application root (e.g. http://myhost:8080/ambit2)
	 * @param application AmbitApplication
	 * @param algorithm  Algorithm
	 * @param reporter ModelURIReporter (to generate model uri)
	 */
	public CallableWekaModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			USERID token) {
		super(form, context,algorithm,
				new FilteredWekaModelBuilder(applicationRootReference,
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


	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		builder.setTrainingData(((RDFInstancesParser)batch).getInstances());
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			model = createModel();
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			x.process(update);
			
			writeAnnotations(model.getPredicted(), x);
			
			return new TaskResult(builder.getModelReporter().getURI(model));
		} catch (WekaException e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
		} catch (AmbitException e) {
			Context.getCurrentLogger().severe(e.getMessage());
			if ((e.getCause()!=null) && (e.getCause() instanceof WekaException)) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getCause().getMessage(),e.getCause());
			else 
				throw e;
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
