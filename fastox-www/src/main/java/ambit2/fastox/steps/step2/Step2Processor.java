package ambit2.fastox.steps.step2;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
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
import ambit2.fastox.users.ToxPredictSession.SearchMode;
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
		
		session.setSearch(null);
		session.setCondition(null);		
		int max = 1;
		try {
			max = Integer.parseInt(form.getFirstValue(FastoxStepResource.params.max.toString()));
		} catch (Exception x) {max =1;}	
		finally {
			session.setPageSize(Integer.toString(max));
		}

		try {
	
			Reference uri = Wizard.getInstance(WizardMode.A).getService(SERVICE.dataset);
			Reference ref = getSearchQuery(form, wizard,max,session);
			/*
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
			*/
			/*
			else {
				OTDataset dataset = OTDataset.dataset().withUri(ref).withDatasetService(uri);
				if (ref != null) 
					ref = OTDataset.dataset().withDatasetService(uri).copy(dataset).getUri();
			}
			*/
			if (ref != null) {
				session.setDatasetURI(ref.toString());

			}
			
			form.clear();
			return form;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		

	}

	protected Reference getSearchQuery(Form userDefinedSearch, Wizard wizard, int pageSize, IToxPredictSession session) throws AmbitException {
		Reference topRef = null;
		//max

		
		Form query = new Form();
		
		String tab = userDefinedSearch.getFirstValue("tab");
		String text = userDefinedSearch.getFirstValue(FastoxStepResource.params.text.toString());
		String condition = userDefinedSearch.getFirstValue(FastoxStepResource.params.condition.toString());		
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
				query.removeAll(FastoxStepResource.params.max.toString());
				query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
				session.setSearchMode(SearchMode.smarts);
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
				query.removeAll(FastoxStepResource.params.max.toString());
				query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
			};
	
		} else if (text != null) {
			try {
				text = text.trim();
				//check if this is a SMILES , otherwise search as text
				if (isInChI(text)) {
					session.setSearchMode(SearchMode.inchi);
					topRef = new Reference(String.format("%s/query/compound/%s/all",
							 		wizard.getService(SERVICE.application),Reference.encode(text)));
					query.removeAll(FastoxStepResource.params.max.toString());
					query.add(FastoxStepResource.params.max.toString(),"1");					
				} else {
					SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
					IAtomContainer c = p.parseSmiles(text.trim());
					if ((c==null) || (c.getAtomCount()==0)) throw new InvalidSmilesException(text.trim());
					topRef = new Reference(wizard.getService(SERVICE.application)+"/query/structure");
					query.add(FastoxStepResource.params.search.toString(), text);		
					query.removeAll(FastoxStepResource.params.max.toString());
					query.add(FastoxStepResource.params.max.toString(),"1");
					session.setSearchMode(SearchMode.smiles);
				}
			} catch (Exception x) {
				
				
				query.removeAll(FastoxStepResource.params.max.toString());
				if (CASProcessor.isValidFormat(text)) { //then this is a CAS number
					if (!CASNumber.isValid(text)) throw new StepException("text",String.format("Invalid CAS Registry number %s",text));
					else query.add(FastoxStepResource.params.max.toString(),"1");
					session.setSearchMode(SearchMode.cas);
				} else if (EINECS.isValidFormat(text)) { //this is EINECS
					//we'd better not search for invalid numbers
					if (!EINECS.isValid(text)) throw new StepException("text",String.format("Invalid EINECS number %s",text));
					else query.add(FastoxStepResource.params.max.toString(),"1");
					session.setSearchMode(SearchMode.einecs);
				} else {
					session.setSearch(text);
					session.setSearchMode(SearchMode.text);
					session.setCondition(condition);	
					query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
				}
				topRef = wizard.getService(SERVICE.compound);
				query.add(FastoxStepResource.params.search.toString(), text);
				if (condition != null)
					query.add(FastoxStepResource.params.condition.toString(),condition);	

			}
			
		} else if (dataset != null) {
			topRef = new Reference(dataset);
			query.removeAll(FastoxStepResource.params.max.toString());
			//query.add(FastoxStepResource.params.max.toString(),Integer.toString(pageSize));
			session.setSearchMode(SearchMode.dataset);
			
		} else {
			if (TABS.Search.toString().equals(tab))
				throw new StepException("text",String.format("Please enter a query string!"));
			else if (TABS.Draw.toString().equals(tab))
				throw new StepException("search",String.format("Please draw a query structure!"));
			else if (TABS.Datasets.toString().equals(tab)) {
				throw new StepException("dataset",String.format("Please select a dataset!"));
			}
		}
		/*
		String[] s= new String[] {  "ChemicalName","IUPACName","CASRN","EINECS"};//,"REACHRegistrationDate"};
		
		
		if (session.getFeatures()==null) session.setFeatures(new Form());
		
		for (String n:s)  {

			session.getFeatures().add("feature_uris[]",
					String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
							//Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
							Reference.encode(n)));
							
		}
		
		if (dataset!=null)  {
			
			session.getFeatures().add("feature_uris[]",
					String.format("%s:%s%s",
					topRef.getScheme(),
					topRef.getHierarchicalPart(),
					OpenTox.URI.feature.getURI()));
		}
		*/

		topRef.setQuery(query.getQueryString());
	
		return topRef;
	}
	
	public boolean isInChI(String inchi) {
		try {
			if (inchi.startsWith("InChI")) {
				InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
				InChIToStructure c =f.getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				return c.getAtomContainer().getAtomCount()>0;
			} else return false;
		} catch (Exception x) {
			return false;
		}
		
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

