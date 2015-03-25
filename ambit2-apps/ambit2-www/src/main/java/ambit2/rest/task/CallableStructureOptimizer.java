package ambit2.rest.task;

import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.model.predictor.AbstractStructureProcessor;
import ambit2.rest.model.predictor.StructureProcessor;

public class CallableStructureOptimizer<USERID> extends
		CallableModelPredictor<IStructureRecord, AbstractStructureProcessor, USERID> {

	public CallableStructureOptimizer(Form form, Reference appReference, Context context,
			AbstractStructureProcessor predictor, USERID token) {
		super(form, appReference, context, predictor, token);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ambit2.rest.task.CallableQueryProcessor#processForm(org.restlet.data.
	 * Reference, org.restlet.data.Form)
	 */
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		super.processForm(applicationRootReference, form);
	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		// StructureProcessor has a writer embedded
		return null;
	}

}