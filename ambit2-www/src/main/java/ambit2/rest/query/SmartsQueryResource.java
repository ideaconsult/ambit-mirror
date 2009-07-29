package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FunctionalGroup;

/**
 * Query by smarts
 * @author nina
 *
 */
public class SmartsQueryResource  extends StructureQueryResource<QuerySMARTS> {

	public SmartsQueryResource(Context context, Request request,
			Response response) {
		super(context, request, response);
	}
	
	@Override
	protected QuerySMARTS createQuery(Context context, Request request,
			Response response) throws AmbitException {
		try {
//			System.out.println(request.getAttributes().get("org.restlet.http.headers"));
			String smarts = Reference.decode(request.getAttributes().get("smarts").toString());
			QuerySMARTS query = new QuerySMARTS();
			query.setChemicalsOnly(true);
			query.setValue(new FunctionalGroup(smarts,smarts,smarts));
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		

	}		

}
