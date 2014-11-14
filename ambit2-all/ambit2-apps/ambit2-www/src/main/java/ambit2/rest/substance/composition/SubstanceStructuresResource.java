package ambit2.rest.substance.composition;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.substance.relation.ReadSubstanceRelation;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.substance.SubstanceResource;

/**
 * Substance composition. Retrieves a list of compounds (in dataset format)
 * @author nina
 *
 */
public class SubstanceStructuresResource extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String structure = "/structure";
	public final static String compositionType = "compositionType";
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Form form = getParams();
		setTemplate(createTemplate(form));
		setGroupProperties(context, request, response);		
		STRUCTURE_RELATION relation = null;
		try {
			Object cmp = request.getAttributes().get(compositionType);
			relation = STRUCTURE_RELATION.valueOf(cmp.toString().toUpperCase());
		} catch (Exception x) {}

		Object key = request.getAttributes().get(SubstanceResource.idsubstance);
		if (key==null) return new ReadSubstanceRelation(relation, null);
		try {
			return new ReadSubstanceRelation(relation,new SubstanceRecord(Integer.parseInt(key.toString())));
		} catch (Exception x) {
			int len = key.toString().trim().length(); 
			if ((len > 40) && (len <=45)) {
				return new ReadSubstanceRelation(relation,new SubstanceRecord(key.toString()));
			}	
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
	
	@Override
	protected void setGroupProperties(Context context, Request request,
			Response response) throws ResourceException {
		String[] r = NCISearchProcessor.METHODS.reach.getOpenToxEntry();
		if (r==null) return;
		Template gp = new Template();
		for (int i=0; i < r.length;i++) {
			Property p = new Property(r[i]);
			p.setLabel(r[i]);
			p.setEnabled(true);
			p.setOrder(i+1);
			gp.add(p);
		}
		setGroupProperties(gp);
	}

}
