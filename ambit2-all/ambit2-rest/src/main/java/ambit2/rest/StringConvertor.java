package ambit2.rest;

import java.io.StringWriter;
import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;

import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

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
		this(reporter,mediaType,null);
	}
	public StringConvertor(R reporter,MediaType mediaType,String fileNamePrefix) {
		super(reporter,mediaType,fileNamePrefix);
	}
	@Override
	public Representation process(Q query) throws AmbitException {
		try {
			reporter.setOutput(new StringWriter());
			Writer writer = reporter.process(query);
			writer.flush();
			Representation rep = new StringRepresentation(writer.toString(),getMediaType(),
					Language.ENGLISH,CharacterSet.UTF_8);
			setDisposition(rep);
	
	        return rep;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try { reporter.close(); } catch (Exception x) {}
		}
		
	};

}
