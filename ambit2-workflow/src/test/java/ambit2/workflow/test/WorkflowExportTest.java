package ambit2.workflow.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ambit2.core.data.experiment.TemplateField;
import ambit2.core.exceptions.AmbitIOException;
import ambit2.core.io.DownloadTool;
import ambit2.workflow.ui.WorkflowExport;

import com.microworkflow.process.Workflow;
import com.microworkflow.ui.WorkflowTools;

public class WorkflowExportTest {
	protected WorkflowExport export;
	protected Document xpdl;
	@Before
	public void setup() {
		export = new WorkflowExport();
	}
	@Test
	public void testWrite() throws Exception  {
		StringWriter writer = new StringWriter();
		Workflow wf = new Workflow();
		wf.setDefinition(WorkflowTools.createWorkflow().getDefinition());
		//wf.setDefinition(new LoginSequence(null));
		Document doc = export.write(wf);
    	DOMSource domSource = new DOMSource(doc);
    	StreamResult streamResult = new StreamResult(writer);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer serializer = tf.newTransformer();
    	serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
    	serializer.setOutputProperty(OutputKeys.INDENT,"yes");
    	serializer.transform(domSource, streamResult);		
    	System.out.println(writer.toString());
	}
	@Test
	public void testRead() throws Exception  {
		InputStreamReader reader = new InputStreamReader(
				DownloadTool.class.getClassLoader().getResourceAsStream("ambit2/workflow/test/workflow.xpdl"),
				"UTF-8");
		Assert.assertNotNull(reader);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xpdl = builder.parse(new InputSource(reader));   
        try {
        	export.read(xpdl);
        } catch (Exception x) {
        	throw new Exception(x);
        } finally {
        	reader.close();
        }
	}

		
}
