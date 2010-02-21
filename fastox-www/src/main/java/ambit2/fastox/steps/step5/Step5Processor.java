package ambit2.fastox.steps.step5;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.rest.task.RemoteTask;

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

	
	@Override
	public Form process(Representation entity, IToxPredictSession session)
			throws AmbitException {
		
		Form form = new Form(entity);
		if (session.getDatasetURI() == null) throw new AmbitException("No dataset!");
		String[] models = form.getValuesArray(params.model.toString());
		form.removeAll(params.model.toString());
		
		if (session.getNumberOfRunningModels()==0) session.clearModels();
		
		if ((models == null) || (models.length==0)) throw new AmbitException("No models!");
		for (String model:models) {
			if ("on".equals(form.getFirstValue(model))) {
					try {
						Object status = session.getModelStatus(model);
						if ((status!= null) && (status instanceof RemoteTask)) {
							//already running, will not do anything
						} else {
							Form query = new Form();
							query.add(ambit2.rest.OpenTox.params.dataset_uri.toString(),session.getDatasetURI());
							RemoteTask task = new RemoteTask(new Reference(model),
									MediaType.APPLICATION_WWW_FORM,
									query.getWebRepresentation()
									,Method.POST,authentication);
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
/*
	protected void runModel(String model, String compound, Form form) throws ResourceException {
		Representation r = null;
		try {
			form.removeAll(model);
			ClientResource client = new ClientResource(new Reference(model));
			
			client.setFollowingRedirects(false);
			
			r = client.post(query.getWebRepresentation());

			Status status = client.getStatus();
			
			Reference uri = client.getResponse().getLocationRef();
			
			if (uri != null) {
				form.add(model,uri.toString());
				form.add(uri.toString(),status.getName());
			} else {
				String text = readUriList(r.getStream());
				form.add(model,text);
				form.add(text,status.getName());

				form.add(params.errors.toString(),status.toString());
			}

		} catch (ResourceException x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} catch (Exception x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} finally {
			try {r.release();} catch (Exception x) {}
			
		}			
	}	
	*/

	
}
