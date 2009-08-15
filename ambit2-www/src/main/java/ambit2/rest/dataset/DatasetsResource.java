package ambit2.rest.dataset;

import java.io.Writer;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.resource.InputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;
import ambit2.rest.task.CallableFileImport;

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
public class DatasetsResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	
	
	public final static String datasets = "/dataset";	
	public final static String datasetKey = "dataset_id";	
	public final static String datasetID =  String.format("%s/{%s}",DatasetsResource.datasets,datasetKey);
	
	protected boolean collapsed;
	public DatasetsResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));		
	}

	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws StatusException {
		ReadDataset query = new ReadDataset();
		
		Object id = request.getAttributes().get(datasetKey);
		collapsed = true;
		if (id != null) try {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(new Integer(Reference.decode(id.toString())));
			query.setValue(dataset);
			collapsed = false;
		} catch (NumberFormatException x) {
			error = new InvalidResourceIDException(id);
			query=null;
		} catch (Exception x) {
			query.setValue(null);
		}
		else {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key != null) {
				RetrieveDatasets query_by_name = new RetrieveDatasets(null,new SourceDataset(Reference.decode(key.toString())));
				query_by_name.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
				return query_by_name;
			} 
		}
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {

	if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputStreamConvertor(
				new DatasetsHTMLReporter(getRequest().getRootRef(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest().getRootRef()) {
			@Override
			public void processItem(SourceDataset dataset, Writer output) {
				super.processItem(dataset, output);
				try {
				output.write('\n');
				} catch (Exception x) {}
			}
		},MediaType.TEXT_URI_LIST);
	} else //html 	
		return new OutputStreamConvertor(
				new DatasetHTMLReporter(getRequest().getRootRef(),collapsed),MediaType.TEXT_HTML);
	}
	
	@Override
	public boolean allowPost() {
		return getRequest().getAttributes().get(datasetKey)==null;
	}
	
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		if ((entity != null) && MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
			  DiskFileItemFactory factory = new DiskFileItemFactory();
              //factory.setSizeThreshold(100);
	          RestletFileUpload upload = new RestletFileUpload(factory);
	          try {
	              List<FileItem> items = upload.parseRequest(getRequest());
				  Reference ref =  ((AmbitApplication)getApplication()).addTask(
						 "File import",
						new CallableFileImport(items,DatasetHTMLReporter.fileUploadField,getConnection()),
						getRequest().getRootRef());		
				  getResponse().setLocationRef(ref);
				  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				  getResponse().setEntity(null);
				  System.out.println(ref);
	          } catch (Exception x) {
	        	  getResponse().setStatus(new Status(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage()));
	        	  getResponse().setEntity(null);
	          }
		} else  {
			if ((entity != null) && (entity.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF))) {
				try {
				  Reference ref =  ((AmbitApplication)getApplication()).addTask(
							 "File import"+entity.getDownloadName(),
							new CallableFileImport((InputRepresentation)entity,getConnection()),
							getRequest().getRootRef());		
					  getResponse().setLocationRef(ref);
					  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
					  getResponse().setEntity(null);
				} catch (Exception x) {
					getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
				}
			} else
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			
		}
		/*
		if (entity != null) 
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
                // 1/ Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1000240);
                // 2/ Create a new file upload handler based on the Restlet
                // FileUpload extension that will parse Restlet requests and
                // generates FileItems.
                RestletFileUpload upload = new RestletFileUpload(factory);
                List<FileItem> items;
                try {
                    // 3/ Request is parsed by the handler which generates a
                    // list of FileItems
                    items = upload.parseRequest(getRequest());

                    // Process only the uploaded item called "fileToUpload" and
                    // save it on disk
                    boolean found = false;
                    for (final Iterator<FileItem> it = items.iterator(); 
                    		it.hasNext()
                            && !found;) {
                        FileItem fi = it.next();
                        if (fi.getFieldName().equals(DatasetsHTMLReporter.fileUploadField)) {
                        	fi.getContentType();
                            found = true;
                            File file = new File(System.getProperty("java.io.tmpdir")+fi.getName());
                            fi.write(file);
                        }
                    }    
                    
                    getResponse().setStatus(Status.REDIRECTION_FOUND);
                    getResponse().setLocationRef(getRequest().getOriginalRef());
                    getResponse().setEntity(null);
                    return;
                 } catch (Exception e) {
                	 throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,e.getMessage()));
                 } finally {
                	 
                 }
            }
         getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
         */
            
	}
}
