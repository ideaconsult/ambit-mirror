package ambit2.rest.task;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.rest.AmbitResource;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.algorithm.CatalogURIReporter;
import ambit2.rest.task.Task.taskStatus;

public class TaskHTMLReporter extends CatalogURIReporter<Task<Reference>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(Request ref) {
		super(ref);
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference>> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest());//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write("<h4>Tasks:");
			for (taskStatus status :taskStatus.values())
				output.write(String.format("<a href='?search=%s'>%s</a>&nbsp;",status,status));
			output.write("</h4><p>");
			output.write("<table>");
			output.write("<tr><th>Start time</th><th>Task</th><th colspan='2'>Status</th></tr>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Task<Reference> item, Writer output) {
		String t = "";
		String status = "Unknown";
		try {
			t = item.getReference().toString();
			status = item.getStatus();
		} catch (Exception x) {
			x.printStackTrace();
			status = "Error";
			t = x.getMessage();
		} finally {
			try {output.write(
					String.format("<tr><td>%s</td><td><a href='%s'>%s</a></td><td><img src=\"%s/images/%s\"></td><td>%s</td></tr>",
							new Date(item.started),
							t,item.getName(),
							baseReference.toString(),
							item.isDone()?"tick.png":"24x24_ambit.gif",
								status
							)); } catch (Exception x) {
				x.printStackTrace();
			}
		}
	};
	@Override
	public void footer(Writer output, Iterator<Task<Reference>> query) {
		try {
			output.write("</table>");
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}