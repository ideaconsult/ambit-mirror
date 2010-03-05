package ambit2.rest.property;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

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

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.FreeTextPropertyQuery;
import ambit2.db.search.property.RetrieveFieldNames;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.property.CreatePropertyReferenceID;
import ambit2.db.update.property.ReadProperty;
import ambit2.db.update.property.UpdateProperty;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Feature definition resource http://opentox.org/development/wiki/feature
 * <br>
 * Supported REST operations:
 * <ul>
 * <li>GET 	 /feature/{id}  returns text/uri-list or RDF
 * <li>GET 	 /feature?search=<name>&condition=like   returns text/uri-list or RDF
 * <li>GET 	 /feature?sameas=<uri-of-sameas-resource>   returns text/uri-list or RDF
 * <li>POST	 /feature feature_uris[]=URI  or RDF representation in the content
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

	public final static String featuredef = OpenTox.URI.feature.getURI();
	public final static String idfeaturedef = OpenTox.URI.feature.getKey();
	
	public final static String featuredefID = String.format("/{%s}",idfeaturedef);

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
				MediaType.APPLICATION_JSON,
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
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) || 
				variant.getMediaType().equals(MediaType.APPLICATION_JSON)
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
				key = form.getFirstValue(QueryResource.sameas);
				String condition = form.getFirstValue(QueryResource.condition);
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setSearchByAlias(true);
					q.setFieldname(record);
					q.setChemicalsOnly(chemicalsOnly);
					try {
						q.setCondition(condition==null?StringCondition.getInstance(StringCondition.C_EQ):StringCondition.getInstance(condition));
					} catch (Exception x) {
						q.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
					}
					return q;	
				}
				key = form.getFirstValue(QueryResource.search_param);				
				if (key != null) {
						RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
						q.setSearchByAlias(false);
						q.setFieldname(record);
						q.setChemicalsOnly(chemicalsOnly);
						try {
							q.setCondition(condition==null?StringCondition.getInstance(StringCondition.C_REGEXP):StringCondition.getInstance(condition));
						} catch (Exception x) {
							q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
						}
						return q;
				}
				//otherwise
				ReadProperty property = new ReadProperty();
				property.setFieldname(record);
				property.setChemicalsOnly(chemicalsOnly);					
				return property;
				
			}
			else return new ReadProperty(new Integer(o.toString()));
		} catch (NumberFormatException x) {
			throw new InvalidResourceIDException(o);
			
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
	
	
	@Override
	protected RDFObjectIterator<Property> createObjectIterator(
			Reference reference, MediaType mediaType) throws ResourceException {
		RDFPropertyIterator iterator = new RDFPropertyIterator(reference,mediaType);
		iterator.setBaseReference(getRequest().getRootRef());
		return iterator;
	}
	@Override
	protected RDFObjectIterator<Property> createObjectIterator(
			Representation entity) throws ResourceException {
		RDFPropertyIterator iterator = new RDFPropertyIterator(entity,entity.getMediaType());
		iterator.setBaseReference(getRequest().getRootRef());
		return iterator;
	}
	@Override
	protected Property onError(String uri) {
		Property p = new Property(uri,new LiteratureEntry(uri,uri));
		p.setLabel(uri);
		return p;
	}
	@Override
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return getParameter(queryForm,OpenTox.params.feature_uris.toString(),
				OpenTox.params.feature_uris.getDescription(),
				true);
	}
	@Override
	protected AbstractUpdate createUpdateObject(Property entry)
			throws ResourceException {
		if (entry.getReference().getId()>0)
			return new CreatePropertyReferenceID(entry);
		else if (entry.getId()>0) return new UpdateProperty(entry);
		else return new CreateProperty(entry);
	}
	
	@Override
	protected QueryURIReporter<Property, IQueryRetrieval<Property>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new PropertyURIReporter(baseReference);
	}
	
	@Override
	protected void customizeEntry(Property entry, Connection connection)
			throws ResourceException {
		
		if (entry.getId()>0) return;
		QueryExecutor<RetrieveFieldNames> x = new QueryExecutor<RetrieveFieldNames>();
		ResultSet rs = null;
		try {
			RetrieveFieldNames q = new RetrieveFieldNames();
			q.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
			q.setValue(entry);
			x.setConnection(connection);
			rs = x.process(q);
			while (rs.next()) {
				Property p = q.getObject(rs);
				entry.setId(p.getId());
				entry.setLabel(p.getLabel());
				entry.setUnits(p.getUnits());
				entry.setNominal(p.isNominal());
				entry.setReference(p.getReference());
			}
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage(),e);
		} finally {
			try {rs.close();} catch (Exception e){}
			try {x.close();} catch (Exception e){}
		}
	}
}
