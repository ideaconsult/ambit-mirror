package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.stax2.XMLOutputFactory2;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class RDFStaXConvertor<T,Q extends IQueryRetrieval<T>>  extends QueryRepresentationConvertor<T,Q,XMLStreamWriter>  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2781666113532835085L;

	public RDFStaXConvertor(QueryReporter<T, Q, XMLStreamWriter> reporter) {
		super(reporter,MediaType.APPLICATION_RDF_XML);
	}
	
	@Override
	public Representation process(final Q query) throws AmbitException {
		 return new OutputRepresentation(MediaType.APPLICATION_RDF_XML) {
			 @Override
			public void write(OutputStream out) throws IOException {
				//BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(out));
				XMLStreamWriter writer = null;
				try {
					
					XMLOutputFactory factory      =  XMLOutputFactory2.newInstance();
					
					//TODO verify if performance is similar to the original stream writer
					writer  = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(out,"UTF-8"));

					getReporter().setOutput(writer);
            		getReporter().process(query);

				} catch (Exception  x) {
					throw new IOException(x.getMessage());
				} finally {
					try { out.close();} catch (Exception x) {}
					
				}
				
			}
		 };
	}


}
