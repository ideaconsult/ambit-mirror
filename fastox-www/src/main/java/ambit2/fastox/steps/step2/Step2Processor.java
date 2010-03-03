package ambit2.fastox.steps.step2;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.CASProcessor;
import ambit2.core.data.EINECS;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.StepException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.step1.Step1Resource.TABS;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.rest.OpenTox;

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
		
		int max = 1;
		try {
			max = Integer.parseInt(form.getFirstValue(FastoxStepResource.params.max.toString()));
		} catch (Exception x) {max =1;}	
		finally {
			session.setPageSize(Integer.toString(max));
		}

		Reference ref = getSearchQuery(form, wizard,max);

		if (ref != null) {
			session.setDatasetURI(ref.toString());
		}
		form.clear();
		return form;

	}

	protected Reference getSearchQuery(Form userDefinedSearch, Wizard wizard, int pageSize) throws AmbitException {
		Reference topRef = null;
		//max

		
		Form query = new Form();
		
		String tab = userDefinedSearch.getFirstValue("tab");
		String text = userDefinedSearch.getFirstValue(FastoxStepResource.params.text.toString());
		String search = userDefinedSearch.getFirstValue(FastoxStepResource.params.search.toString());
		String mode = userDefinedSearch.getFirstValue(FastoxStepResource.params.mode.toString());
		String file = userDefinedSearch.getFirstValue(FastoxStepResource.params.file.toString());
		String dataset = userDefinedSearch.getFirstValue(FastoxStepResource.params.dataset.toString());
		
		if (file != null) {
			//should not come here, goes into processMultiPartForm
			throw new StepException("file",String.format("Wrong place for file upload %s",file));
		} 
		if (search != null)  {
			if ("structure".equals(mode)) {
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/structure");
				query.add(FastoxStepResource.params.search.toString(), search);
				query.add(FastoxStepResource.params.max.toString(),"1");
				Exception x = parseStructure(search);
				if (x!=null) throw new StepException("search",x);

			} else if ("substructure".equals(mode)) {
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/smarts");
				query.add(FastoxStepResource.params.search.toString(), search);
				query.add(FastoxStepResource.params.text.toString(), text==null?"":text);
				query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
				
			} else { /// ("similarity".equals(mode)) {
				Exception e = parseStructure(search);
				if (e!=null) throw new StepException("search",e);
				
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/similarity");
				query.add(FastoxStepResource.params.search.toString(), search);
				
				try {  
					query.add(FastoxStepResource.params.threshold.toString().toString(), userDefinedSearch.getFirstValue(FastoxStepResource.params.threshold.toString()));
				} catch (Exception x) {
					query.add(FastoxStepResource.params.threshold.toString(), "0.85");
				}
				query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
			};
	
		} else if (text != null) {
			try {
				text = text.trim();
				//check if this is a SMILES , otherwise search as text
				SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
				IAtomContainer c = p.parseSmiles(text.trim());
				if ((c==null) || (c.getAtomCount()==0)) throw new InvalidSmilesException(text.trim());
				topRef = new Reference(wizard.getService(SERVICE.application)+"/query/structure");
				query.add(FastoxStepResource.params.search.toString(), text);		
				query.add(FastoxStepResource.params.max.toString(),"1");
			} catch (Exception x) {
				if (CASProcessor.isValidFormat(text)) { //then this is a CAS number
					if (!CASNumber.isValid(text)) throw new StepException("text",String.format("Invalid CAS Registry number %s",text));
					else query.add(FastoxStepResource.params.max.toString(),"1");
				} else if (EINECS.isValidFormat(text)) { //this is EINECS
					//we'd better not search for invalid numbers
					if (!EINECS.isValid(text)) throw new StepException("text",String.format("Invalid EINECS number %s",text));
					else query.add(FastoxStepResource.params.max.toString(),"1");
				}
				topRef = wizard.getService(SERVICE.compound);
				query.add(FastoxStepResource.params.search.toString(), text);
				query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
			}
			
		} else if (dataset != null) {
			topRef = new Reference(dataset);
			query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
			
		} else {
			if (TABS.Search.toString().equals(tab))
				throw new StepException("text",String.format("Please enter a query string!"));
			else if (TABS.Draw.toString().equals(tab))
				throw new StepException("search",String.format("Please draw a query structure!"));
			else if (TABS.Datasets.toString().equals(tab)) {
				throw new StepException("dataset",String.format("Please select a dataset!"));
			}
		}
		
		String[] s= new String[] {  "ChemicalName","IUPACName","CASRN","EINECS","REACHRegistrationDate"};
		for (String n:s)  
			query.add("feature_uris[]",
					String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
							Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
		if (dataset!=null)  {
			
			query.add("feature_uris[]",String.format("%s:%s%s",
					topRef.getScheme(),
					topRef.getHierarchicalPart(),
					OpenTox.URI.feature.getURI()));
		}

		topRef.setQuery(query.getQueryString());
	
		return topRef;
	}
	
	protected Exception parseStructure(String smiles) {
		try {
			SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			IAtomContainer c = p.parseSmiles(smiles);
			AtomConfigurator cfg = new AtomConfigurator();
			cfg.process(c);
			return null;
		} catch (Exception x) {
			return x;
		}
		
	}
}

