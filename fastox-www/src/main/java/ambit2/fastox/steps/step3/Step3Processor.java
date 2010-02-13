package ambit2.fastox.steps.step3;

import org.restlet.data.Form;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;

public class Step3Processor extends StepProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8851070408024957472L;
	public Step3Processor() {
		
	}
	@Override
	public Form process(Representation entity) throws AmbitException {
		Form form = new Form(entity);
		String dataset = form.getFirstValue(params.dataset.toString());
		if (dataset == null) throw new AmbitException("No dataset!");
		return form;
	}
}
