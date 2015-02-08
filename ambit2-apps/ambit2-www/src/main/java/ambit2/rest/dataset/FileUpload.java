package ambit2.rest.dataset;

import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.structure.ConformerURIReporter;
import ambit2.rest.task.CallableFileImport;
import ambit2.rest.task.CallableQueryResultsCreator;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.TaskResult;

public class FileUpload<USERID> {
    protected Request request;
    protected Application application;
    protected SourceDataset dataset;
    protected boolean firstCompoundOnly = false;

    public boolean isFirstCompoundOnly() {
	return firstCompoundOnly;
    }

    public void setFirstCompoundOnly(boolean firstCompoundOnly) {
	this.firstCompoundOnly = firstCompoundOnly;
    }

    public SourceDataset getDataset() {
	return dataset;
    }

    public void setDataset(SourceDataset dataset) {
	this.dataset = dataset;
    }

    public Application getApplication() {
	return application;
    }

    public void setApplication(Application application) {
	this.application = application;
    }

    public Request getRequest() {
	return request;
    }

    public void setRequest(Request request) {
	this.request = request;
    }

    protected Response response;
    protected Context context;

    public Context getContext() {
	return context;
    }

    public void setContext(Context context) {
	this.context = context;
    }

    public Response getResponse() {
	return response;
    }

    public void setResponse(Response response) {
	this.response = response;
    }

    /**
     * Creates new entry in query table and adds structures into query_results
     */
    protected Representation copyDatasetToQueryResultsTable(Form form, USERID token, boolean clearPreviousContent)
	    throws ResourceException {
	CallableQueryResultsCreator callable = new CallableQueryResultsCreator(form, getRequest().getRootRef(),
		getContext(), null, token);
	callable.setClearPreviousContent(clearPreviousContent);
	try {
	    getResponse().setLocationRef(callable.call().getReference());
	    if (token != null) {
		PolicyProtectedTask task = new PolicyProtectedTask(token.toString(), true);
		task.setUri(new TaskResult(getResponse().getLocationRef().toString(), true));
		task.setPolicy();
	    }
	    getResponse().setStatus(Status.SUCCESS_OK);
	    return new StringRepresentation(getResponse().getLocationRef().toString(), MediaType.TEXT_URI_LIST);

	} catch (Exception x) {
	    throw new ResourceException(x);
	}

    }

    public Representation upload(Representation entity, Variant variant, boolean newEntry, boolean propertyOnly,
	    USERID token) throws ResourceException {

	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
	    boolean clearPreviousContent = !propertyOnly; // propertyOnly POST:
							  // false, PUT : true
	    return copyDatasetToQueryResultsTable(new Form(entity), token, clearPreviousContent);
	} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
	    DiskFileItemFactory factory = new DiskFileItemFactory();
	    // factory.setSizeThreshold(100);
	    RestletFileUpload upload = new RestletFileUpload(factory);
	    Connection connection = null;
	    try {
		DBConnection dbc = new DBConnection(getApplication().getContext());
		connection = dbc.getConnection();
		List<FileItem> items = upload.parseRequest(getRequest());
		DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter = new DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset>(
			getRequest());

		ConformerURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(
			getRequest());

		CallableFileImport callable = new CallableFileImport(getRequest().getClientInfo(), dataset, items,
			DatasetsHTMLReporter.fileUploadField, connection, reporter, compoundReporter,
			firstCompoundOnly, token);

		callable.setPropertyOnly(propertyOnly);

		ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("File import", callable,
			getRequest().getRootRef(), token);

		ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
		task.update();
		getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	    } catch (Exception x) {
		try {
		    connection.close();
		} catch (Exception xx) {
		    xx.printStackTrace();
		}
		throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	    }
	} else if (isAllowedMediaType(entity.getMediaType())) {
	    Connection connection = null;
	    try {
		DBConnection dbc = new DBConnection(getApplication().getContext());
		connection = dbc.getConnection();
		DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter = new DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset>(
			getRequest());
		ConformerURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(
			getRequest());

		CallableFileImport callable = new CallableFileImport(getRequest().getClientInfo(), dataset, entity,
			connection, reporter, compoundReporter, firstCompoundOnly, token);

		callable.setPropertyOnly(propertyOnly);
		ITask<ITaskResult, String> task = ((AmbitApplication) getApplication())
			.addTask(

			String.format("File import %s [%d]", entity.getDownloadName() == null ? entity.getMediaType()
				: entity.getDownloadName(), entity.getSize()), callable, getRequest().getRootRef(),
				token == null ? null : token.toString());
		ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
		task.update();
		getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	    } catch (Exception x) {
		try {
		    connection.close();
		} catch (Exception xx) {
		    xx.printStackTrace();
		}
		throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);

	    }
	} else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Unsupported Content-type=%s",
		    entity.getMediaType()));

    }

    protected boolean isAllowedMediaType(MediaType mediaType) {
	return ChemicalMediaType.CHEMICAL_MDLSDF.equals(mediaType) || MediaType.APPLICATION_RDF_XML.equals(mediaType)
		|| MediaType.APPLICATION_RDF_TURTLE.equals(mediaType) || MediaType.TEXT_RDF_N3.equals(mediaType)
		|| MediaType.TEXT_CSV.equals(mediaType) || ChemicalMediaType.CHEMICAL_SMILES.equals(mediaType)
		|| ChemicalMediaType.CHEMICAL_MDLMOL.equals(mediaType)
		|| ChemicalMediaType.CHEMICAL_CML.equals(mediaType) || ChemicalMediaType.NANO_CML.equals(mediaType);
    }

}
