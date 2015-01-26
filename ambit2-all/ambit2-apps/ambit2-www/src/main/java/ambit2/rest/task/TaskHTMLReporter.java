package ambit2.rest.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.TaskStatus;

import org.restlet.Context;
import org.restlet.Request;

import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitResource;
import ambit2.rest.DisplayMode;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.reporters.CatalogURIReporter;

public class TaskHTMLReporter<USERID> extends CatalogURIReporter<UUID> {
    protected ITaskStorage<USERID> storage;
    protected DisplayMode _dmode = DisplayMode.table;
    protected boolean headless = false;

    public boolean isHeadless() {
	return headless;
    }

    public void setHeadless(boolean headless) {
	this.headless = headless;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 7644836050657868159L;

    public TaskHTMLReporter(ITaskStorage<USERID> storage, Request ref, ResourceDoc doc, DisplayMode _dmode) {
	super(ref, doc);
	this.storage = storage;
    }

    @Override
    public void header(Writer output, Iterator<UUID> query) {

	try {
	    if (!headless) {
		AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(), getResourceRef(), "", getDocumentation());
	    }
	    printNavigation();
	} catch (Exception x) {

	}
    }

    @Override
    public Writer process(Iterator<UUID> query) throws AmbitException {
	_dmode = query instanceof SingleTaskIterator ? DisplayMode.singleitem : DisplayMode.table;
	return super.process(query);
    }

    protected void printNavigation() throws Exception {
	StringBuilder caption = new StringBuilder();
	String max = getRequest().getResourceRef().getQueryAsForm().getFirstValue(AbstractResource.max_hits);
	max = max == null ? "10" : max;
	caption.append("<h4>&nbsp;Tasks: |");
	for (TaskStatus status : TaskStatus.values())
	    caption.append(String.format("<a href='%s%s?search=%s&%s=%s'>%s</a>|&nbsp;", baseReference,
		    SimpleTaskResource.resource, status, AbstractResource.max_hits, max, status));
	caption.append("</h4><p>");

	switch (_dmode) {
	case table: {
	    output.write("<table id='tasks' class='datatable'>");
	    output.write("<caption CLASS='results'>");
	    output.write(caption.toString());
	    output.write("</caption>");
	    output.write("<thead>");
	    output.write("<th>Task</th><th>Status</th><th>Start time</th><th>Elapsed time,ms</th>");
	    output.write("</thead>");
	    output.write("<tbody>");
	    break;
	}
	default: {
	    output.write(caption.toString());
	}
	}

    }

    @Override
    public void processItem(UUID name, Writer output) {
	switch (_dmode) {
	case singleitem: {
	    processSingleItem(name, output);
	    break;
	}
	default: { // table
	    processTableItem(name, output);
	    break;
	}
	}
    }

    public void processSingleItem(UUID name, Writer output) {
	ITask<ITaskResult, USERID> item = storage.findTask(name);
	String t = "";
	String status = "Unknown";
	try {
	    t = item.getUri() == null ? "" : item.getUri().toString();
	    status = item.getStatus().toString();
	} catch (Exception x) {
	    Context.getCurrentLogger().log(Level.WARNING, x.getMessage(), x);
	    status = "Error";
	    t = "";
	} finally {

	    try {
		String uri = String.format("%s%s/%s", baseReference, SimpleTaskResource.resource, item.getUuid());
		output.write(String.format(
			"<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">\n"
				+ "<div class=\"ui-widget-header ui-corner-top\"><p><a href='%s'>Job</a> started %s &nbsp;</p></div>\n"
				+ "<div class=\"ui-widget-content ui-corner-bottom %s\">\n"
				+ "%s\n"
				+ "<p>Name:&nbsp;<strong>%s</strong></p>"
				+ "<p>Status:&nbsp;<a href='%s' id='result'>%s</a>&nbsp;<img src='%s/images/%s' id='status'>"
				+ "%s\n" + "</p>\n" + "</div></div>\n",

			uri, new Date(item.getStarted()), item.isDone() ? (item.getError() == null ? ""
				: "ui-state-highlight") : "",
			item.isDone() ? "" : String.format(checkTask, uri, baseReference, baseReference), item
				.getName(), item.isDone() ? item.getUri().getUri() : "", // href
			TaskStatus.Completed.equals(item.getStatus()) ? "Ready. Results available." : item.getStatus(), // status
			baseReference, item.isDone() ? item.getError() != null ? "cross.png" : "tick.png"
				: "24x24_ambit.gif", // image
			getErrorReport(item.getError())

		// item.getError()!=null?"Error":item.getTimeCompleted()>0?"Ready.":"",
		// item.getError()!=null?"":item.getTimeCompleted()>0?new
		// Date(item.getTimeCompleted()):""

		// item.isDone()?(item.getError()==null?"ui-icon-check":"ui-icon-alert"):"ui-icon-info",
		// status,item.getError()==null?"":item.getError().getMessage(),
		// item.getPolicyError()==null?"":"Policy error",item.getPolicyError()==null?"":item.getPolicyError().getMessage()
		));

	    } catch (Exception x) {
		x.printStackTrace();
	    }
	}
    };

