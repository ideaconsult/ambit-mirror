package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class OutputStreamConvertor<T,Q extends IQueryRetrieval<T>>  extends RepresentationConvertor<T,Q,Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public OutputStreamConvertor(QueryReporter<T, Q, Writer> reporter,MediaType mediaType) {
		super(reporter);
		this.mediaType = mediaType;
	}

	public Representation process(final Q query) throws ambit2.base.exceptions.AmbitException {
		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
            		OutputStreamWriter writer = null;          	
	            	try {
	            		writer = new OutputStreamWriter(stream);	  
	            		getReporter().setOutput(writer);
	            		getReporter().process(query);
	            		//writer.flush();
	            		//stream.flush();
	            	} catch (AmbitException x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		x.printStackTrace();
	            	} finally {
	            		try {if (writer !=null) writer.flush(); } catch (Exception x) { x.printStackTrace();}
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };		
	};	


}
