package ambit2.rest.substance.owner;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.rest.substance.SubstanceResource;

/**
 * All substance by owner
 * 
 * @author nina
 * 
 */
public class SubstanceByOwnerResource<Q extends IQueryRetrieval<SubstanceRecord>> extends
	SubstanceResource<Q, SubstanceRecord> {

    @Override
    protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
	Object owneruuid = request.getAttributes().get(OwnerSubstanceFacetResource.idowner);
	ReadSubstanceByOwner q = new ReadSubstanceByOwner(ReadSubstanceByOwner._ownersearchmode.owner_uuid,
		owneruuid.toString()) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 6003910893547753024L;

	    public ambit2.base.data.SubstanceRecord getObject(java.sql.ResultSet rs) throws AmbitException {
		ambit2.base.data.SubstanceRecord record = super.getObject(rs);
		record.setProperty(new SubstancePublicName(), record.getPublicName());
		record.setProperty(new SubstanceName(), record.getCompanyName());
		record.setProperty(new SubstanceUUID(), record.getCompanyUUID());
		record.setProperty(new SubstanceOwner(), record.getOwnerName());
		return record;
	    }
	};
	return (Q) q;

    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation put(Representation representation) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation put(Representation representation, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete() throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }
}
