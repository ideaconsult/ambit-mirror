package ambit2.fastox.steps.step3;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTDatasets;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.steps.StepException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;

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
		processQuery(session);
		Form form = new Form(entity);
		if (session.getDatasetURI() == null) throw new AmbitException("No dataset!");
		
		Object o = form.getFirstValue(params.endpoint.toString());
		session.setEndpoint((o==null)?session.getEndpoint():Reference.decode(o.toString()));
		o = form.getFirstValue(params.endpoint_name.toString());
		session.setEndpointName((o==null)?session.getEndpointName():Reference.decode(o.toString()));
		session.clearModels();
		return form;
	}
	

	public void processQuery( IToxPredictSession session)
			throws AmbitException {

		try {
	
			Reference uri = Wizard.getInstance(WizardMode.A).getService(SERVICE.dataset);
			Reference ref = new Reference(session.getDatasetURI());
			
			logger.info(ref);
			if ("".equals(ref.getQuery())) {
				
			} else {

				OTDatasets datasets = OTDatasets.datasets();
				datasets.withDatasetService(uri);
				
				ClientResource resource = new ClientResource(ref);
				Representation r=null;
				try {
					r = resource.get(MediaType.TEXT_URI_LIST);
					BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));
					String line = null;
					while ((line = reader.readLine())!=null) {
						datasets.add(OTDataset.dataset(line.trim()).withDatasetService(uri));
						logger.info(line.trim());
					}					
				} catch (ResourceException x) {
					if (Status.CLIENT_ERROR_NOT_FOUND.equals(x.getStatus()))
						 throw new StepException("text","We did not find any matching entries for the search you performed in the OpenTox database. Please try again.");
				} catch (Exception x) {
					throw x;
				} finally {
					try {r.release();} catch (Exception x) {}
					try {resource.release();} catch (Exception x) {}
				}
				
				if (datasets.size()==0) throw new StepException("text","We did not find any matching entries for the search you performed in the OpenTox database. Please try again.");
				else if (datasets.size()==1) for (OTDataset d: datasets.getItems()) { ref = d.getUri(); break;}
				else {
					OTDataset dataset = datasets.merge();
					ref = dataset.getUri();
				}
			} 
		
			/*
			else {
				OTDataset dataset = OTDataset.dataset().withUri(ref).withDatasetService(uri);
				if (ref != null) 
					ref = OTDataset.dataset().withDatasetService(uri).copy(dataset).getUri();
			}
			*/
			if (ref != null) session.setDatasetURI(ref.toString());

		} catch (Exception x) {
			throw new AmbitException(x);
		}
		

	}
}
