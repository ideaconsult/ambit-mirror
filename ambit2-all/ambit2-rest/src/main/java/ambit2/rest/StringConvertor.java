package ambit2.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.Reporter;

/**
 * Converts query results to string
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class StringConvertor<T,Q, R extends Reporter<Q,Writer> >  extends RepresentationConvertor<T,Q,Writer,R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	

	public StringConvertor(R reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}
	@Override
	public Representation process(Q query) throws AmbitException {
		try {
			reporter.setOutput(new StringWriter());
			Writer writer = reporter.process(query);
			writer.flush();
			return new StringRepresentation(writer.toString(),getMediaType(),
					Language.ENGLISH,CharacterSet.UTF_8);
		} catch (IOException x) {
			throw new AmbitException(x);
		} finally {
			try { reporter.close(); } catch (Exception x) {}
		}
		
	};

}
