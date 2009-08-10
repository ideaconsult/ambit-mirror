package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;

import ambit2.rest.AmbitResource;
import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.algorithm.AlgorithmURIReporter;

public class TaskHTMLReporter extends AlgorithmURIReporter<Task<Reference>> {

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
		try {
			String t = item.getReference().toString();
			output.write(String.format("<a href='%s'>%s</a>&nbsp;%s<br>", t,t,item.isDone()?"Completed":"Running"));
		} catch (Exception x) {
			
		}
	};
	@Override
	public void footer(Writer output, Iterator<Task<Reference>> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AlgorithmResource.algorithm, baseReference);
			output.flush();
		} catch (Exception x) {
			
		}
	}
}