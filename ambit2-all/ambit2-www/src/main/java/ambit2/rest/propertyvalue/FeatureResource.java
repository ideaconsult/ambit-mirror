package ambit2.rest.propertyvalue;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * http://opentox.org/wiki/opentox/Feature
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
delete a feature 	DELETE 	/feature/compound/{cid}/feature_definition/{f_def_id} 	- 	- 	200,404,503 
</pre>
 * @author nina
 *
 */
public class FeatureResource<PropertyValue> extends QueryResource<IQueryRetrieval<PropertyValue>, PropertyValue> {
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
	public FeatureResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			query = createQuery(context, request, response);
			error = null;
		} catch (AmbitException x) {
			query = null;
			error = x;
		}
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	
		return new StringConvertor(new PropertyValueReporter());
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyValueXMLReporter(getRequest().getRootRef()));
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new PropertyValueHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	getURUReporter(getRequest().getRootRef()),MediaType.TEXT_URI_LIST);
		} else return new StringConvertor(new PropertyValueReporter());
					
	}		
	@Override
	protected QueryURIReporter<PropertyValue, IQueryRetrieval<PropertyValue>> getURUReporter(
			Reference baseReference) throws ResourceException {
		PropertyValueURIReporter reporter = new PropertyValueURIReporter<PropertyValue, IQueryRetrieval<PropertyValue>>(baseReference);
		if (query instanceof AbstractQuery) {
			if (((AbstractQuery)query).getValue() instanceof IStructureRecord)
			reporter.setRecord((IStructureRecord)((AbstractQuery)query).getValue());
		}
		return reporter;		
	}
	@Override
	protected IQueryRetrieval<PropertyValue> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue();
		field.setSearchByID(true);
		field.setChemicalsOnly(true);
		IStructureRecord record = new StructureRecord();
		Object key = request.getAttributes().get(CompoundResource.idcompound);		
		if (key != null) try {
			record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
			field.setChemicalsOnly(true);
		} catch (Exception x) { record = null;}
		
		key = request.getAttributes().get(ConformerResource.idconformer);		
		if (key != null) try {
			if (record ==null) record = new StructureRecord();
			record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
			field.setChemicalsOnly(false);
		} catch (Exception x) { }
		field.setValue(record);
		try {
			Property p = new Property("");
			p.setId(Integer.parseInt(Reference.decode(request.getAttributes().get(PropertyResource.idfeaturedef).toString())));
			field.setFieldname(p);
		} catch (Exception x) {
			throw new StatusException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		return (IQueryRetrieval) field;
	}

	@Override
	public boolean allowPost() {
		return true;
	}
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
