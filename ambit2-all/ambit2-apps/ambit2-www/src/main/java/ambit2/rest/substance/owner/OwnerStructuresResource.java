package ambit2.rest.substance.owner;

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
import ambit2.rest.substance.composition.SubstanceStructuresResource;

/**
 * Substance composition. Retrieves a list of compounds given the substance owner
 * @author nina
 *
 */
public class OwnerStructuresResource extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {

	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Form form = getParams();
		setTemplate(createTemplate(form));
		setGroupProperties(context, request, response);				
		STRUCTURE_RELATION relation = null;
		try {
			Object cmp = request.getAttributes().get(SubstanceStructuresResource.compositionType);
			relation = STRUCTURE_RELATION.valueOf(cmp.toString().toUpperCase());
		} catch (Exception x) {}

		Object key = request.getAttributes().get(OwnerSubstanceFacetResource.idowner);
		if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		SubstanceRecord record = new SubstanceRecord();
		record.setOwnerUUID(key.toString());
		return new ReadSubstanceRelation(relation,record);
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
