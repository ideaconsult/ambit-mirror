package ambit2.rest.task.waffles;

import java.io.File;
import java.net.HttpURLConnection;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.db.processors.PropertyValuesWriter;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.task.CallableModelPredictor;

public class CallableWafflesPredictor<USERID> extends CallableModelPredictor<File[],WafflesPredictor,USERID> { 

	
	public CallableWafflesPredictor(Form form, Reference appReference,
			Context context, WafflesPredictor predictor,USERID token) {
		super(form, appReference, context,predictor,token);
	}

	@Override
	protected File[] createTarget(Reference reference) throws Exception {
		File[] files = new File[] {null,null};
		int pos = reference.toString().lastIndexOf("/dataset/");
		dataset_service = reference.toString().substring(0,pos+9);
		
		//todo fallback to RDF if arff is not available
		files[0] = File.createTempFile("wflinput_",".arff");
		files[0].deleteOnExit();
		HttpURLConnection uc=null;
		try {
			uc = ClientResourceWrapper.getHttpURLConnection(reference.toString(), "GET", ChemicalMediaType.WEKA_ARFF.toString());
			DownloadTool.download(uc.getInputStream(), files[0]);
			logger.fine(files[0].getAbsolutePath());

		} catch (Exception x) {
			throw x;
		} finally {
			try { if (uc != null) uc.getInputStream().close(); } catch (Exception x) {}
			try { if (uc != null) uc.disconnect(); } catch (Exception x) {}
		}
		
		//now predict
		try {
			//TODO set labels
			files[1] = (File) predictor.predict(files[0]);
			logger.fine(files[1].getAbsolutePath());
			//TODO handle errors
		} catch (Exception x) {
			throw x;
		}
		
		return files;

	}
	
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return new ARFFIterator(applicationRootReference);
	}

	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	/*
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
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
			return null;
	}
	*/
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		
		IProcessor<IStructureRecord, IStructureRecord> writer = getWriter();
		if (writer != null) p1.add(writer);
		
		return p1;
	}	

	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() throws Exception  {

			PropertyValuesWriter writer = new PropertyValuesWriter();
			writer.setDataset(new SourceDataset(sourceReference.toString(),
					new LiteratureEntry(predictor.getModelReporter().getURI(predictor.getModel()),sourceReference.toString())));
			return writer;
	}
}
