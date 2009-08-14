package ambit2.rest.propertyvalue;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
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
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * http://opentox.org/wiki/opentox/Feature
 * @author nina
 *
 */
public class FeatureResource<T> extends QueryResource<IQueryRetrieval<T>, T> {
	/**
	 * Parameters, expected in http headers
	 * @author nina
	 *
	 */
	public enum headers  {
			compound_id {
				@Override
				public boolean isMandatory() {	return true;}
			},
			conformer_id,
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
			return new StringConvertor(	new PropertyValueURIReporter(getRequest().getRootRef()) {
				@Override
				public void processItem(Object dataset, Writer output) {
					super.processItem(dataset, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else return new StringConvertor(new PropertyValueReporter());
					
	}		
	@Override
	protected QueryURIReporter<T, IQueryRetrieval<T>> getURUReporter(
			Reference baseReference) throws ResourceException {
		return new PropertyValueURIReporter(baseReference);
	}
	@Override
	protected IQueryRetrieval<T> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue();
		field.setSearchByID(true);
		
		IStructureRecord record = new StructureRecord();
		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		String id  = null;
		try {
			id = getParameter(requestHeaders,headers.compound_id.toString(),headers.compound_id.isMandatory());
			record.setIdchemical(Integer.parseInt(id));
			field.setChemicalsOnly(true);
		} catch (Exception x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %s",id))
					);
		}
		try {
			id = getParameter(requestHeaders,headers.conformer_id.toString(),headers.conformer_id.isMandatory());
			record.setIdstructure(Integer.parseInt(id));
			field.setChemicalsOnly(false);
		
		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {
			field.setValue(record);
		}
		try {
			Property p = new Property("");
			p.setId(Integer.parseInt(Reference.decode(request.getAttributes().get(featureID).toString())));
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
