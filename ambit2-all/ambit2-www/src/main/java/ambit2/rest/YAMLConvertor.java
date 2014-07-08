package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.ho.yaml.YamlEncoder;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.NotFoundException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class YAMLConvertor<T,Q extends IQueryRetrieval<T>>  extends QueryRepresentationConvertor<T,Q,YamlEncoder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	public YAMLConvertor(QueryReporter<T, Q, YamlEncoder> reporter) {
		super(reporter);
	}
	public YAMLConvertor(QueryReporter<T, Q, YamlEncoder> reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}
	public YAMLConvertor(QueryReporter<T, Q, YamlEncoder> reporter,MediaType mediaType,String fileNamePrefix) {
		super(reporter,mediaType,fileNamePrefix);
	}

	public Representation process(final Q query) throws AmbitException {
		Representation rep = new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
            		YamlEncoder writer = null;          	
	            	try {
	            		writer = new YamlEncoder(stream);	  
	            		getReporter().setOutput(writer);
	            		getReporter().process(query);
	            		
	            		//writer.flush();
	            		//stream.flush();
	            	} catch (NotFoundException x) {
	            		;
	            	} catch (Exception x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		Context.getCurrentLogger().warning(x.getMessage());
	            	} finally {
	            		try {if (writer !=null) writer.flush(); writer.close();} catch (Exception x) { x.printStackTrace();}
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };		
			 setDisposition(rep);
			 return rep;
	};	


}