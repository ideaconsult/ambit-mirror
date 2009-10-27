package ambit2.rest.propertyvalue;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.PropertyValue;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.value.UpdateCompoundPropertyValueNumber;
import ambit2.db.update.value.UpdateCompoundPropertyValueString;
import ambit2.db.update.value.UpdateStructurePropertyIDNumber;
import ambit2.db.update.value.UpdateStructurePropertyIDString;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * http://opentox.org/development/wiki/feature
<pre>
REST operations¶
Description 	Method 	URI 	Parameters 	Result 	Status codes
get the value for a specific feature 	GET 	/feature/compound/{cid}/feature_definition/{f_def_id} 	- 	text/xml -> xml representation of feature; text/plain -> text value of the feature 	200,404,503
get the value for a all features 	GET 	/feature/compound/{cid} 	- 	xml representation of all features 	200,404,503
get the value for a all feature values for a given feature definition in a dataset 	GET 	/feature/dataset/{did}/feature_definition/{fid} 	- 	xml representation of all features 	200,404,503
get the value for a all feature values for a given feature definition 	GET 	/feature/feature_definition/{fid} 	- 	xml representation of all features 	200,404,503
update the value for a specific feature 	PUT 	/feature/compound/{cid}/feature_definition/{f_def_id} 	value 	- 	200,400,404,503
update the value for a specific feature 	PUT 	/feature/compound/{cid}/conformer/{cid}/feature_definition/{f_def_id} 	value 	- 	200,400,404,503
save a new feature per compound 	POST 	/feature/compound/{cid}/feature_definition/{f_def_id} 	value 	URI of feature representation 	200,404,503
save a new feature per conformer 	POST 	/feature/compound/{cid}/conformer/{cid}/feature_definition/{f_def_id} 	value 	URI of feature representation 	200,404,503
Not supported yet: delete a feature 	DELETE 	/feature/compound/{cid}/feature_definition/{f_def_id} 	- 	- 	200,404,503 
</pre>
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
	};		
	public static final String featureID = "idfeature";
	public static final String resource = String.format("%s/{%s}",PropertyValueResource.featureKey,featureID);

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			queryObject = createQuery(getContext(), getRequest(),getResponse());
			error = null;
		} catch (ResourceException x) {
			queryObject = null;
			error = x;
		}
		customizeVariants(new MediaType[] {MediaType.TEXT_HTML,MediaType.TEXT_XML,MediaType.TEXT_URI_LIST,ChemicalMediaType.TEXT_YAML,MediaType.TEXT_PLAIN});
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	
		return new StringConvertor(new PropertyValueReporter());
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyValueXMLReporter(getRequest()));
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new PropertyValueHTMLReporter(getRequest(),true),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	getURUReporter(getRequest()),MediaType.TEXT_URI_LIST);
		} else return new StringConvertor(new PropertyValueReporter());
					
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
	//TODO refactor to throw ResourceException
	@Override
	protected IQueryRetrieval<PropertyValue> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue();
		field.setSearchByID(true);
		field.setChemicalsOnly(true);
		IStructureRecord record = getRecordByParameters();
		field.setChemicalsOnly(record.getIdstructure()<=0);
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
	protected PropertyValue createObjectFromHeaders(Form requestHeaders, Representation entity)
			throws ResourceException {
		String value = getParameter(requestHeaders,headers.value.toString(),headers.value.isMandatory());
		if (value == null) { //www form most probably
			Form form = new Form(entity);
			value = form.getFirstValue(headers.value.toString());
		}
		if (value == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Value not defined");
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
		getResponse().setLocationRef(getRequest().getOriginalRef());
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
	/*
	 * POST - create entity based on parameters in http header, creates a new entry in the databaseand returns an url to it

	public void createNewObject(Representation entity) throws ResourceException {
		
		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		
		StructureRecord record = new StructureRecord();	
		boolean chemicalsOnly = getRecordByParameters(record);
		
		String value = getParameter(requestHeaders,headers.value.toString(),headers.value.isMandatory());
		Property key = getPropertyByParameters();
		record.setProperty(key, value);
		
		PropertyValuesWriter valuesWriter = new PropertyValuesWriter();
		valuesWriter.setDataset(new SourceDataset("Unknown",LiteratureEntry.getInstance("Unknown", "Unknown")));
		Connection c = null;
		try {
			valuesWriter.setConnection(getConnection());
			valuesWriter.process(record);
			PropertyURIReporter uriReporter = new PropertyURIReporter(getRequest().getRootRef());
			getResponse().setLocationRef(uriReporter.getURI(key));
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(null);
		} catch (Exception x) {
			x.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);			
			getResponse().setEntity(null);
		} finally {
			try {valuesWriter.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
	}	
	*/
	/*
	@Override
	protected T createObjectFromHeaders(Form requestHeaders)
			throws ResourceException {
		String name = getParameter(requestHeaders,headers.name.toString(),headers.name.isMandatory());
		String refid = getParameter(requestHeaders,headers.reference_id.toString(),headers.reference_id.isMandatory());
		String type = getParameter(requestHeaders,headers.type.toString(),headers.type.isMandatory());
		LiteratureEntry entry =  new LiteratureEntry("","");
		try {
			entry.setId(Integer.parseInt(refid));
			Property p = new Property(name, entry);
			p.setLabel(Property.guessLabel(name));
			return p;
		} catch (NumberFormatException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
	}
	
	protected ambit2.db.update.AbstractObjectUpdate<T> createUpdateObject(T entry) throws ResourceException {
		
	};
	*/
}
