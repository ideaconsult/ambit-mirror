package ambit2.rest.task.tautomers;

import java.sql.Connection;

import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.task.CallableModelPredictor;
import ambit2.rest.task.TaskResult;

public class CallableTautomersGenerator<USERID> extends CallableModelPredictor<IStructureRecord,TautomersGenerator,USERID> {

	public CallableTautomersGenerator(Form form,
			Reference appReference,Context context,
			TautomersGenerator predictor,USERID token) {
		super(form, appReference, context, predictor,token);

	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		//TautomersGenerator has a writer embedded
		return null;
	}

	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
			if (foreignInputDataset) {
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Datasets not on this server are not supported!");
			} else {
				TaskResult task = new TaskResult(
						String.format("%s/query/relation/%s/HAS_TAUTOMER?dataset_uri=%s",
						applicationRootReference,		
						sourceReference.toString().indexOf("/dataset/")>0?"compound":"compound",
						Reference.encode(sourceReference.toString()))
						);
				task.setNewResource(false);
				return task;
			}
	}
	
}	