package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.AbstractObjectConvertor;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 * Converts resource to DOM representation
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class DocumentConvertor<T,Q extends IQueryRetrieval<T>>  
										extends AbstractObjectConvertor<T,Q,Document>  {

	public DocumentConvertor(QueryReporter<T,Q,Document> reporter,String fileNamePrefix) {
		super(reporter,MediaType.TEXT_XML,fileNamePrefix);
	}	
	public DocumentConvertor(QueryReporter<T,Q,Document> reporter) {
		this(reporter,null);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	protected Document createOutput(Q query) throws AmbitException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	};
	public Representation processDoc(final Document doc) throws AmbitException {
		 return new OutputRepresentation(MediaType.TEXT_XML) {
			 @Override
			public void write(OutputStream out) throws IOException {

				try {
					DOMSource source = new DOMSource(doc);
					Transformer xformer = TransformerFactory.newInstance().newTransformer();
			        xformer.setOutputProperty(OutputKeys.INDENT,"yes");
			        xformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
			    
			        Result result = new StreamResult(out);
			        xformer.transform(source, result);
			        
				} catch (Exception  x) {
					throw new IOException(x.getMessage());
				} finally {
					try { out.flush();} catch (Exception x) {}
				}
				
			}
		 };

		

	
	}

}
