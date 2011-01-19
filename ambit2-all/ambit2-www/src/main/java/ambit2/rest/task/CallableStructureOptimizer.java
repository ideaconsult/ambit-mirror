package ambit2.rest.task;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.model.predictor.AbstractStructureProcessor;

public class CallableStructureOptimizer<USERID> extends CallableModelPredictor<IStructureRecord,AbstractStructureProcessor,USERID> {

	public CallableStructureOptimizer(Form form,
			Reference appReference,Context context,
			AbstractStructureProcessor predictor,USERID token) {
		super(form, appReference, context, predictor,token);

	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		//StructureProcessor has a writer embedded
		return null;
	}

	
}	