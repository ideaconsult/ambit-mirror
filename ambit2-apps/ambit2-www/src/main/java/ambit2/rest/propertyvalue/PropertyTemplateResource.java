package ambit2.rest.propertyvalue;

import java.io.Serializable;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.RetrieveTemplatePropertyValue;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

public class PropertyTemplateResource<T extends Serializable> extends PropertyValueResource<T> {
	public static final String resource = "/template";
	public static final String resourceID = "/{idtemplate}";
	public static final String compoundTemplate = String.format("%s%s",CompoundResource.compoundID,resource);
	public static final String compoundTemplateID = String.format("%s%s/{idtemplate}",CompoundResource.compoundID,resource);
	public static final String conformerTemplateID =  String.format("%s%s/{idtemplate}",ConformerResource.conformerID,resource);
	
	public static final String TemplateIDConformer =  String.format("%s/{idtemplate}%s",ConformerResource.conformerID,resource);
	public static final String TemplateIDCompound = String.format("%s/{idtemplate}%s",CompoundResource.compoundID,resource);
	

	@Override
	protected IQueryRetrieval<T> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		RetrieveTemplatePropertyValue  field = new RetrieveTemplatePropertyValue();
		//field.setSearchByAlias(true);
		
		IStructureRecord record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(CompoundResource.idcompound).toString())));
		} catch (NumberFormatException x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %d",request.getAttributes().get(CompoundResource.idcompound)),
					x
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
			field.setFieldname(null);
			Object id = request.getAttributes().get("idtemplate");
			if (id != null) {
				Template template = new Template();
				try {
					template.setId(Integer.parseInt(id.toString()));
				} catch (NumberFormatException x) {
					template.setName(Reference.decode(id.toString()));
					template.setId(-1);
				}
				field.setFieldname(template);
			} 
		} catch (Exception x) {
			field.setFieldname(null);
		}
		return (IQueryRetrieval) field;
	}

}
