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
	public final static String ns_opentox = "http://opentox.org/1.0";
	public final static String node_ambit = "ambit";
	public final static String node_dataset = "dataset";
	public final static String node_datasets = "datasets";
	public final static String node_structures = "structures";
	public final static String node_structure = "structure";
	public final static String node_features = "features";
	public final static String node_feature = "feature";
	public final static String node_reference = "reference";
	public final static String attr_href = "href";
	public final static String node_link = "link";
	public final static String attr_id = "id";
	public final static String attr_name = "name";
	public final static String slash = "/";
	public final static String version = "1.0";
	
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
