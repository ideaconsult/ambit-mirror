package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;

import org.codehaus.stax2.XMLOutputFactory2;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

public class RDFStaXConvertor<T,Q extends IQueryRetrieval<T>>  extends QueryRepresentationConvertor<T,Q,XMLStreamWriter>  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2781666113532835085L;

	
	public RDFStaXConvertor(QueryReporter<T, Q, XMLStreamWriter> reporter,String fileNamePrefix) {
		super(reporter,MediaType.APPLICATION_RDF_XML,fileNamePrefix);
	}
	public RDFStaXConvertor(QueryReporter<T, Q, XMLStreamWriter> reporter) {
		this(reporter,null);
	}
	
	@Override
	public Representation process(final Q query) throws Exception {
		Representation rep = new OutputRepresentation(MediaType.APPLICATION_RDF_XML) {
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
		 setDisposition(rep);
		 return rep;
	}


}
