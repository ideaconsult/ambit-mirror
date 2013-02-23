package ambit2.i5rest.convertors;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.types.Types.DocumentReference;
import org.ideaconsult.iuclidws.types.Types.QueryParameterList;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.processors.Reporter;
import ambit2.rest.RepresentationConvertor;

/**
 * Retrieves info from QueryEngine, given query parameters
 * @author nina
 *
 * @param <T>
 * @param <R>
 */
public class I5OutputStreamConvertor<R extends Reporter<QueryParameterList,Writer>>  
								extends RepresentationConvertor<DocumentReference,QueryParameterList,Writer,R> {

	private final static Logger LOGGER = Logger.getLogger(I5OutputStreamConvertor.class);
	
	public I5OutputStreamConvertor(R reporter,MediaType media) {
		super(reporter,media);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7226297274978063017L;

	@Override
	public Representation process(final QueryParameterList query) throws AmbitException {

		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
            		OutputStreamWriter writer = null;          	
	            	try {
	            		writer = new OutputStreamWriter(stream,"UTF-8");	  
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
	            		logger.log(Level.WARNING,query.toString(),x);
	            	} finally {
	            		try {if (writer !=null) writer.flush(); } catch (Exception x) { x.printStackTrace();}
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };		
	};	


}
