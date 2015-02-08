package ambit2.rest.propertyvalue;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.db.update.value.UpdateCompoundPropertyValueNumber;
import ambit2.db.update.value.UpdateCompoundPropertyValueString;
import ambit2.db.update.value.UpdateStructurePropertyIDNumber;
import ambit2.db.update.value.UpdateStructurePropertyIDString;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**

 * @author nina
 *
 */
public class FeatureResource extends QueryResource<IQueryRetrieval<PropertyValue>, PropertyValue> {
	public final static String CompoundFeaturedefID = String.format("%s%s%s/{%s}",
			PropertyValueResource.featureKey,CompoundResource.compoundID,PropertyResource.featuredef,PropertyResource.idfeaturedef);
	public final static String ConformerFeaturedefID = String.format("%s%s%s/{%s}",
			PropertyValueResource.featureKey,ConformerResource.conformerID,PropertyResource.featuredef,PropertyResource.idfeaturedef);
	
	/**
	 * Parameters, expected in http headers
	 * @author nina
	 *
	 */
	public enum headers  {
			value;
			public boolean isMandatory() {
				return false;
			}			
			public String getDescription() {
				return toString();
			}
	};		
	public static final String featureID = "idfeature";
	public static final String resource = String.format("%s/{%s}",PropertyValueResource.featureKey,featureID);

	public FeatureResource() {
		super();
	}
	@Override
	protected void doInit() throws ResourceException {
		try {
			super.doInit();
		} catch (ResourceException x) {
			queryObject = null;
			error = x;
		}
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				ChemicalMediaType.TEXT_YAML,
				MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_JAVA_OBJECT});
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	
		return new StringConvertor(new PropertyValueReporter(),MediaType.TEXT_PLAIN,filenamePrefix);
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputWriterConvertor(
					new PropertyValueHTMLReporter(getRequest(),DisplayMode.table),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	getURUReporter(getRequest()),MediaType.TEXT_URI_LIST,filenamePrefix);
		} else return new StringConvertor(new PropertyValueReporter(),MediaType.TEXT_URI_LIST,filenamePrefix);
					
	}		
	@Override
	protected QueryURIReporter<PropertyValue, IQueryRetrieval<PropertyValue>> getURUReporter(
			Request baseReference) throws ResourceException {
		PropertyValueURIReporter reporter = new PropertyValueURIReporter<PropertyValue, IQueryRetrieval<PropertyValue>>(baseReference);
		if (queryObject instanceof AbstractQuery) {
			if (((AbstractQuery)queryObject).getValue() instanceof IStructureRecord)
			reporter.setRecord((IStructureRecord)((AbstractQuery)queryObject).getValue());
		}
		return reporter;		
	}
	
	@Override
	protected IQueryRetrieval<PropertyValue> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue();
		field.setSearchByID(true);
		field.setChemicalsOnly(true);
		IStructureRecord record = getRecordByParameters();
		field.setChemicalsOnly(record==null?true:record.getIdstructure()<=0);
		field.setValue(record);
		field.setFieldname(getPropertyByParameters()); 
		return (IQueryRetrieval) field;
	}
	protected Property getPropertyByParameters() throws ResourceException {
		try {
			Property p = new Property("");
			p.setId(Integer.parseInt(Reference.decode(getRequest().getAttributes().get(PropertyResource.idfeaturedef).toString())));
			return p;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}		
	}
	protected IStructureRecord getRecordByParameters() {
		IStructureRecord record = new StructureRecord();
		Object key = getRequest().getAttributes().get(CompoundResource.idcompound);		
		if (key != null) try {
			record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
			
		} catch (Exception x) { record = null;}
		
		key = getRequest().getAttributes().get(ConformerResource.idconformer);		
		if (key != null) try {
			if (record ==null) record = new StructureRecord();
			record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
			return record;
		} catch (Exception x) {  }	
		return record;
	}

	@Override
	protected PropertyValue createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		Form requestHeaders = new Form(entity);
		String value = getParameter(requestHeaders,headers.value.toString(),headers.value.getDescription(),headers.value.isMandatory());
		Property p = getPropertyByParameters();
		try {
			return new PropertyValue<Double>(p,Double.parseDouble(value.toString()));
		} catch (Exception x) {
			return new PropertyValue<String>(p,value.toString());
		}		
	}

	protected AbstractUpdate createUpdateObject(PropertyValue entry) throws ResourceException {
		IStructureRecord record = getRecordByParameters();
		
		if (record.getIdstructure()>0) 
			if (entry.getValue() instanceof Number) {
				UpdateStructurePropertyIDNumber u = new UpdateStructurePropertyIDNumber();
				u.setGroup(record);
				u.setObject(entry);
				return u;
			} else  {
				UpdateStructurePropertyIDString u = new UpdateStructurePropertyIDString();
				u.setGroup(record);
				u.setObject(entry);
				return u;
			}
		else
			if (entry.getValue() instanceof Number) {
				UpdateCompoundPropertyValueNumber u = new UpdateCompoundPropertyValueNumber();
				u.setGroup(record);
				u.setObject(entry);
				return u;
			} else  {
				UpdateCompoundPropertyValueString u = new UpdateCompoundPropertyValueString();
				u.setGroup(record);
				u.setObject(entry);
				return u;
			}			
	};

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {

		createNewObject(entity);
		getResponse().setLocationRef(getResourceRef(getRequest()));
		getResponse().setStatus(Status.SUCCESS_OK);
		getResponse().setEntity(get(new Variant(MediaType.TEXT_HTML)));
		return getResponse().getEntity();
	}
	@Override
	protected Representation put(Representation entity)
			throws ResourceException {

		createNewObject(entity);
		return entity;
	}
	
}
