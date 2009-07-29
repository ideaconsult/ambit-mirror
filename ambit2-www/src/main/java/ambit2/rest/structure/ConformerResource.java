package ambit2.rest.structure;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.search.structure.QueryStructureByID;

/**
 * Handles /compound/{idchemical}/conformer/{idconformer}  
 * && /dataset/{datasetid}/compound/{idchemical}/conformer/{idconformer} 
 * <br>
 * Supports Content-Type:
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
	public final static String conformers = String.format("%s%s/all",compoundID,conformerKey);
	public final static String idconformer = "/{idconformer}";
	public final static String conformerID = String.format("%s%s%s",compoundID,conformerKey,idconformer);
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
			QueryStructureByID query = new QueryStructureByID();			
			query.setMaxRecords(-1);
			Object idconformer = request.getAttributes().get(ConformerResource.idconformer);
			if (idconformer!= null) {
				record.setIdstructure(Integer.parseInt(Reference.decode(idconformer.toString())));
				query.setChemicalsOnly(false);
			} else {
				query.setChemicalsOnly(true);
				query.setValue(record);
			}
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}	
	protected QueryReporter getURIReporter() {
		return new ConformerURIReporter<QueryStructureByID>(getRequest().getRootRef());
	}
}
