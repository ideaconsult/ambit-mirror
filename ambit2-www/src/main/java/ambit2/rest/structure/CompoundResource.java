package ambit2.rest.structure;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.QueryReporter;
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
import ambit2.rest.query.QueryXMLReporter;
import ambit2.rest.query.StructureQueryResource;

/**
	/{datasetid}/compound/{idchemical}
 * Supports Content-Type:
<pre>
application/pdf
text/xml
chemical/x-cml
chemical/x-mdl-molfile
chemical/x-mdl-sdfile
chemical/x-daylight-smiles
image/png
</pre>
 * @author nina
 */
public class CompoundResource extends StructureQueryResource<QueryStructureByID> {
	public final static String compound = "/compound";
	public final static String idcompound = "idcompound";
	public final static String compoundID = String.format("%s/{%s}",compound,idcompound);
	public final static String compoundID_media = String.format("%s%s",compoundID,"/{media}");	
	
	public CompoundResource(Context context, Request request, Response response) {
		super(context,request,response);
	
	}
	
	@Override
	public String[] URI_to_handle() {
		return new String[] {compoundID,compoundID_media};
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {

		if ("png".equals(media)) variant.setMediaType(MediaType.IMAGE_PNG);
		if ("pdf".equals(media)) variant.setMediaType(MediaType.APPLICATION_PDF);
		if ("sdf".equals(media)) variant.setMediaType(ChemicalMediaType.CHEMICAL_MDLSDF);
		if ("cml".equals(media)) variant.setMediaType(ChemicalMediaType.CHEMICAL_CML);
		if ("smiles".equals(media)) variant.setMediaType(ChemicalMediaType.CHEMICAL_SMILES);
		if ("xml".equals(media)) variant.setMediaType(MediaType.TEXT_XML);
		if ("html".equals(media)) variant.setMediaType(MediaType.TEXT_HTML);
		if ("uri".equals(media)) variant.setMediaType(MediaType.TEXT_URI_LIST);
		
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) 
			//return new DocumentConvertor<IStructureRecord, QueryStructureByID>(new StructureReporter((getRequest()==null)?null:getRequest().getRootRef()));
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);	
		else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID>(
					new PDFReporter<QueryStructureByID>());				
	
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor<IStructureRecord, QueryStructureByID>(new QueryXMLReporter((getRequest()==null)?null:getRequest().getRootRef()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter((getRequest()==null)?null:getRequest().getRootRef()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor<IStructureRecord, QueryStructureByID>(
					getURIReporter(),MediaType.TEXT_URI_LIST);
						
		} else
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);			
	}
	protected QueryReporter getURIReporter() {
		return new CompoundURIReporter<QueryStructureByID>(getRequest().getRootRef());
	}

	@Override
	protected QueryStructureByID createQuery(Context context, Request request,
			Response response) throws AmbitException {
		media = getMediaParameter(request);
		try {
//			System.out.println(request.getAttributes().get("org.restlet.http.headers"));
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(idcompound).toString())));
			QueryStructureByID query = new QueryStructureByID();
			query.setMaxRecords(1);
			query.setChemicalsOnly(true);
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		

	}	
}
