package ambit2.rest.task.waffles;

import java.io.File;
import java.net.HttpURLConnection;
import java.sql.Connection;

import org.opentox.dsl.task.ClientResourceWrapper;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;

public class CallableWafflesPredictor<USERID> extends CallableQueryProcessor<File, IStructureRecord,USERID> { 
	protected WafflesPredictor predictor;
	protected File resultsFile;
	protected String dataset_service;
	
	public CallableWafflesPredictor(Form form, Reference appReference,
			Context context, WafflesPredictor predictor,USERID token) {
		super(appReference, form, context,token);
		this.predictor = predictor;
	}

	protected AbstractBatchProcessor createBatch(File target) throws Exception{
		return null;
	}

	@Override
	protected File createTarget(Reference reference) throws Exception {
		int pos = reference.toString().lastIndexOf("/dataset/");
		dataset_service = reference.toString().substring(0,pos+9);
		
		//todo fallback to RDF if arff is not available
		File file = File.createTempFile("waffles_input_",".arff");
		file.deleteOnExit();
		HttpURLConnection uc=null;
		try {
			uc = ClientResourceWrapper.getHttpURLConnection(reference.toString(), "GET", ChemicalMediaType.WEKA_ARFF.toString());
			DownloadTool.download(uc.getInputStream(), file);
			System.out.println(file);
			return file;
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (uc != null) uc.disconnect(); } catch (Exception x) {}
		}

	}

	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		//now predict
		try {
			resultsFile = (File) predictor.predict(target);
			System.out.println(resultsFile);
			//TODO handle errors
		} catch (Exception x) {
			x.printStackTrace();
		}
		
				if (!resultsFile.exists()) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"No results available!");
				try {
					
					RemoteTask task = new RemoteTask(new Reference(dataset_service),MediaType.TEXT_URI_LIST,
							new FileRepresentation(resultsFile,MediaType.APPLICATION_RDF_XML),Method.POST);
					//wait to complete, so that we can delete the tmp file
					Thread.sleep(200);
					while (!task.isDone()) {
						task.poll();
						Thread.sleep(800);
						Thread.yield();
					}
					if (task.isERROR()) 
						throw task.getError();
					else	
						return new TaskResult(task.getResult().toString());
				} catch (Exception x) {
					throw x;
				} finally {
					try { if (resultsFile.exists()) resultsFile.delete();} catch (Exception x) {}
					try { if (target.exists()) target.delete();} catch (Exception x) {}
				}

			
	}

	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}	

}
