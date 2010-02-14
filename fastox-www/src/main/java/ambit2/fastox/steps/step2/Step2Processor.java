package ambit2.fastox.steps.step2;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;

public class Step2Processor extends StepProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943671624329016734L;
	@Override
	public Form process(Representation entity) throws AmbitException {
		Wizard wizard = Wizard.getInstance(WizardMode.A);
		Form form = new Form(entity);
		String text = form.getFirstValue(FastoxStepResource.params.text.toString());
		if (text != null) {
			Form query = new Form();
			query.add(FastoxStepResource.params.search.toString(), text);
			query.add("max","100");
			String[] s= new String[] {"ChemicalName","CASRN","EINECS","REACHRegistrationDate"};
			for (String n:s) 
			query.add("feature_uris[]",
					String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
							Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
			String uri = wizard.getService(SERVICE.compound)+"?"+query.getQueryString();

			form.add(params.dataset.toString(),uri);
		}
		return form;
	}
}