    private final String checkTask = "<script>checkTask('%s','result', 'status', '%s/images/tick.png', '%s/images/cross.png');</script>\n";

    public void processTableItem(UUID name, Writer output) {
	ITask<ITaskResult, USERID> item = storage.findTask(name);
	String t = "";
	String status = "Unknown";
	try {
	    t = item.getUri() == null ? "" : item.getUri().toString();
	    status = item.getStatus().toString();
	} catch (Exception x) {
	    Context.getCurrentLogger().log(Level.WARNING, x.getMessage(), x);
	    status = "Error";
	    t = "";
	} finally {

	    try {
		String uri = String.format("%s%s/%s", baseReference, SimpleTaskResource.resource, item.getUuid());

		output.write(String.format("<tr>\n" + "<td><a href='%s'>Job</a></td>\n" + "<td>%s %s "
			+ // checktask
			"Status:&nbsp;<a href='%s' id='result'>%s</a>&nbsp;<img src='%s/images/%s' id='status'>"
			+ "%s\n</td><td>%s</td><td>%s</td></tr>\n",

		uri, item.isDone() ? "" : String.format(checkTask, uri, baseReference, baseReference),
			item.getName(),
			item.isDone() ? item.getUri().getUri() : "", // href
			TaskStatus.Completed.equals(item.getStatus()) ? "Ready. Results available." : item.getStatus(), // status
			baseReference,
			item.isDone() ? item.getError() != null ? "cross.png" : "tick.png" : "24x24_ambit.gif", // image
			getErrorReport(item.getError()), new Date(item.getStarted()),
			item.isDone() ? new Date(item.getTimeCompleted()) : ""

		// item.getError()!=null?"Error":item.getTimeCompleted()>0?"Ready.":"",
		// item.getError()!=null?"":item.getTimeCompleted()>0?new
		// Date(item.getTimeCompleted()):""

			// item.isDone()?(item.getError()==null?"ui-icon-check":"ui-icon-alert"):"ui-icon-info",
			// status,item.getError()==null?"":item.getError().getMessage(),
			// item.getPolicyError()==null?"":"Policy error",item.getPolicyError()==null?"":item.getPolicyError().getMessage()
			));

	    } catch (Exception x) {
		Context.getCurrentLogger().log(Level.WARNING, x.getMessage(), x);
	    }
	}
    };

    protected String getErrorReport(Throwable x) {
	if (x == null)
	    return "";
	if (x.getCause() != null)
	    x = x.getCause();

	StringWriter details = new StringWriter();
	x.printStackTrace(new PrintWriter(details) {
	    @Override
	    public void print(String s) {
		super.print(String.format("%s<br>", s));

	    }
	});
	String detailsDiv = details == null ? ""
		: String.format(
			"<a href=\"#\" style=\"padding: 5px 10px;\" onClick=\"toggleDiv('%s'); return false;\">Details: %s</a><br><div style='display: none;' id='details'><p>%s</p></div>\n",
			"details", x.getMessage(), details);
	return detailsDiv;
    }

    @Override
    public void footer(Writer output, Iterator<UUID> query) {
	try {
	    switch (_dmode) {
	    case table: {
		output.write("</tbody></table>");
	    }
	    default: {
		break;
	    }
	    }
	    AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
	    output.flush();
	} catch (Exception x) {

	}
    }
}