package ambit2.rest.bundle.dataset;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.idea.i5.io.QASettings;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.tools.DownloadTool;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.study.ProtocolEffectRecordMatrix;
import ambit2.base.data.study.Value;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.ids.ReadChemIdentifiersByComposition;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix._matrix;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.substance.CallableBundleMatrixCreator;
import ambit2.rest.substance.CallableStudyBundleImporter;
import ambit2.rest.substance.CallableStudyBundleImporter._mode;
import ambit2.rest.substance.SubstanceDatasetResource;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.AmbitFactoryTaskConvertor;

public class BundleMatrixResource extends SubstanceDatasetResource<ReadSubstancesByBundle> {
    protected SubstanceEndpointsBundle bundle;

    @Override
    public String getTemplateName() {
	return "bundle_matrix.ftl";
    }

    @Override
    protected ReadSubstancesByBundle createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (idbundle == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	try {
	    bundle = new SubstanceEndpointsBundle(Integer.parseInt(idbundle.toString()));
	    return new ReadSubstancesByBundle(bundle) {
		public ambit2.base.data.SubstanceRecord getObject(java.sql.ResultSet rs) throws AmbitException {
		    ambit2.base.data.SubstanceRecord record = super.getObject(rs);
		    record.setProperty(new SubstancePublicName(), record.getPublicName());
		    record.setProperty(new SubstanceName(), record.getCompanyName());
		    record.setProperty(new SubstanceUUID(), record.getCompanyUUID());
		    record.setProperty(new SubstanceOwner(), record.getOwnerName());
		    return record;
		}
	    };
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

    }

    @Override
    protected IQueryRetrieval<ProtocolEffectRecord<String, String, String>> getEffectQuery() {
	_matrix matrix = getList();
	switch (matrix) {
	case matrix_final:
	    return new ReadEffectRecordByBundleMatrix(bundle, _matrix.matrix_final);
	   default: 
	    return new ReadEffectRecordByBundleMatrix(bundle, _matrix.matrix_working);
	}
    }

    @Override
    protected void getCompositionProcessors(ProcessorsChain chain) {
	final ReadSubstanceComposition q = new ReadSubstanceComposition();
	MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition> compositionReader = new MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition>(
		q) {
	    @Override
	    public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
		q.setBundle(bundle);
		if (target.getRelatedStructures() != null)
		    target.getRelatedStructures().clear();
		return super.process(target);
	    }

	    protected SubstanceRecord processDetail(SubstanceRecord target, CompositionRelation detail)
		    throws Exception {
		target.addStructureRelation(detail);
		q.setRecord(null);
		return target;
	    };
	};
	chain.add(compositionReader);
	ReadChemIdentifiersByComposition qids = new ReadChemIdentifiersByComposition();
	MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition> idsReader = new MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition>(
		qids) {
	    @Override
	    protected SubstanceRecord processDetail(SubstanceRecord target, IStructureRecord detail) throws Exception {
		for (CompositionRelation r : target.getRelatedStructures())
		    if (detail.getIdchemical() == r.getSecondStructure().getIdchemical()) {
			for (Property p : detail.getProperties()) {
			    r.getSecondStructure().setProperty(p, detail.getProperty(p));
			}
			break;
		    }
		return target;
	    }

	    @Override
	    public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
		try {
		    return super.process(target);
		} catch (Exception x) {
		    logger.log(Level.FINE, x.getMessage());
		    return target;
		}
	    }
	};
	chain.add(idsReader);
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	
	_matrix matrix = getList();
	switch (matrix) {
	case matrix_working:
	    break;
	default: 
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	String token = getToken();

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
	    CallableBundleMatrixCreator callable = new CallableBundleMatrixCreator(_matrix.matrix_working,Method.DELETE, null, bundle, r,
		    conn, getToken());
	    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Delete matrix from bundle",
		    callable, getRequest().getRootRef(), token);

	    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
	    FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
	    task.update();
	    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
	    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	} catch (Exception x) {
	    x.printStackTrace();
	    try {
		conn.close();
	    } catch (Exception xx) {
	    }
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
    }

    protected _matrix getList() throws ResourceException {
	Object matrixtype = getRequest().getAttributes().get("list");
	if (matrixtype == null) return _matrix.matrix_working; //working matrix by default
	else if ("working".equals(matrixtype.toString())) return _matrix.matrix_working;
	else if ("final".equals(matrixtype.toString())) return _matrix.matrix_final;
	else if ("deleted".equals(matrixtype.toString())) return _matrix.deleted_values;
	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	_matrix matrix = getList();
	switch (matrix) {
	case deleted_values:
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	default:
	    break;
	}
	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");
	if (entity.getMediaType() != null)
	    if (MediaType.APPLICATION_WWW_FORM.getName().equals(entity.getMediaType().getName())) {
		String token = getToken();

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
		    CallableBundleMatrixCreator callable = new CallableBundleMatrixCreator(matrix,Method.POST,
			    new Form(entity), bundle, r, conn, getToken());
		    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Matrix from bundle",
			    callable, getRequest().getRootRef(), token);

		    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		    FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
		    task.update();
		    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

		} catch (Exception x) {
		    x.printStackTrace();
		    try {
			conn.close();
		    } catch (Exception xx) {
		    }
		    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		}
	    }
	throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);

    }

    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	_matrix matrix = getList();
	
	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (entity.getMediaType() != null) {
	    if (MediaType.APPLICATION_JSON.getName().equals(entity.getMediaType().getName())) {
		_mode importmode = _mode.studyimport;
		switch (matrix) {
		case deleted_values: {
		    importmode = _mode.matrixvaluedelete;
		    break;
		} 
		case matrix_working: {
		    importmode = _mode.studyimport;
		    break;
		}
		default: 
		    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		}

		try {
		    File file = File.createTempFile("_matrix_", ".json");
		    file.deleteOnExit();
		    DownloadTool.download(entity.getStream(), file);
		    String token = getToken();
		    QASettings qa = new QASettings();
		    qa.clear(); // sets enabled to false and clears all flags
		    CallableStudyBundleImporter<String> callable = new CallableStudyBundleImporter<String>(importmode,
			    file, getRootRef(), getContext(), new SubstanceURIReporter(getRequest().getRootRef()),
			    new DatasetURIReporter(getRequest().getRootRef()), token);
		    callable.setBundle(bundle);
		    callable.setClearComposition(false);
		    callable.setClearMeasurements(false);
		    callable.setQASettings(qa);
		    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Substance import",
			    callable, getRequest().getRootRef(), token);

		    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		    FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
		    task.update();
		    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);
		} catch (Exception x) {
		    x.printStackTrace();
		} finally {
		    try {
			entity.getStream().close();
		    } catch (Exception xx) {
		    }
		}

	    } else if (MediaType.MULTIPART_FORM_DATA.getName().equals(entity.getMediaType().getName())) {
		switch (matrix) {
		case matrix_working: {
		    break;
		}
		default: 
		    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		}		

		DiskFileItemFactory factory = new DiskFileItemFactory();
		RestletFileUpload upload = new RestletFileUpload(factory);
		try {
		    List<FileItem> items = upload.parseRequest(getRequest());
		    String token = getToken();
		    QASettings qa = new QASettings();
		    qa.clear(); // sets enabled to false and clears all flags
		    boolean clearMeasurements = false;
		    boolean clearComposition = false;
		    for (FileItem file : items) {
			if (file.isFormField()) {
			    // ignore
			} else {
			    String ext = file.getName().toLowerCase();
			    if (ext.endsWith(".json")) {
			    } else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported format "
					+ ext);
			}
		    }
		    CallableStudyBundleImporter<String> callable = new CallableStudyBundleImporter<String>(items,
			    "files[]", getRootRef(), getContext(), new SubstanceURIReporter(getRequest().getRootRef()),
			    new DatasetURIReporter(getRequest().getRootRef()), token);
		    callable.setBundle(bundle);
		    callable.setClearComposition(clearComposition);
		    callable.setClearMeasurements(clearMeasurements);
		    callable.setQASettings(qa);
		    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Substance import",
			    callable, getRequest().getRootRef(), token);

		    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		    FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
		    task.update();
		    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);
		} catch (ResourceException x) {
		    throw x;
		} catch (Exception x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x);
		}
	    }
	}
	throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected Map<String, Object> getMap(Variant variant) throws ResourceException {
	Map<String, Object> map = super.getMap(variant);
	Object idbundle = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());
	try {
	    map.put("bundleid", Integer.toString(Integer.parseInt(idbundle.toString())));
	} catch (Exception x) {
	}
	return map;
    }

    @Override
    protected Value processValue(ProtocolEffectRecord<String, String, String> detail, boolean istextvalue) {
	ValueAnnotated value = new ValueAnnotated();
	if (istextvalue) {
	    if (detail.getTextValue()==null) 
		if (detail.getInterpretationResult()!=null)  value.setTextValue(detail.getInterpretationResult()); 
		else  value.setTextValue(""); 
            else value.setTextValue(detail.getTextValue().toString());     
	} else {
	    value.setLoQualifier(detail.getLoQualifier());
	    value.setUpQualifier(detail.getUpQualifier());
	    value.setUpValue(detail.getUpValue());
	    value.setLoValue(detail.getLoValue());
	}
	value.setIdresult(detail.getIdresult());

	if (detail instanceof ProtocolEffectRecordMatrix)
	    try {
		value.setCopied(((ProtocolEffectRecordMatrix) detail).isCopied());
		value.setDeleted(((ProtocolEffectRecordMatrix) detail).isDeleted());
		value.setRemark(((ProtocolEffectRecordMatrix) detail).getRemarks());
	    } catch (Exception x) {
	    }
	return value;
    }
}
