package ambit2.rest.query;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.db.search.structure.QueryExactStructure;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyResource;

public class ExactStructureQueryResource extends StructureQueryResource<QueryExactStructure> {
	public final static String resource =  "/structure";
	//protected String textSearch = "CasRN";
	protected String smiles;
	protected String dataset_id;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
	}
	
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return (dataset_id == null)?
				null
				//String.format("%s%s?text=%s",
					//	getRequest().getRootRef(),PropertyResource.featuredef,Reference.encode(textSearch))				
			:
				
			String.format("%s%s/%s%s",
						getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),dataset_id,PropertyResource.featuredef);				
	}	
	@Override
	protected QueryExactStructure createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
		
		smiles = getSMILES(form,true);
		if (smiles == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty SMILES");
		try {
			SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
			QueryExactStructure q = new QueryExactStructure();
			q.setChemicalsOnly(true);
			
			IAtomContainer c = p.parseSmiles(smiles); 
			q.setValue(c);
			setTemplate(createTemplate(context, request, response));
			return q;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	/*
	@Override
	protected QueryStructure createQuery(Context context, Request request,
			Response response) throws ResourceException {
		smiles = getSMILES(new Form(getRequest().getResourceRef().getQueryAsForm()),true);
		if (smiles == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty SMILES");
		try {
			QueryStructure q = new QueryStructure();
			q.setChemicalsOnly(true);
			q.setValue(smiles);
			q.setFieldname("smiles");
			q.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
			return q;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	*/

}
