package ambit2.rest.task;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.model.predictor.StructureProcessor;

public class CallableStructureOptimizer extends CallableModelPredictor<IStructureRecord,StructureProcessor> {

	public CallableStructureOptimizer(Form form,
			Reference appReference,Context context,
			StructureProcessor predictor) {
		super(form, appReference, context, predictor);

	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		//StructureProcessor has a writer embedded
		return null;
	}

}	