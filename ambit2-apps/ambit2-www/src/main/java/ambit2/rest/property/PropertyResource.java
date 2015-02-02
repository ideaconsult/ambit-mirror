package ambit2.rest.property;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.rdf.ns.OT;

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
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.FreeTextPropertyQuery;
import ambit2.db.search.property.RetrieveFieldNames;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.property.CreatePropertyReferenceID;
import ambit2.db.update.property.DeleteProperty;
import ambit2.db.update.property.ReadProperty;
import ambit2.db.update.property.UpdateProperty;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Feature definition resource http://opentox.org/development/wiki/feature <br>
 * Supported REST operations:
 * <ul>
 * <li>GET /feature/{id} returns text/uri-list or RDF
 * <li>GET /feature?search=<name>&condition=like returns text/uri-list or RDF
 * <li>GET /feature?sameas=<uri-of-sameas-resource> returns text/uri-list or RDF
 * <li>POST /feature feature_uris[]=URI or RDF representation in the content
 * </ul>
 * 
 * <pre>
 * Create a new feature from RDF representation
 * curl -H "Content-type:application/rdf+xml" -X POST -d @feature.rdf http://service.net:8080/feature -v
 * </pre>
 * 
 * @author nina
 * 
 */
public class PropertyResource extends QueryResource<IQueryRetrieval<Property>, Property> {
    /**
     * Parameters, expected in http headers
     * 
     * @author nina
     * 
     */

    public final static String featuredef = OpenTox.URI.feature.getURI();
    public final static String idfeaturedef = OpenTox.URI.feature.getKey();

    public final static String featuredefID = String.format("/{%s}", idfeaturedef);

    protected DisplayMode _dmode;

