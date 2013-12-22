package ambit2.rest.substance.study;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.substance.relation.ReadSubstanceRelation;
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

}
