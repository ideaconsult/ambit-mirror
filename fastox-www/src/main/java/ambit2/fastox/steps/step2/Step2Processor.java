package ambit2.fastox.steps.step2;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;

public class Step2Processor extends StepProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943671624329016734L;
	//construct search url and pass it over 
	@Override
	public Form process(Representation entity, IToxPredictSession session)
			throws AmbitException {
		Wizard wizard = Wizard.getInstance(WizardMode.A);
		Form form = new Form(entity);
		Form query = getFreeTextSearchQuery(form, wizard);
		if (query != null) {
			String uri = wizard.getService(SERVICE.compound)+"?"+query.getQueryString();
			session.setDatasetURI(uri);
		}
		return form;

	}

	protected Form getFreeTextSearchQuery(Form userDefinedSearch, Wizard wizard)  {
		String text = userDefinedSearch.getFirstValue(FastoxStepResource.params.text.toString());
		if (text != null) {
			Form query = new Form();
			query.add(FastoxStepResource.params.search.toString(), text);
			query.add("max","100");
			String[] s= new String[] {"ChemicalName","CASRN","EINECS","REACHRegistrationDate"};
			for (String n:s) 
			query.add("feature_uris[]",
					String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
							Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
			return query;
		}		
		return null;
	}
}


/*
			String uri = wizard.getService(SERVICE.compound)+"?"+query.getQueryString();
			Reference ref = new Reference(uri);
			Representation r = null;
			try {
				Form postDataset = new Form();
				postDataset.add("dataset_uri",uri);
				ClientResource resource = new ClientResource(wizard.getService(Wizard.SERVICE.dataset));
				r = resource.post(postDataset.getWebRepresentation(),MediaType.APPLICATION_WWW_FORM);
				try { r.release(); } catch (Exception x) {}
				
				ref = resource.getResponse().getLocationRef();
				Status status = resource.getStatus();
				while (status.equals(Status.REDIRECTION_SEE_OTHER) || status.equals(Status.SUCCESS_ACCEPTED)) {
					System.out.println(status);
					System.out.println(ref);
					resource.setReference(ref);
					Response response = resource.getResponse();

					status = response.getStatus();
					if (Status.REDIRECTION_SEE_OTHER.equals(status)) {
						ref = response.getLocationRef();
					} 
					try { response.release(); } catch (Exception x) {}

				}				
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				form.add(params.dataset.toString(),ref.toString());
				
			}
*/			