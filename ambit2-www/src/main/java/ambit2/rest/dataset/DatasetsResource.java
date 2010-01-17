package ambit2.rest.dataset;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.YAMLConvertor;
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
	
	
	public final static String datasets = "/datasets";	
	public final static String metadata = "/metadata";	

	//public final static String datasetID =  String.format("%s/{%s}",DatasetsResource.datasets,datasetKey);
	
	protected boolean collapsed;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,MediaType.TEXT_XML,MediaType.TEXT_URI_LIST,ChemicalMediaType.TEXT_YAML,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_MDLMOL,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT});
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {

		ReadDataset query = new ReadDataset();
		query.setValue(null);
		collapsed = false;
		
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(new Integer(Reference.decode(id.toString())));
			query.setValue(dataset);
		} catch (NumberFormatException x) {
			throw new InvalidResourceIDException(id);
		} catch (Exception x) {
			throw new InvalidResourceIDException(id);
		}
		
		Form form = request.getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			RetrieveDatasets query_by_name = new RetrieveDatasets(null,new SourceDataset(Reference.decode(key.toString())));
			query_by_name.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
			return query_by_name;
		}
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {

	if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		return new DocumentConvertor(new DatasetsXMLReporter(getRequest()));	
	} else if (variant.getMediaType().equals(ChemicalMediaType.TEXT_YAML)) {
			return new YAMLConvertor(new DatasetYamlReporter(getRequest()),ChemicalMediaType.TEXT_YAML);			
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest()) {
			@Override
			public Object processItem(SourceDataset dataset) throws AmbitException  {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)
			) {
		return new RDFJenaConvertor<SourceDataset, IQueryRetrieval<SourceDataset>>(
				new MetadataRDFReporter<IQueryRetrieval<SourceDataset>>(getRequest(),variant.getMediaType()),variant.getMediaType());			

		
	} else //html 	
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (entity == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
			  DiskFileItemFactory factory = new DiskFileItemFactory();
              //factory.setSizeThreshold(100);
	          RestletFileUpload upload = new RestletFileUpload(factory);
	          Connection connection = null;
	          try {
					DBConnection dbc = new DBConnection(getApplication().getContext());
					connection = dbc.getConnection(getRequest());	
	              List<FileItem> items = upload.parseRequest(getRequest());
	              DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter = 
	            	  	new DatasetURIReporter<IQueryRetrieval<SourceDataset>> (getRequest());
				  Reference ref =  ((AmbitApplication)getApplication()).addTask(
						 "File import",
						new CallableFileImport(items,DatasetsHTMLReporter.fileUploadField,connection,reporter),
						getRequest().getRootRef());		
				  getResponse().setLocationRef(ref);
				  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				  getResponse().setEntity(null);
				  
	          } catch (Exception x) {
	        	  try { connection.close(); } catch (Exception xx) {}
	        	  throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
	          }
		} else if (isAllowedMediaType(entity.getMediaType())) {
					Connection connection = null;
					try {
						DBConnection dbc = new DBConnection(getApplication().getContext());
						connection = dbc.getConnection(getRequest());						
			          DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter = 
			            	  	new DatasetURIReporter<IQueryRetrieval<SourceDataset>> (getRequest());					
					  Reference ref =  ((AmbitApplication)getApplication()).addTask(
							  
							  	 String.format("File import %s [%d]", entity.getDownloadName()==null?entity.getMediaType():entity.getDownloadName(),entity.getSize()),
								new CallableFileImport((InputRepresentation)entity,connection,reporter),
								getRequest().getRootRef());		
						  getResponse().setLocationRef(ref);
						  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
						  getResponse().setEntity(null);
					} catch (Exception x) {
						try { connection.close(); } catch (Exception xx) {}
 		        	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);

					}
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("Unsupported Content-type=%s",entity.getMediaType()));
	
		return getResponse().getEntity();
            
	}
	
	protected boolean isAllowedMediaType(MediaType mediaType) {
		return 
		ChemicalMediaType.CHEMICAL_MDLSDF.equals(mediaType) ||
		MediaType.APPLICATION_RDF_XML.equals(mediaType) ||
		MediaType.APPLICATION_RDF_TURTLE.equals(mediaType) ||
		MediaType.TEXT_RDF_N3.equals(mediaType) ||
		//MediaType.TEXT_CSV.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_SMILES.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_MDLMOL.equals(mediaType);
		//ChemicalMediaType.CHEMICAL_CML.equals(mediaType) ||
	}
}
