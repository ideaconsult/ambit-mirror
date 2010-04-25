package ambit2.fastox.task;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.rest.SimpleTaskResource;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.Task;
import ambit2.rest.task.Task.TaskStatus;

public class TaskHTMLReporter<IToxPredictUser> extends CatalogURIReporter<Task<Reference,IToxPredictUser>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(Request ref) {
		super(ref);
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference,IToxPredictUser>> query) {
		try {
			output.write("<html><body>");
			output.write("<h4>Tasks:");
			for (TaskStatus status :TaskStatus.values())
				output.write(String.format("<a href='%s%s?search=%s'>%s</a>&nbsp;",
						baseReference,SimpleTaskResource.resource,status,status));
			output.write("</h4><p>");
			output.write("<table>");
			output.write("<tr><th>Start time</th><th>End time</th><th>Task</th><th>Name</th><th colspan='2'>Status</th></tr>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Task<Reference,IToxPredictUser> item, Writer output) {
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
					String.format("<tr><td>%s</td><td><a href='%s'>%s</a></td><td><img src=\"%s/images/%s\"></td><td>%s</td></tr>",
							new Date(item.getStarted()),
							item.getCompleted()>0?new Date(item.getCompleted()):"",
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
	public void footer(Writer output, Iterator<Task<Reference,IToxPredictUser>> query) {
		try {
			output.write("</table>");
			output.write("</body></html>");
			output.flush();
		} catch (Exception x) {
			
		}
	}
}