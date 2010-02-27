package ambit2.rest.query;

import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.db.search.structure.QueryExactStructure;

public class ExactStructureQueryResource extends StructureQueryResource<QueryExactStructure> {
	public final static String resource =  "/structure";
	protected String smiles;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
	}
	
	@Override
	protected QueryExactStructure createQuery(Context context, Request request,
			Response response) throws ResourceException {
		smiles = getSMILES(new Form(getRequest().getResourceRef().getQueryAsForm()),true);
		if (smiles == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty SMILES");
		try {
			SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			QueryExactStructure q = new QueryExactStructure();
			q.setChemicalsOnly(true);
			
			q.setValue(p.parseSmiles(smiles));
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
