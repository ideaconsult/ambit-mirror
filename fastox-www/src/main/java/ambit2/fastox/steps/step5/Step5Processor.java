package ambit2.fastox.steps.step5;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;

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
	protected String dataset;
	@Override
	public Form process(Representation entity) throws AmbitException {
		Form form = new Form(entity);

		String[] models = form.getValuesArray(params.model.toString());
		form.removeAll(params.model.toString());
		dataset = form.getFirstValue(params.dataset.toString());
		for (String model:models) {
			if ("on".equals(form.getFirstValue(model))) {
					try {
						runModel(model,dataset,form);
						form.add(params.model.toString(),model);
					} catch (ResourceException x) {
						
					}
			}
		}		
		return form;	
	
	}
	
	protected void runModel(String model, String compound, Form form) throws ResourceException {
		Representation r = null;
		try {
			form.removeAll(model);
			ClientResource client = new ClientResource(new Reference(model));
			
			client.setFollowingRedirects(false);
			Form query = new Form();
			query.add("dataset_uri",compound);
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
	

	
}
