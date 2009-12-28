package ambit2.rest.structure;

import java.awt.Dimension;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.ARFFReporter;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.QueryXMLReporter;
import ambit2.rest.query.StructureQueryResource;

/**
 * Chemical compound resource as in http://opentox.org/development/wiki/structure
 * REST Operations:
 * <ul>
 * <li>GET 	 /compound/{id}
 * </li>
 * </ul>
 *Content-Type:
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
	
	public final static String compound = OpenTox.URI.compound.getURI();
	public final static String idcompound = OpenTox.URI.compound.getKey();
	public final static String compoundID = OpenTox.URI.compound.getResourceID();
	
	protected boolean collapsed = false;
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (queryObject == null) try {
			IProcessor<Object, Representation>  convertor = createConvertor(variant);
			Representation r = convertor.process(null);
        	return r;			
		} catch (Exception x) { getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x); return null;}
		else return super.get(variant);
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		if (!variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			setTemplate(createTemplate(getContext(),getRequest(),getResponse()));
		}
		Form acceptform = getRequest().getResourceRef().getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(media));
		}		
		
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) 
			//return new DocumentConvertor<IStructureRecord, QueryStructureByID>(new StructureReporter((getRequest()==null)?null:getRequest().getRootRef()));
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);
		else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(getTemplate()),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLMOL)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(new Template(),true),ChemicalMediaType.CHEMICAL_MDLMOL);				
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG) ||
				variant.getMediaType().equals(MediaType.IMAGE_BMP) ||
				variant.getMediaType().equals(MediaType.IMAGE_JPEG) ||
				variant.getMediaType().equals(MediaType.IMAGE_TIFF) ||
				variant.getMediaType().equals(MediaType.IMAGE_GIF) 
				) {
			Dimension d = new Dimension(250,250);
			Form form = getRequest().getResourceRef().getQueryAsForm();
			try {
				
				d.width = Integer.parseInt(form.getFirstValue("w").toString());
			} catch (Exception x) {}
			try {
				d.height = Integer.parseInt(form.getFirstValue("h").toString());
			} catch (Exception x) {}			
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(d),variant.getMediaType());	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID,PDFReporter<QueryStructureByID>>(
					new PDFReporter<QueryStructureByID>(getTemplate()));				
	
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new QueryXMLReporter(getRequest()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter(
							getRequest(),
							collapsed,
							getURIReporter(),
							getTemplate()),
					MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFReporter(getTemplate()),ChemicalMediaType.WEKA_ARFF);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CSVReporter(getTemplate()),MediaType.TEXT_CSV);				
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) 
				) {
			return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
					new DatasetRDFReporter(getRequest(),variant.getMediaType(),getTemplate()),variant.getMediaType());			
		} else
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(getTemplate()),ChemicalMediaType.CHEMICAL_MDLSDF);			
		
	}
	protected QueryURIReporter getURIReporter() {
		return new CompoundURIReporter<QueryStructureByID>(getRequest());
	}
	protected IQueryRetrieval<IStructureRecord> createSingleQuery(String property,String cond,String key,boolean chemicalsOnly) {
		AbstractStructureQuery query;
		
        try {
        	key = Reference.decode(key.toString().trim());
        	String[] keys = key.toString().split(" .. ");
        	Double d1=null;
        	Double d2=null;
        	NumberCondition condition = NumberCondition.getInstance(NumberCondition.between);
        	if (keys.length==2) try {
        		d1 = Double.parseDouble(keys[0].toString()); //number
        		d2 = Double.parseDouble(keys[1].toString()); //number
        	} catch (Exception x) {
        		d1 = Double.parseDouble(key.toString()); 
        	}
        	else {
        		d1 = Double.parseDouble(key.toString());
        	}

        	QueryFieldNumeric q = new QueryFieldNumeric();
        	//q.setChemicalsOnly(true);
        	q.setChemicalsOnly(chemicalsOnly);
        	if (property != null) q.setFieldname(new Property(property,null));
	        try {
	        	if ((cond==null)||"".equals(cond.trim())) throw new Exception("Undefined condition");
	        	condition = NumberCondition.getInstance(cond);
	        	q.setValue(d1);
	        	q.setMaxValue(d2==null?(d1+1E-10):d2);
	        } catch (Exception x) {
	        	condition = NumberCondition.getInstance(NumberCondition.between);
	        	q.setValue(d1);
	        	q.setMaxValue(d2==null?(d1+1E-10):d2);
	        } finally {
	        	q.setCondition(condition);
	        }
	        query= q;
        } catch (Exception x) {
        	QueryField q_by_name =  new QueryField();
        	if (property != null) q_by_name.setFieldname(new Property(String.format("%s%%",property),null));
        	q_by_name.setNameCondition(StringCondition.getInstance(StringCondition.C_LIKE));
        	q_by_name.setChemicalsOnly(chemicalsOnly);
        	//q_by_name.setChemicalsOnly(true);
	        StringCondition condition = StringCondition.getInstance(StringCondition.C_LIKE);
	        try {
	        	condition = (cond==null)||("".equals(cond))?StringCondition.getInstance(StringCondition.C_LIKE):StringCondition.getInstance(cond);
	        } catch (Exception xx) {
	        	condition = StringCondition.getInstance(StringCondition.C_LIKE);
	        } finally {
	        	q_by_name.setCondition(condition);
	        	q_by_name.setValue(String.format("%s%s",Reference.decode(key.toString()),condition.toString().equals(StringCondition.C_LIKE)?"%":""));
	        }
	        query = q_by_name;
        }					
        return query;
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		media = getMediaParameter(request);
		try {
			
			Object key = request.getAttributes().get(OpenTox.URI.compound.getKey());
			if (key==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				String[] keys = form.getValuesArray(QueryResource.search_param);
				String[] properties = form.getValuesArray("property");
				String[] condition = form.getValuesArray("condition");
				if (keys != null) {
					collapsed = true;
					/*
					QueryCombinedStructure qcombined = new QueryCombinedStructure();
					qcombined.setCombine_as_and(true);
					qcombined.setChemicalsOnly(true);

					IQueryRetrieval<IStructureRecord>  query = qcombined;
										*/
					IQueryRetrieval<IStructureRecord>  query = null;
					for (int i=0; i < keys.length; i++) {
						String theKey = Reference.decode(keys[i].trim());
						String property = null;
						try {
							property = ((properties==null)||(i>=properties.length)||(properties[i]==null))?"":Reference.decode(properties[i].trim());
						} catch (Exception x) {
							
							property = null;
						}
						IQueryRetrieval<IStructureRecord> q =  createSingleQuery(property,
								((condition==null)||(i>=condition.length)||(condition[i]==null))?"":condition[i], 
								theKey,
								true);
								//keys.length==1);
						query=q;
						break;
						/*
						if (keys.length>1) qcombined.add(q);
						else query = q;
						*/
					}
			        return query;
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
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %s",
							request.getAttributes().get(OpenTox.URI.compound.getKey())==null?"":request.getAttributes().get(OpenTox.URI.compound.getKey())),
					x
					);
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x)
					;
		}
		

	}	

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {

		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		Form form = new Form(entity);
		form.getFirstValue("compound[]");
		try {
			IQueryRetrieval<IStructureRecord> q = createQuery(getContext(),getRequest(),getResponse());
			//ProcessorCreateQuery
			
		} catch (Exception x) {
			
		}		
		throw new ResourceException(new Status(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Not implemented yet!"));
	}
}
