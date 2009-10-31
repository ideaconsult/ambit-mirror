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
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.AbstractReporter;
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
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
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
	public final static String compound = "/compound";
	public final static String idcompound = "idcompound";
	public final static String compoundID = String.format("%s/{%s}",compound,idcompound);

	protected boolean collapsed = false;

	@Override
	public Representation get(Variant variant) {
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
		Form acceptform = getRequest().getResourceRef().getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(Reference.decode(media)));
		}		
		if (queryObject == null) {
			if (variant.getMediaType().equals(MediaType.TEXT_HTML)) 
				return new StringConvertor(new AbstractReporter<Object,Writer>() {
					
					public Writer process(Object target) throws AmbitException {
						try {
							AmbitResource.writeHTMLHeader(output, "Compound", getRequest());
							output.write("<div>No search criteria specified</div>");
							AmbitResource.writeHTMLFooter(output, "Compound", getRequest());
						} catch (Exception x) {}
						return output;
					}
					public void close() throws Exception {}

				},MediaType.TEXT_HTML);
			else throw new NotFoundException();
		}
		
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) 
			//return new DocumentConvertor<IStructureRecord, QueryStructureByID>(new StructureReporter((getRequest()==null)?null:getRequest().getRootRef()));
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);	
		else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(getTemplate()),ChemicalMediaType.CHEMICAL_MDLSDF);
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
					new PDFReporter<QueryStructureByID>(getTemplate()));				
	
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new QueryXMLReporter(getRequest()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
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
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFReporter(getTemplate()),ChemicalMediaType.WEKA_ARFF);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CSVReporter(getTemplate()),MediaType.TEXT_CSV);				
		} else
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(getTemplate()),ChemicalMediaType.CHEMICAL_MDLSDF);			
	}
	protected QueryURIReporter getURIReporter() {
		return new CompoundURIReporter<QueryStructureByID>(getRequest());
	}
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		media = getMediaParameter(request);
		try {
			setTemplate(createTemplate(context, request, response));
			Object key = request.getAttributes().get(idcompound);
			if (key==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				key = form.getFirstValue("search");
				String property = form.getFirstValue("property");
				if (property!=null) property = Reference.decode(property.trim());
				
				if (key != null) {
					collapsed = true;
					
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
			        	q.setChemicalsOnly(true);
			        	if (property != null) q.setFieldname(new Property(property,null));
				        try {
				        	condition = NumberCondition.getInstance(Reference.decode(request.getAttributes().get("condition").toString()));
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
			        	if (property != null) q_by_name.setFieldname(new Property(property,null));
			        	q_by_name.setValue(Reference.decode(key.toString()));
			        	q_by_name.setChemicalsOnly(true);
				        StringCondition condition = StringCondition.getInstance(StringCondition.C_LIKE);
				        try {
				        	condition = StringCondition.getInstance(Reference.decode(request.getAttributes().get("condition").toString()));
				        } catch (Exception xx) {
				        	condition = StringCondition.getInstance(StringCondition.C_LIKE);
				        } finally {
				        	q_by_name.setCondition(condition);
				        }
				        query = q_by_name;
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
					String.format("Invalid resource id %d",request.getAttributes().get(idcompound)),
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
			System.out.println(q);
		} catch (Exception x) {
			
		}		
		throw new ResourceException(new Status(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Not implemented yet!"));
	}
}
