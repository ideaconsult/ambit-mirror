package ambit2.rest.dataset;

import org.opentox.aa.OTAAParams;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.ResourceDoc;
import ambit2.rest.StringConvertor;
import ambit2.rest.YAMLConvertor;
import ambit2.rest.error.InvalidResourceIDException;
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
public class DatasetsResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	
	protected SourceDataset dataset;
	public final static String datasets = "/datasets";	
	public final static String metadata = "/metadata";	
	protected FileUpload upload;
	protected IStructureKey matcher;

	//public final static String datasetID =  String.format("%s/{%s}",DatasetsResource.datasets,datasetKey);
	
	protected boolean collapsed;

	public DatasetsResource() {
		super();
		setDocumentation(new ResourceDoc("dataset","Dataset"));
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,ChemicalMediaType.TEXT_YAML,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_MDLMOL,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT});
		upload = new FileUpload();
		upload.setRequest(getRequest());
		upload.setResponse(getResponse());
		upload.setContext(getContext());
		upload.setApplication(getApplication());
		upload.setDataset(dataset);
		

		/*
				new ClassHolder[] {
						new ClassHolder("ambit2.core.processors.structure.key.CASKey","CAS registry number","",""),
						new ClassHolder("ambit2.core.processors.structure.key.EINECSKey","EINECS registry number","",""),
						new ClassHolder("ambit2.core.processors.structure.key.PubchemCID","PubChem Compound ID (PUBCHEM_COMPOUND_CID)","",""),
						new ClassHolder("ambit2.core.processors.structure.key.DSSToxCID","DSSTox Chemical ID DSSTox_CID) number uniquely assigned to a particular STRUCTURE across all DSSTox files","",""),
						new ClassHolder("ambit2.core.processors.structure.key.DSSToxRID","DSSTox Record ID (DSSTox_RID) is number uniquely assigned to each DSSTox record across all DSSTox files","",""),						
						new ClassHolder("ambit2.core.processors.structure.key.InchiPropertyKey","InChi","",""),
						new ClassHolder("ambit2.core.processors.structure.key.SmilesKey","SMILES","",""),
						//new ClassHolder("ambit2.core.processors.structure.key.PropertyKey","Other property - to be defined","",""),
				},"Match chemical compounds from file and the database by:"
				);
		 */
		
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {

		ReadDataset query = new ReadDataset();
		query.setValue(null);
		collapsed = false;
		
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			dataset = new SourceDataset();
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

	if (variant.getMediaType().equals(ChemicalMediaType.TEXT_YAML)) {
			return new YAMLConvertor(new DatasetYamlReporter(getRequest(),getDocumentation()),ChemicalMediaType.TEXT_YAML);			
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed,getDocumentation()),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest(),getDocumentation()) {
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
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) ||
			variant.getMediaType().equals(MediaType.APPLICATION_JSON)
			) {
		return new RDFJenaConvertor<SourceDataset, IQueryRetrieval<SourceDataset>>(
				new MetadataRDFReporter<IQueryRetrieval<SourceDataset>>(getRequest(),
						getDocumentation(),variant.getMediaType()),variant.getMediaType());			

		
	} else //html 	
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed,getDocumentation()),MediaType.TEXT_HTML);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,false,
				getUserToken(OTAAParams.subjectid.toString())
				);
		
	}
	/**
	 * Creates a dataset, but if a structure is found, import only properties 
	 */
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,true,
				getUserToken(OTAAParams.subjectid.toString())
				);
	}



}
