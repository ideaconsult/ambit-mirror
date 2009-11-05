package ambit2.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.xml.sax.SAXException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.reference.AbstractDOMParser;

/**
 * Reads content given an URI and returns objects
 * @author nina
 *
 * @param <Result>
 */
public class RESTClient<Result,Q extends IQueryRetrieval<Result>, Parser extends AbstractDOMParser<Result>> 
									extends DefaultAmbitProcessor<Reference, Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4508593148397503650L;
	protected Parser parser;
	protected QueryDOMReporter<Result, Q> reporter;
	
	public QueryDOMReporter<Result, Q> getReporter() {
		return reporter;
	}
	public void setReporter(QueryDOMReporter<Result, Q> reporter) {
		this.reporter = reporter;
	}
	public RESTClient() {
		this(null,null);
	}
	public RESTClient(Parser parser,QueryDOMReporter<Result, Q> reporter) {
		super();
		this.parser = parser;
		this.reporter = reporter;
	}	
	public Parser getParser() {
		return parser;
	}
	public void setParser(Parser parser) {
		this.parser = parser;
	}
	public Result process(Reference target) throws AmbitException {
		Representation r = null;
		try {
			Logger.getLogger(target.toString());
			ClientResource client = new ClientResource(target);
			

			r = client.get(MediaType.TEXT_XML);
			if (client.getStatus().equals(Status.SUCCESS_OK))
				parser.parse(new InputStreamReader(r.getStream(),"UTF-8"));

			return null; //what should be returned???????
		} catch (NotFoundException x) {
			return null;
		} catch (AmbitException x) {
			throw x;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		} catch (IOException x) {
			throw new AmbitException(x);
		} catch (SAXException x) { 		
			throw new AmbitException(x);
		} catch (Exception x) {
			throw new AmbitException(x);

		} finally {
			
			try {if (r != null) r.getStream().close(); } catch (Exception x) {}
			
		}
	}

}
