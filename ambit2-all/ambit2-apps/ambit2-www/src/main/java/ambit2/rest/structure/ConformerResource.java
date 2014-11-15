package ambit2.rest.structure;

import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.structure.DeleteStructure;
import ambit2.rest.OpenTox;

/**
 * Conformer resource as in http://opentox.org/development/wiki/structure
 * REST Operations:
 * <ul>
 * <li>GET /compound/{id1}/conformer/all  returns all available conformres per comound 
 * <li>GET /compound/{id1}/conformer/{id2}  returns a single conformer
 * </li>
 * </ul>
 *Content-Type:
<pre>
application/pdf
text/xml
chemical/x-cml
chemical/x-mdl-molfile
chemical/x-mdl-sdfile
chemical/x-daylight-smiles
image/png
</pre>
 * @author nina
 *
 */
public class ConformerResource extends CompoundResource {

	public final static String conformerKey = "/conformer";
	public final static String conformer = String.format("%s%s",compoundID,conformerKey);
	public final static String idconformer = "idconformer";
	//public final static String conformers = String.format("%s%s",compoundID,conformerKey);
	public final static String conformerID = String.format("%s%s/{%s}",compoundID,conformerKey,idconformer);
	
	public ConformerResource() {
		super();
		setDocumentation(new ResourceDoc("structure","Conformer"));
		
	}
	@Override
	public boolean isHtmlbyTemplate() {
		return getRequest().getAttributes().get(ConformerResource.idconformer)!=null;
	}
	@Override
	protected String getDefaultTemplateURI(Context context, Request request,
			Response response) {
		/*
		Object id = request.getAttributes().get(OpenTox.URI.compound.getKey());
		if (id != null)
			//return String.format("riap://application/dataset/%s%s",id,PropertyResource.featuredef);
		return String.format("%s%s/%s%s",
				getRequest().getRootRef(),
					OpenTox.URI.compound.getURI(),
					request.getAttributes().get(ConformerResource.idcompound),
					OpenTox.URI.conformer.getURI(),
					request.getAttributes().get(ConformerResource.idconformer),
					PropertyResource.featuredef);		
		else 
			return super.getDefaultTemplateURI(context,request,response);
			*/
		return null;
	}
	@Override
	protected QueryStructureByID createQuery(Context context, Request request,
			Response response) throws ResourceException {
		media = getMediaParameter(request);
		Form form = request.getResourceRef().getQueryAsForm();
		try { 
			headless = Boolean.parseBoolean(form.getFirstValue("headless")); 
		} catch (Exception x) { headless=false;}
		try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
		try {
			
			setTemplate(createTemplate(context, request, response));
			IStructureRecord record = new StructureRecord();
			try {
				record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(idcompound).toString())));
			} catch (NumberFormatException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						"Invalid resource id",
						x
						);				
			}
			QueryStructureByID query = new QueryStructureByID();			
			query.setPageSize(-1);
			Object idconformer = request.getAttributes().get(ConformerResource.idconformer);
			try {
				record.setIdstructure(Integer.parseInt(Reference.decode(idconformer.toString())));
				query.setChemicalsOnly(false);
			} catch (Exception x) {
				record.setIdstructure(-1);
				query.setChemicalsOnly(true);
				query.setPageSize(-1);
				query.setValue(record);
			}
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,
					"Invalid resource id",
					x
					);
		}
	}	
	protected QueryURIReporter getURIReporter() {
		return new ConformerURIReporter<QueryStructureByID>(getRequest(),getDocumentation());
	}
	
	
	@Override
	protected AbstractUpdate createDeleteObject(IStructureRecord record)
			throws ResourceException {
		record = record==null?new StructureRecord():record;
		Object key = getRequest().getAttributes().get(OpenTox.URI.compound.getKey());
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
			if(record.getIdchemical()<=0)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			key = getRequest().getAttributes().get(OpenTox.URI.conformer.getKey());
			record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
			if(record.getIdstructure()<=0)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);			
			else {
				DeleteStructure c =  new DeleteStructure();
				c.setObject(record);
				return c;
			}
		} catch (ResourceException x) {	
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		Representation entity = getRequestEntity();
		try {
			executeUpdate(entity, 
					null,
					createDeleteObject(null));
			return getResponseEntity();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,x.getMessage(),x);
		}
	}
	@Override
	protected IStructureRecord getRecord() {
		IStructureRecord record = super.getRecord();
		Object idconformer = getRequest().getAttributes().get(ConformerResource.idconformer);
		if (idconformer!=null)
			try {record.setIdstructure(Integer.parseInt(Reference.decode(idconformer.toString())));} catch (Exception x) {}
		return record;
	}

}