    public PropertyResource() {
	super();
	setDocumentation(new ResourceDoc("Feature", "Feature"));
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "feature.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.TEXT_URI_LIST, ChemicalMediaType.TEXT_YAML,
		MediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
		MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVA_OBJECT });
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    PropertyURIReporter r = new PropertyURIReporter(getRequest());
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    PropertyURIReporter r = new PropertyURIReporter(getRequest());
	    return new OutputWriterConvertor(new PropertyJSONReporter(getRequest()), MediaType.APPLICATION_JSON);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {
	    return new RDFJenaConvertor<Property, IQueryRetrieval<Property>>(
		    new PropertyRDFReporter<IQueryRetrieval<Property>>(getRequest(), variant.getMediaType(),
			    getDocumentation()), variant.getMediaType(), filenamePrefix);
	} else {
	    PropertyURIReporter r = new PropertyURIReporter(getRequest());
	    return new OutputWriterConvertor(new PropertyJSONReporter(getRequest()), MediaType.APPLICATION_JSON);
	}    
    }

    @Override
    protected IQueryRetrieval<Property> createQuery(Context context, Request request, Response response)
	    throws ResourceException {

	IStructureRecord record = null;
	boolean chemicalsOnly = true;
	Object key = request.getAttributes().get(CompoundResource.idcompound);
	if (key != null)
	    try {
		record = new StructureRecord();
		record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
	    } catch (Exception x) {
		record = null;
	    }

	key = request.getAttributes().get(ConformerResource.idconformer);
	if (key != null)
	    try {
		if (record == null)
		    record = new StructureRecord();
		record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
		chemicalsOnly = false;
	    } catch (Exception x) {
	    }

	Form form = request.getResourceRef().getQueryAsForm();
	try {

	    headless = Boolean.parseBoolean(form.getFirstValue("headless"));
	} catch (Exception x) {
	    headless = false;
	}

	Object o = request.getAttributes().get(idfeaturedef);
	try {
	    _dmode = o == null ? DisplayMode.table : DisplayMode.singleitem;
	    if (o == null) {
		IQueryRetrieval<Property> qf = getFreeTextQuery(getContext(), getRequest(), getResponse());
		if (qf != null)
		    return qf;
		key = form.getFirstValue(OpenTox.params.sameas.toString());
		String condition = form.getFirstValue(QueryResource.condition);
		if (key != null) {
		    key = Reference.decode(key.toString());
		    if (!key.toString().startsWith("http://") && !key.toString().startsWith("https://"))
			key = String.format("%s%s", OT.NS, key);
		    RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(key.toString());
		    q.setSearchByAlias(true);
		    q.setFieldname(record);
		    q.setChemicalsOnly(chemicalsOnly);
		    try {
			q.setCondition(condition == null ? StringCondition.getInstance(StringCondition.C_EQ)
				: StringCondition.getInstance(condition));
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
			q.setCondition(condition == null ? StringCondition.getInstance(StringCondition.C_REGEXP)
				: StringCondition.getInstance(condition));
		    } catch (Exception x) {
			q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
		    }
		    return q;
		}
		// otherwise
		ReadProperty property = new ReadProperty();
		property.setFieldname(record);
		property.setChemicalsOnly(chemicalsOnly);
		return property;

	    } else
		return new ReadProperty(new Integer(o.toString()));
	} catch (NumberFormatException x) {
	    throw new InvalidResourceIDException(o);

	} catch (Exception x) {
	    _dmode = DisplayMode.table;
	    return new ReadProperty();
	} finally {
	}
    }

    protected IQueryRetrieval<Property> getFreeTextQuery(Context context, Request request, Response response)
	    throws ResourceException {
	Form form = request.getResourceRef().getQueryAsForm();
	String[] keys = form.getValuesArray("text");

	ArrayList<String> s = new ArrayList<String>();
	for (String key : keys) {
	    if (key == null)
		continue;
	    String[] k = key.split(" ");
	    for (String kk : k)
		s.add(kk);
	}
	if (s.size() == 0)
	    return null;
	keys = s.toArray(keys);
	if ((keys != null) && (keys.length > 0)) {
	    FreeTextPropertyQuery query = new FreeTextPropertyQuery();
	    query.setFieldname(keys);
	    query.setValue(keys);
	    return query;
	} else
	    return null;
    }

    /**
     * PUT allowed for feature resources only (updates the feature
     * representation)
     */
    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	if (getRequest().getAttributes().get(idfeaturedef) != null)
	    createNewObject(entity);
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	return getResponse().getEntity();
    }

    /**
     * POST allowed to feature collections only (creates new feature)
     */
    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	if (getRequest().getAttributes().get(idfeaturedef) == null)
	    createNewObject(entity);
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	return getResponse().getEntity();
    }

    @Override
    protected RDFObjectIterator<Property> createObjectIterator(Reference reference, MediaType mediaType)
	    throws ResourceException {
	try {
	    RDFPropertyIterator iterator = new RDFPropertyIterator(reference, mediaType) {
		@Override
		protected boolean skip(Resource newEntry) {
		    return super.skip(newEntry) || isFeatureSource(newEntry);
		}
	    };
	    iterator.setBaseReference(getRequest().getRootRef());
	    return iterator;
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(x);
	}
    }

    @Override
    protected RDFObjectIterator<Property> createObjectIterator(Representation entity) throws ResourceException {
	RDFPropertyIterator iterator = new RDFPropertyIterator(entity, entity.getMediaType()) {
	    @Override
	    protected boolean skip(Resource newEntry) {
		return super.skip(newEntry) || isFeatureSource(newEntry);
	    }
	};
	iterator.setForceReadRDFLocalObjects(true);
	iterator.setBaseReference(getRequest().getRootRef());
	return iterator;
    }

    @Override
    protected Property onError(String uri) {
	Property p = new Property(uri, new LiteratureEntry(uri, uri));
	p.setLabel(uri);
	return p;
    }

    @Override
    protected String getObjectURI(Form queryForm) throws ResourceException {
	return getParameter(queryForm, OpenTox.params.feature_uris.toString(),
		OpenTox.params.feature_uris.getDescription(), true);
    }

    @Override
    protected AbstractUpdate createUpdateObject(Property entry) throws ResourceException {
	if (getRequest().getAttributes().get(idfeaturedef) != null)
	    try {
		entry.setId(Integer.parseInt(getRequest().getAttributes().get(idfeaturedef).toString()));
	    } catch (Exception x) {
		entry.setId(-1);
	    }

	if (entry.getReference().getId() > 0)
	    return new CreatePropertyReferenceID(entry);
	else if (entry.getId() > 0)
	    return new UpdateProperty(entry);
	else
	    return new CreateProperty(entry);
    }

    @Override
    protected QueryURIReporter<Property, IQueryRetrieval<Property>> getURUReporter(Request baseReference)
	    throws ResourceException {
	return new PropertyURIReporter(baseReference);
    }

    @Override
    protected void customizeEntry(Property entry, Connection connection) throws ResourceException {

	if (entry.getId() > 0)
	    return;
	QueryExecutor<RetrieveFieldNames> x = new QueryExecutor<RetrieveFieldNames>();
	ResultSet rs = null;
	try {
	    RetrieveFieldNames q = new RetrieveFieldNames();
	    q.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	    q.setFieldname("name");
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
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage(), e);
	} finally {
	    try {
		rs.close();
	    } catch (Exception e) {
	    }
	    try {
		x.close();
	    } catch (Exception e) {
	    }
	}
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	Object o = getRequest().getAttributes().get(idfeaturedef);
	if (o == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	Representation entity = getRequestEntity();

	try {
	    int propertyid = Integer.parseInt(o.toString());
	    Property p = new Property(null);
	    p.setId(propertyid);
	    executeUpdate(entity, p, createDeleteObject(p));
	    return getResponseEntity();
	} catch (NumberFormatException x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid id", x);
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    @Override
    protected AbstractUpdate createDeleteObject(Property entry) throws ResourceException {
	DeleteProperty delete = new DeleteProperty();
	delete.setObject(entry);
	return delete;
    }
}
