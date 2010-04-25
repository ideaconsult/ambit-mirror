package ambit2.rest.task;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.rest.AmbitResource;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.Task.TaskStatus;

public class TaskHTMLReporter<USERID> extends CatalogURIReporter<Task<Reference,USERID>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(Request ref) {
		super(ref);
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference,USERID>> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest());//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write("<h4>Tasks:");
			for (TaskStatus status :TaskStatus.values())
				output.write(String.format("<a href='%s%s?search=%s'>%s</a>&nbsp;",
						baseReference,SimpleTaskResource.resource,status,status));
			output.write("</h4><p>");
			output.write("<table>");
			output.write("<tr><th>Start time</th><th>Elapsed time,ms</th><th>Task</th><th>Name</th><th colspan='2'>Status</th></tr>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Task<Reference,USERID> item, Writer output) {
		String t = "";
		String status = "Unknown";
		try {
			t = item.getUri()==null?"":item.getUri().toString();
			status = item.getStatus();
		} catch (Exception x) {
			x.printStackTrace();
			status = "Error";
			t = x.getMessage();
		} finally {
			try {output.write(
					String.format("<tr><td>%s</td><td>%s</td><td><a href='%s%s/%s'>%s</a></td><td><a href='%s'>%s</a></td><td><img src=\"%s/images/%s\"></td><td>%s</td><td>%s</td></tr>",
							new Date(item.started),
							item.completed>0?item.completed-item.started:"",
							baseReference.toString(),
							SimpleTaskResource.resource,
							item.getUuid(),
							item.getUuid(),
							t,item.getName(),
							baseReference.toString(),
							item.isDone()?"tick.png":"24x24_ambit.gif",
							status,
							item.getError()==null?"":item.getError().getMessage()
							)); } catch (Exception x) {
				x.printStackTrace();
			}
		}
	};
	@Override
	public void footer(Writer output, Iterator<Task<Reference,USERID>> query) {
		try {
			output.write("</table>");
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}