package ambit2.rest.structure;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.QueryURIReporter;

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
	
	@Override
	protected QueryStructureByID createQuery(Context context, Request request,
			Response response) throws ResourceException {
		media = getMediaParameter(request);
		try {
			IStructureRecord record = new StructureRecord();
			try {
				record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(idcompound).toString())));
			} catch (NumberFormatException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Invalid resource id %d",request.getAttributes().get(idcompound)),
						x
						);				
			}
			QueryStructureByID query = new QueryStructureByID();			
			query.setMaxRecords(-1);
			Object idconformer = request.getAttributes().get(ConformerResource.idconformer);
			try {
				record.setIdstructure(Integer.parseInt(Reference.decode(idconformer.toString())));
				query.setChemicalsOnly(false);
			} catch (Exception x) {
				record.setIdstructure(-1);
				query.setChemicalsOnly(true);
				query.setMaxRecords(-1);
				query.setValue(record);
			}
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,
					String.format("Invalid resource id %d",request.getAttributes().get(idcompound)),
					x
					);
		}
	}	
	protected QueryURIReporter getURIReporter() {
		return new ConformerURIReporter<QueryStructureByID>(getRequest());
	}
}
