package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;

import ambit2.rest.AmbitResource;
import ambit2.rest.algorithm.AlgorithmCatalogResource;
import ambit2.rest.algorithm.CatalogURIReporter;

public class TaskHTMLReporter extends CatalogURIReporter<Task<Reference>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(Reference ref) {
		super(ref);
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference>> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", baseReference);//,"<meta http-equiv=\"refresh\" content=\"10\">");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Task<Reference> item, Writer output) {
		String t = "";
		String status = item.isDone()?"Completed":"Running, Please wait ...";
		try {
			t = item.getReference().toString();
			
		} catch (Exception x) {
			status = "Error";
			t = x.getMessage();
		} finally {
			try {output.write(String.format("%s&nbsp;<a href='%s'>%s</a>&nbsp;%s<br>", item.getName(),t,t,status)); } catch (Exception x) {}
		}
	};
	@Override
	public void footer(Writer output, Iterator<Task<Reference>> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AlgorithmCatalogResource.algorithm, baseReference);
			output.flush();
		} catch (Exception x) {
			
		}
	}
}