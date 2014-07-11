package ambit2.rest.task;

import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.model.predictor.AbstractStructureProcessor;
import ambit2.rest.model.predictor.StructureProcessor;

public class CallableStructureOptimizer<USERID> extends CallableModelPredictor<IStructureRecord,AbstractStructureProcessor,USERID> {

	public CallableStructureOptimizer(Form form,
			Reference appReference,Context context,
			AbstractStructureProcessor predictor,USERID token) {
		super(form, appReference, context, predictor,token);

	}
	
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		super.processForm(applicationRootReference, form);
		Object mopac_commands = form.getFirstValue("mopac_commands");
		if (mopac_commands!=null && !"".equals(mopac_commands.toString().trim()))
			if (predictor instanceof StructureProcessor) try {
				((StructureProcessor)predictor).setMopac_commands(mopac_commands.toString().trim());
			} catch (Exception x) {}
	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		//StructureProcessor has a writer embedded
		return null;
	}

	
}	