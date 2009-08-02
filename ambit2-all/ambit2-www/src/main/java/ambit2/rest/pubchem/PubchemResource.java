package ambit2.rest.pubchem;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.restlet.Context;
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
import ambit2.rest.ChemicalMediaType;

public class PubchemResource extends Resource {
	protected String term = "";
	protected EntrezSearchProcessor entrezQuery ;
	public PubchemResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));		
		
		try {
			this.term = Reference.decode(request.getAttributes().get("term").toString());
		} catch (Exception x) {
			this.term = null;
		}		
		entrezQuery = new EntrezSearchProcessor();
	}
	public Representation getRepresentation(Variant variant) {
		
		try {
	        if (term != null) {
	        	List<IStructureRecord> records = entrezQuery.process(term);
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
	            		
	            	 return new StringRepresentation(b.toString());
			     }
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Undefined query");
	        	return null;	        	
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
