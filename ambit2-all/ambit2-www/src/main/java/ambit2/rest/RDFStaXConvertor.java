package ambit2.rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

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
				 BufferedOutputStream buf = new BufferedOutputStream(out);
;				XMLStreamWriter writer = null;
				try {
					XMLOutputFactory factory      = XMLOutputFactory.newInstance();
					factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, "TRUE");

					writer  = factory.createXMLStreamWriter(buf);
					
					getReporter().setOutput(writer);
            		getReporter().process(query);
	
				} catch (Exception  x) {
					x.printStackTrace();
					throw new IOException(x.getMessage());
				} finally {
					try { writer.flush();} catch (Exception x) {}
					try { out.flush();} catch (Exception x) {}
					try { writer.close();} catch (Exception x) {}
					
				}
				
			}
		 };
	}


}
