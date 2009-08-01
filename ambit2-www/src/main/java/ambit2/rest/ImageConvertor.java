package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class ImageConvertor<T,Q extends IQueryRetrieval<T>>  extends QueryRepresentationConvertor<T,Q,OutputStream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public ImageConvertor(QueryReporter<T, Q, OutputStream> reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}

	public Representation process(final Q query) throws ambit2.base.exceptions.AmbitException {
		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
	            	try {
	            		getReporter().setOutput(stream);
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
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };		
	};	


}