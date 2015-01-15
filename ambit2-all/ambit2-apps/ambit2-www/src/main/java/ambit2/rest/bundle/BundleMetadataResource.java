package ambit2.rest.bundle;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.MetadataRDFReporter;
import ambit2.rest.dataset.MetadatasetJSONReporter;
import ambit2.rest.dataset.MetadatasetURIReporter;
import ambit2.user.rest.resource.AmbitDBQueryResource;

public class BundleMetadataResource extends
	AmbitDBQueryResource<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle> {
    protected SubstanceEndpointsBundle dataset;

    public BundleMetadataResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "bundles.ftl";
    }

    @Override
    public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
	super.configureTemplateMap(map, request, app);
	if (dataset != null)
	    map.put("datasetid", dataset.getID());
    }

    @Override
    protected IQueryRetrieval<SubstanceEndpointsBundle> createUpdateQuery(Method method, Context context,
	    Request request, Response response) throws ResourceException {
	Object id = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (Method.POST.equals(method)) {
	    if (id == null)
		return null;// post allowed only on /bundle level, not on
			    // /bundle/id
	} else {
	    if (id != null) {
		try {
		    Integer idnum = new Integer(Reference.decode(id.toString()));
		    dataset = new SubstanceEndpointsBundle();
		    dataset.setID(idnum);
		    ReadBundle query = new ReadBundle();
		    query.setValue(dataset);
		    return query;
		} catch (NumberFormatException x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	    }
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected IQueryRetrieval<SubstanceEndpointsBundle> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	ReadBundle query = null;
	Object id = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (id != null)
	    try {
		Integer idnum = new Integer(Reference.decode(id.toString()));
		if (idnum.intValue() <= 0)
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		dataset = new SubstanceEndpointsBundle();
		dataset.setID(idnum);
		query = new ReadBundle();
		query.setValue(dataset);
		return query;
	    } catch (NumberFormatException x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	else
	    return new ReadBundle();
    }

    @Override
    protected CallableProtectedTask<String> createCallable(Method method, Form form, SubstanceEndpointsBundle item)
	    throws ResourceException {
	SubstanceEndpointsBundle bundle = null;
	Object id = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());
	if ((id != null))
	    try {
		Integer i = new Integer(Reference.decode(id.toString()));
		if (i > 0)
		    bundle = new SubstanceEndpointsBundle(i);
	    } catch (Exception x) {
	    }

	Connection conn = null;
	try {
	    DatasetURIReporter r = new DatasetURIReporter(getRequest());
	    DBConnection dbc = new DBConnection(getApplication().getContext(), getConfigFile());
	    conn = dbc.getConnection();
	    return new CallableBundleCreator(bundle, r, method, form, conn, getToken());
	} catch (Exception x) {
	    x.printStackTrace();
	    try {
		conn.close();
	    } catch (Exception xx) {
	    }
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
    }

    @Override
    protected String getObjectURI(Form queryForm) throws ResourceException {
	return null;
    }

    @Override
    protected CallableProtectedTask<String> createCallable(Method method, List<FileItem> input,
	    SubstanceEndpointsBundle item) throws ResourceException {
	return super.createCallable(method, input, item);
    }

    @Override
    public IProcessor<IQueryRetrieval<SubstanceEndpointsBundle>, Representation> createConvertor(Variant variant)
	    throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new OutputWriterConvertor(
		    new MetadatasetJSONReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle>(
			    getRequest()), MediaType.APPLICATION_JSON);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    return new StringConvertor(
		    new DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle>(
			    getRequest()), MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return new RDFJenaConvertor<SubstanceEndpointsBundle, IQueryRetrieval<SubstanceEndpointsBundle>>(
		    new MetadataRDFReporter<SubstanceEndpointsBundle, IQueryRetrieval<SubstanceEndpointsBundle>>(
			    getRequest(), getDocumentation(), variant.getMediaType()), variant.getMediaType(),
		    filenamePrefix);

	} else
	    // default json
	    return new OutputWriterConvertor(
		    new MetadatasetJSONReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle>(
			    getRequest()), MediaType.APPLICATION_JSON);
    }

    @Override
    protected QueryURIReporter<SubstanceEndpointsBundle, IQueryRetrieval<SubstanceEndpointsBundle>> getURIReporter(
	    Request baseReference) throws ResourceException {
	return new MetadatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle>(
		baseReference, getDocumentation());
    }

    @Override
    public String getConfigFile() {
	return "ambit2/rest/config/ambit2.pref";
    }

    @Override
    protected boolean isAllowedMediaType(MediaType mediaType) throws ResourceException {
	return MediaType.MULTIPART_FORM_DATA.equals(mediaType) || MediaType.APPLICATION_WWW_FORM.equals(mediaType);
    }
}
