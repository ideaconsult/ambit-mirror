package ambit2.rest.structure;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryStructureByID;

/**
 * Handles /compound/{idchemical}/conformer/{idconformer}  
 * && /dataset/{datasetid}/compound/{idchemical}/conformer/{idconformer} 
 * @author nina
 *
 */
public class ConformerResource extends CompoundResource {

	public final static String conformerKey = "/conformer";
	public final static String conformer = String.format("%s%s",compoundID,conformerKey);
	public final static String idconformer = "/{idconformer}";
	public final static String conformerID = String.format("%s%s%s",compoundID,conformer,idconformer);
	public final static String conformerID_media = String.format("%s%s",conformerID,"/{media}");
	
	public ConformerResource(Context context, Request request, Response response) {
		super(context,request,response);

	}
	@Override
	public String[] URI_to_handle() {
		return new String[] {conformer,conformerID,conformerID_media};
	}	
	@Override
	protected QueryStructureByID createQuery(Context context, Request request,
			Response response) throws AmbitException {
		media = getMediaParameter(request);
		try {
			//System.out.println(request.getAttributes().get("org.restlet.http.headers"));
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(idcompound).toString())));			
			record.setIdstructure(Integer.parseInt(Reference.decode(request.getAttributes().get(idconformer).toString())));
			QueryStructureByID query = new QueryStructureByID();
			query.setChemicalsOnly(false);
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}	
}
