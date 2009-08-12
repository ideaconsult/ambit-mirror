package ambit2.rest.structure;

import java.awt.Dimension;
import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.AbstractReporter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
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
public class CompoundResource extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String compound = "/compound";
	public final static String idcompound = "idcompound";
	public final static String compoundID = String.format("%s/{%s}",compound,idcompound);
	public final static String compoundID_media = String.format("%s%s",compoundID,"/diagram/{media}");	
	protected boolean collapsed = false;
	public CompoundResource(Context context, Request request, Response response) {
		super(context,request,response);
	
	}
	
	@Override
	public String[] URI_to_handle() {
		return new String[] {compoundID,compoundID_media};
	}
	@Override
	public Representation getRepresentation(Variant variant) {
		if (query == null) try {
			IProcessor<Object, Representation>  convertor = createConvertor(variant);
			Representation r = convertor.process(null);
        	return r;			
		} catch (Exception x) { getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x); return null;}
		else return super.getRepresentation(variant);
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (query == null) {
			if (variant.getMediaType().equals(MediaType.TEXT_HTML)) 
				return new StringConvertor(new AbstractReporter<Object,Writer>() {
					
					public Writer process(Object target) throws AmbitException {
						try {
							AmbitResource.writeHTMLHeader(getOutput(), "Compound", getRequest().getRootRef());
							getOutput().write("<div>No search criteria specified</div>");
							AmbitResource.writeHTMLFooter(getOutput(), "Compound", getRequest().getRootRef());
						} catch (Exception x) {}
						return getOutput();
					}
					public void close() throws Exception {}

				},MediaType.TEXT_HTML);
			else throw new NotFoundException();
		}
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
			Dimension d = new Dimension(250,250);
			Form form = getRequest().getResourceRef().getQueryAsForm();
			try {
				
				d.width = Integer.parseInt(form.getFirstValue("w").toString());
			} catch (Exception x) {}
			try {
				d.height = Integer.parseInt(form.getFirstValue("h").toString());
			} catch (Exception x) {}			
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(d),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID,PDFReporter<QueryStructureByID>>(
					new PDFReporter<QueryStructureByID>());				
	
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new QueryXMLReporter((getRequest()==null)?null:getRequest().getRootRef()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter(
							(getRequest()==null)?null:getRequest().getRootRef(),
							collapsed,
							getURIReporter()),
					MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(
					getURIReporter(),MediaType.TEXT_URI_LIST);
						
		} else
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);			
	}
	protected QueryURIReporter getURIReporter() {
		return new CompoundURIReporter<QueryStructureByID>(getRequest().getRootRef());
	}
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws StatusException {
		media = getMediaParameter(request);
		try {
			Object key = request.getAttributes().get(idcompound);
			if (key==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				key = form.getFirstValue("search");
				if (key != null) {
					collapsed = true;
					QueryField q_by_name =  new QueryField();
			        try {
			        	q_by_name.setValue(Reference.decode(key.toString()));
			        } catch (Exception x) {
			        	throw new AmbitException(x);
			        }			
			        StringCondition condition = StringCondition.getInstance(StringCondition.C_LIKE);
			        try {
			        	condition = StringCondition.getInstance(Reference.decode(request.getAttributes().get("condition").toString()));
			        } catch (Exception x) {
			        	condition = StringCondition.getInstance(StringCondition.C_LIKE);
			        } finally {
			        	q_by_name.setCondition(condition);
			        }
			        return q_by_name;
				} else return null;			
			} else {
				IStructureRecord record = new StructureRecord();
				record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
				QueryStructureByID query = new QueryStructureByID();
				query.setMaxRecords(1);
				query.setChemicalsOnly(true);
				query.setValue(record);
				return query;
			}
		} catch (NumberFormatException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",request.getAttributes().get(idcompound)))
					);
		} catch (Exception x) {
			throw new StatusException(
					new Status(Status.SERVER_ERROR_INTERNAL,x,x.getMessage())
					);
		}
		

	}	
}
