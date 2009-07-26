package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.HTMLReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.structure.CompoundURIReporter;

public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>>  
									extends QueryResource<Q,IStructureRecord> {

	protected String media = null;
	public StructureQueryResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));	
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_SMILES));
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_CML));			
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));		
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
		this.getVariants().add(new Variant(MediaType.APPLICATION_PDF));		
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {

		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);				
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID>(
					new PDFReporter<QueryStructureByID>());				
		} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			return new StringConvertor<IStructureRecord, QueryStructureByID>(
					new SmilesReporter<QueryStructureByID>(),MediaType.TEXT_PLAIN);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundURIReporter<QueryStructureByID>(getRequest().getRootRef()),MediaType.TEXT_URI_LIST);			
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
				return new StringConvertor(new HTMLReporter<Q>());	
		} else
			return new DocumentConvertor(new QueryXMLReporter<Q>(getRequest()==null?null:getRequest().getRootRef()));
	}
	
	protected String getMediaParameter(Request request) {
		try {
			Object m = request.getAttributes().get("media");
			return m==null?null:Reference.decode(m.toString());
		} catch (Exception x) {
			return null;
		}	
	}
}
