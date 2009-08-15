package ambit2.rest.pubchem;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.pubchem.EntrezSearchProcessor;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;

/**
 * Retrieves structure in SDF format from pubchem
 * <br>URI /query/pubchem/{query_term}
 * <br>
 * REST Operation: GET  ; returns text/plain or chemical/x-mdl-sdfile
 * @author nina
 *
 */
public class PubchemResource extends Resource {
	public static final String resource = "/query/pubchem";
	public static final String resourceKey = "term";
	public static final String resourceID = String.format("%s/{%s}",resource,resourceKey);
	protected String term = "";
	protected EntrezSearchProcessor entrezQuery ;
	public PubchemResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));		
		
		try {
			this.term = Reference.decode(request.getAttributes().get("term").toString());
		} catch (Exception x) {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key != null) {
				term = Reference.decode(key.toString());
			} else this.term = null;
		}		
		entrezQuery = new EntrezSearchProcessor();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));		
	}
	public Representation getRepresentation(Variant variant) {
		
		try {
	        if (term != null) {
	        	List<IStructureRecord> records = entrezQuery.process(term);
	        	if (records == null) {
	        		getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
	        		return null;
	        	}
	        	if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF))
		        	return new OutputRepresentation(variant.getMediaType()) {
			            @Override
			            public void write(OutputStream stream) throws IOException {
		            		OutputStreamWriter writer = null;          	
			            	try {
			            		writer = new OutputStreamWriter(stream);	  
			            		List<IStructureRecord> records = entrezQuery.process(term);
			            		for (IStructureRecord record:records)
			            			if (record.getFormat().toLowerCase().equals("sdf"))
			            				writer.write(record.getContent());
			            		writer.flush();
			            		stream.flush();
			            	} catch (AmbitException x) {
			            		x.printStackTrace();
			            		//throw new IOException(x.getMessage());
			            	} finally {
			            		try {if (writer !=null) writer.flush(); } catch (Exception x) { x.printStackTrace();}
			            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
			            	}
			            }
			        };	
			     else {
			    	 StringBuilder b = new StringBuilder();
	            		for (IStructureRecord record:records)
	            			if (record.getFormat().toLowerCase().equals("sdf"))
	            				b.append(record.getContent());		
	            		
	            	 return new StringRepresentation(b.toString(),ChemicalMediaType.CHEMICAL_MDLSDF);
			     }
	        } else {
	        	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
					variant.setMediaType(MediaType.TEXT_HTML);
					StringWriter writer = new StringWriter();
					AmbitResource.writeHTMLHeader(writer, "AMBIT", getRequest().getRootRef());
					writer.write("<h2>PubChem search</h2>");
					writer.write("<p>Enter query term and click <b>Search</b> button. The result will be an SDF file.<p>Examples:<p>");
					writer.write(String.format("<a href=\"%s/query/pubchem/50-00-0\">%s/query/pubchem/50-00-0</a><br>",getRequest().getRootRef(),getRequest().getRootRef()));
					writer.write(String.format("<a href=\"%s/query/pubchem?search=50-00-0\">%s/query/pubchem?search=50-00-0</a><br>",getRequest().getRootRef(),getRequest().getRootRef()));
					AmbitResource.writeHTMLFooter(writer, "AMBIT", getRequest().getRootRef());
					return new StringRepresentation(writer.toString(),MediaType.TEXT_HTML);				
	        	} else {      	
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Undefined query");
		        	return null;	        	
	        	}
	        }
		} catch (NotFoundException x) {

			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,"No results for query "+term);
			return null;	
        
		} catch (Exception x) {

			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}		
			
}
