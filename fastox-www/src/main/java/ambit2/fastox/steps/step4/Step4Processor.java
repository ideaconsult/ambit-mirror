package ambit2.fastox.steps.step4;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.users.IToxPredictSession;

public class Step4Processor extends StepProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4143247838588092340L;
	public Step4Processor() {
		
	}
	@Override
	public Form process(Representation entity, IToxPredictSession session)
			throws AmbitException {
		Form form = new Form(entity);
		if (session.getDatasetURI() == null) throw new AmbitException("No dataset!");
		String[] models = form.getValuesArray(params.model.toString());
		form.removeAll(params.model.toString());
		
		session.clearModels();
		
		if ((models == null) || (models.length==0)) throw new AmbitException("No models!");
		for (String model:models) {
			if ("on".equals(form.getFirstValue(model))) {
					try {
						session.addModel(model, Boolean.TRUE);
					} catch (ResourceException x) {
						
					}
			}
		}				
		return form;
	}
}
