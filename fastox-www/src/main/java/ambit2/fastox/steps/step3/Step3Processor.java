package ambit2.fastox.steps.step3;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.users.IToxPredictSession;

public class Step3Processor extends StepProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8851070408024957472L;
	public Step3Processor() {
		
	}
	@Override
	public Form process(Representation entity, IToxPredictSession session)
			throws AmbitException {
		Form form = new Form(entity);
		if (session.getDatasetURI() == null) throw new AmbitException("No dataset!");
		
		Object o = form.getFirstValue(params.endpoint.toString());
		session.setEndpoint((o==null)?session.getEndpoint():Reference.decode(o.toString()));
		o = form.getFirstValue(params.endpoint_name.toString());
		session.setEndpointName((o==null)?session.getEndpointName():Reference.decode(o.toString()));
		session.clearModels();
		return form;
	}
	
	
}
