package ambit2.fastox.steps.step5;

import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.users.IToxPredictSession;

/**
 * Form.model contains uri to models.
 * @author nina
 *
 */
public class Step5Processor extends StepProcessor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5628448981581045947L;

	
	public Form saveModels(Representation entity, IToxPredictSession session)
			throws AmbitException {
		Form form = new Form(entity);
		String[] models = form.getValuesArray(params.model.toString());
			
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
	@Override
	public Form process(Representation entity, IToxPredictSession session)
			throws AmbitException {
		
		Form form = saveModels(entity,session);
		if (session.getDatasetURI() == null) throw new AmbitException("No dataset!");
		String[] models = form.getValuesArray(params.model.toString());
		
		if (session.getNumberOfRunningModels()==0) session.clearModels();
		form.removeAll(params.model.toString());
		if ((models == null) || (models.length==0)) throw new AmbitException("No models!");
		for (String model:models) {
			if ("on".equals(form.getFirstValue(model))) {
					try {
						RemoteTask task = null;
						Object status = session.getModelStatus(model);
						if ((status!= null) && (status instanceof RemoteTask)) {
							//already running, will not do anything
						} else {
							
							Form query = new Form();
							query.add(ambit2.rest.OpenTox.params.dataset_uri.toString(),session.getDatasetURI());
							
							String launcher = String.format("%s/algorithm",getBaseReference());
							query.add(ambit2.rest.OpenTox.params.model_uri.toString(),model);
							task = new RemoteTask(new Reference(launcher),
										MediaType.TEXT_URI_LIST,
										query.getWebRepresentation()
										,Method.POST);
			
							
							session.addModel(model, task);
						}
					} catch (ResourceException x) {
						session.addModel(model,x);
					}
			}
			form.removeAll(model);
		}				
		form.clear();
		return form;	
	
	}
	
}
