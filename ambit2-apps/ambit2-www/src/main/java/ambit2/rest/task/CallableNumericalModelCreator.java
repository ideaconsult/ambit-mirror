package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import weka.core.Instances;
import Jama.Matrix;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.CoverageModelBuilder;

public class CallableNumericalModelCreator<USERID> extends CallableModelCreator<Instances,Matrix,CoverageModelBuilder,USERID> {
	
	public CallableNumericalModelCreator(
			Form form,
			Reference applicationRootReference,
			Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			USERID token) {

		super(form, context,algorithm,
				new CoverageModelBuilder(applicationRootReference,
						reporter,
						alg_reporter,
						OpenTox.params.target.getValuesArray(form),
						OpenTox.params.parameters.getValuesArray(form),
						OpenTox.params.confidenceOf.getFirstValue(form)==null?null:OpenTox.params.confidenceOf.getFirstValue(form).toString()
						),
						token);
	}

	@Override
	public TaskResult doCall() throws Exception {
		Context.getCurrentLogger().fine("Start()");
		Connection connection = null;
		try {

			try {
				target = createTarget(sourceReference);
				builder.setTrainingData((target instanceof Instances)?(Instances)target:null);
			} catch (Exception x) {
				target = sourceReference;
			}
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();			
			return createReference(connection);
		} catch (Exception x) {
			Context.getCurrentLogger().log(Level.SEVERE,x.getMessage(),x);
			throw x;
		} finally {
			Context.getCurrentLogger().fine("Done");
			try { connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
		
	}
	
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return null;
	}
	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		BufferedReader reader = null;
		HttpURLConnection client = null;
		try {
			client = ClientResourceWrapper.getHttpURLConnection(reference.toString(), "GET", ChemicalMediaType.WEKA_ARFF.toString());
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			return new Instances(reader);
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try { if (client != null) client.getInputStream().close(); } catch (Exception x) {}
			try { if (client != null) client.disconnect(); } catch (Exception x) {}
		}
	}
	@Override
	protected ModelQueryResults createModel() throws Exception {
		ModelQueryResults model = super.createModel();
		if ((model != null) && (model.getTrainingInstances()==null) && (sourceReference!=null))
			model.setTrainingInstances(sourceReference.toString());
		return model;
	}
}
