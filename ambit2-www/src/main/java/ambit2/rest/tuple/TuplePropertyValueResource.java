package ambit2.rest.tuple;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.PropertiesTuple;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveTuplePropertyValue;
import ambit2.rest.StatusException;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Retrieves all property -value pairs for a given chemical and a given tuple
 * @author nina
 *
 */
public class TuplePropertyValueResource<PropertyValue> extends PropertyValueResource<PropertyValue> {
	public static String resourceCompoundID = String.format("%s/%s/{%s}",CompoundResource.compoundID,TupleResource.resourceTag,TupleResource.resourceKey);
	public static String resourceConformerID = String.format("%s/%s/{%s}",ConformerResource.conformerID,TupleResource.resourceTag,TupleResource.resourceKey);
	
	public TuplePropertyValueResource(Context context, Request request, Response response) {
		super(context,request,response);
	}
	

	@Override
	protected IQueryRetrieval<PropertyValue> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveTuplePropertyValue field = new RetrieveTuplePropertyValue();
		//RetrieveTupleStructure field = new RetrieveTupleStructure();
		IStructureRecord record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(CompoundResource.idcompound).toString())));
		} catch (NumberFormatException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",request.getAttributes().get(CompoundResource.idcompound)))
					);
		}
		try {
			record.setIdstructure(Integer.parseInt(Reference.decode(request.getAttributes().get(ConformerResource.idconformer).toString())));
			field.setChemicalsOnly(false);
		
		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {
			field.setValue(record);
		}
		try {
			Object idtuple = Reference.decode(request.getAttributes().get(TupleResource.resourceKey).toString());
			field.setFieldname(new PropertiesTuple(new Integer(idtuple.toString()),null));

		} catch (Exception x) {
			field.setFieldname(null);
		}
		return (IQueryRetrieval)field;		
	}

}