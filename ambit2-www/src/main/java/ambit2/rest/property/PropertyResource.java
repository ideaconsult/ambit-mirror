package ambit2.rest.property;

import java.util.ArrayList;

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

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.FreeTextPropertyQuery;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.property.CreatePropertyReferenceID;
import ambit2.db.update.property.ReadProperty;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Feature definition resource http://opentox.org/development/wiki/feature
 * <br>
 * Supported REST operations:
 * <ul>
 * <li>GET 	 /feature/{id}  returns text/uri-list or text/xml or text/html
 * <li>POST 	POST 	 /feature (as specified)
 * </ul>

 * @author nina
 *
 */
public class PropertyResource extends QueryResource<IQueryRetrieval<Property>, Property> {
	/**
	 * Parameters, expected in http headers
	 * @author nina
	 *
	 */
	public enum headers  {
			name {
				@Override
				public boolean isMandatory() {
					return true;
				}
			},
			reference_id {
				@Override
				public boolean isMandatory() {
					return true;
				}
			},
			type;
			public boolean isMandatory() {
				return false;
			}
	};	
	public final static String featuredef = "/feature";
	public final static String idfeaturedef = "id_feature_definition";
	public final static String featuredefID = String.format("/{%s}",idfeaturedef);
	/*
	public final static String CompoundFeaturedefID = String.format("%s%s/{%s}",CompoundResource.compoundID,featuredef,idfeaturedef);
	public final static String ConformerFeaturedefID = String.format("%s%s/{%s}",ConformerResource.conformerID,featuredef,idfeaturedef);
	public final static String ConformerFeaturedef = String.format("%s%s",ConformerResource.conformerID,featuredef);
	public final static String CompoundFeaturedef = String.format("%s%s",CompoundResource.compoundID,featuredef);
	*/
	protected boolean collapsed ;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				ChemicalMediaType.TEXT_YAML,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT
				});	
	}
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyDOMReporter(getRequest()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				PropertyURIReporter r = new PropertyURIReporter(getRequest());
				r.setDelimiter("\n");
				return new StringConvertor(r,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) {
			return new RDFJenaConvertor<Property, IQueryRetrieval<Property>>(
					new PropertyRDFReporter<IQueryRetrieval<Property>>(getRequest(),variant.getMediaType())
					,variant.getMediaType());		
		} else 
			return new OutputWriterConvertor(
					new PropertyHTMLReporter(getRequest(),collapsed)
					,MediaType.TEXT_HTML);
	}

	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		IStructureRecord record = null;
		boolean chemicalsOnly = true;
		Object key = request.getAttributes().get(CompoundResource.idcompound);		
		if (key != null) try {
			record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
		} catch (Exception x) { record = null;}
		
		key = request.getAttributes().get(ConformerResource.idconformer);		
		if (key != null) try {
			if (record ==null) record = new StructureRecord();
			record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
			chemicalsOnly = false;
		} catch (Exception x) { }
		
		Object o = request.getAttributes().get(idfeaturedef);
		try {
			collapsed = o==null;
			if (o==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				IQueryRetrieval<Property> qf = getFreeTextQuery(getContext(), getRequest(), getResponse());
				if (qf != null) return qf;
				key = form.getFirstValue(QueryResource.search_param);
				if (key != null) {
						RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
						q.setFieldname(record);
						q.setChemicalsOnly(chemicalsOnly);
						q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
						return q;
				} else {
					ReadProperty property = new ReadProperty();
					property.setFieldname(record);
					property.setChemicalsOnly(chemicalsOnly);					
					return property;
				}
			}
			else return new ReadProperty(new Integer(o.toString()));
		} catch (Exception x) {
			collapsed = true;
			return new ReadProperty();
		} finally {
		}
	}
	
	protected IQueryRetrieval<Property> getFreeTextQuery(Context context, Request request,
			Response response) throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		String[] keys = form.getValuesArray("text");
		
		ArrayList<String> s = new ArrayList<String>();
		for (String key : keys) {
			if (key==null)continue;
			String[] k = key.split(" ");
			for (String kk:k) s.add(kk);
		}
		if (s.size()==0) return null;
		keys = s.toArray(keys);
		if ((keys!=null) && (keys.length>0)) {
			FreeTextPropertyQuery query = new FreeTextPropertyQuery();
			query.setFieldname(keys);
			query.setValue(keys);
			return query;
		} else return null;
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (getRequest().getAttributes().get(idfeaturedef)==null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return getResponse().getEntity();
	}
	
	/**
<pre>
Description  	Method  	URI  	Parameters  	Result  	Status codes
create a new feature definition  	 POST  	 /feature  	 name: String, reference_id: String, type: String  	 URI of new feature definition  	200,400,404,503
</pre>
	 */
	@Override
	protected Property createObjectFromHeaders(Form requestHeaders, Representation entity)
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
	@Override
	protected AbstractUpdate createUpdateObject(Property entry)
			throws ResourceException {
		return new CreatePropertyReferenceID(entry);
	}
	
	@Override
	protected QueryURIReporter<Property, IQueryRetrieval<Property>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new PropertyURIReporter(baseReference);
	}
}
