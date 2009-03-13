package ambit2.rest.query;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLTags {
	private static final long serialVersionUID = -3235018056866994267L;
	public final static String ns_ambit = "http://ambit.sourceforge.net/ambit/rest/v2";
	public final static String node_ambit = "ambit";
	public final static String node_dataset = "dataset";
	public final static String node_datasets = "datasets";
	public final static String node_reference = "reference";
	public final static String node_www = "url";
	public final static String node_uri = "uri";
	public final static String attr_id = "id";
	public final static String attr_name = "name";
	public final static String slash = "/";
	
	protected XMLTags() {
		
	}
    public static void save(Document doc,Writer writer) throws IOException,TransformerException {
		Source source = new DOMSource(doc);
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT,"yes");
        xformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");        
        Result result = new StreamResult(writer);
        xformer.transform(source, result);
        writer.flush();
	}	
	public static String save(Document doc) throws IOException,TransformerException {
		StringWriter writer = new StringWriter();
		save(doc,writer);
		return writer.toString();
	}		

}
