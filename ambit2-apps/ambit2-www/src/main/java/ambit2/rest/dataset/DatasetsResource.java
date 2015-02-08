package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;

/**
 * Dataset resource - A set of chemical compounds and assigned features
 * 
 * http://opentox.org/development/wiki/dataset
 * 
 * Supported operations:
 * <ul>
 * <li>GET /dataset  ; returns text/uri-list or text/xml or text/html
 * <li>POST /dataset ; accepts chemical/x-mdl-sdfile or multipart/form-data (SDF,mol, txt, csv, xls,all formats supported in Ambit)
 * <li>GET 	 /dataset/{id}  ; returns text/uri-list or text/xml
 * <li>PUT and DELETE not yet supported
 * </ul>
 * 
 * @author nina 
 *
 */
public class DatasetsResource extends MetadatasetResource<SourceDataset> {
	
	public final static String datasets = "/datasets";	

	protected FileUpload upload;
	protected IStructureKey matcher;
	
	public DatasetsResource() {
		super();
		_dmode = DisplayMode.table;
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "datasets.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		upload = new FileUpload();
		upload.setRequest(getRequest());
		upload.setResponse(getResponse());
		upload.setContext(getContext());
		upload.setApplication(getApplication());
		upload.setDataset(dataset);

		
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		IQueryRetrieval<SourceDataset> query = getQuery(context, request, response,true);
		
		Form form = getResourceRef(request).getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			
			RetrieveDatasets query_by_name = new RetrieveDatasets(null,new SourceDataset(Reference.decode(key.toString())));
			query_by_name.setFieldname(structureParam);
			query_by_name.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
			return query_by_name;
		}
		return query;
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,false,
				getToken()
				);
		
	}
	/**
	 * Creates a dataset, but if a structure is found, import only properties 
	 */
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,true,
				getToken()
				);
	}

	protected void setPaging(Form form, IQueryObject queryObject) {
		String max = form.getFirstValue(max_hits);
		String page = form.getFirstValue(OpenTox.params.page.toString());
		String pageSize = form.getFirstValue(OpenTox.params.pagesize.toString());
		if (max != null)
		try {
			queryObject.setPage(0);
			queryObject.setPageSize(Long.parseLong(form.getFirstValue(max_hits).toString()));
			return;
		} catch (Exception x) {
			
		}
		try {
			queryObject.setPage(Integer.parseInt(page));
		} catch (Exception x) {
			queryObject.setPage(0);
		}
		try {
			queryObject.setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {
			queryObject.setPageSize(50);
		}			
	}

}
