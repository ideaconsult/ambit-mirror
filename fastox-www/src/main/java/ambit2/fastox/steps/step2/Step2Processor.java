package ambit2.fastox.steps.step2;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
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

		Reference ref = getSearchQuery(form, wizard);
		System.out.println(ref);
		if (ref != null) {
			session.setDatasetURI(ref.toString());
		}
		form.clear();
		return form;

	}

	protected Reference getSearchQuery(Form userDefinedSearch, Wizard wizard)  {
		Reference topRef = null;
		//max
		int max = 1;
		try {
			max = Integer.parseInt(userDefinedSearch.getFirstValue(FastoxStepResource.params.max.toString()));
		} catch (Exception x) {max =1;}
		
		Form query = new Form();
		query.add(FastoxStepResource.params.max.toString(),Integer.toString(max));
		
		String text = userDefinedSearch.getFirstValue(FastoxStepResource.params.text.toString());
		String search = userDefinedSearch.getFirstValue(FastoxStepResource.params.search.toString());
		String mode = userDefinedSearch.getFirstValue(FastoxStepResource.params.mode.toString());
		String file = userDefinedSearch.getFirstValue(FastoxStepResource.params.file.toString());
		
		if (file != null) {
			//upload
			Representation input = null;
			MediaType mime = null;
			//RemoteTask task = new RemoteTask(wizard.getService(SERVICE.dataset),mime,input,Method.POST,authentication);
		} if (search != null)  {
			if ("structure".equals(mode)) {
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/similarity");
				query.add(FastoxStepResource.params.search.toString(), search);
				query.add("threshold", "0.9999");
				
			} else if ("substructure".equals(mode)) {
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/smarts");
				query.add(FastoxStepResource.params.search.toString(), search);
				query.add(FastoxStepResource.params.text.toString(), text==null?"":text);
				
			} else { /// ("similarity".equals(mode)) {
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/structure");
				query.add(FastoxStepResource.params.search.toString(), search);
				query.add("threshold", "0.85");
			};
	
		} else if (text != null) {
			try {
				//check if this is a SMILES , otherwise search as text
				SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
				IAtomContainer c = p.parseSmiles(text.trim());
				if ((c==null) || (c.getAtomCount()==0)) throw new InvalidSmilesException(text.trim());
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/structure");
				query.add(FastoxStepResource.params.search.toString(), text);				
			} catch (Exception x) {
				topRef = wizard.getService(SERVICE.compound);
				query.add(FastoxStepResource.params.search.toString(), text);
			}

		}
		
		String[] s= new String[] {"ChemicalName","CASRN","EINECS","REACHRegistrationDate"};
		for (String n:s) 
		query.add("feature_uris[]",
				String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
						Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
		topRef.setQuery(query.getQueryString())		;
		return topRef;
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