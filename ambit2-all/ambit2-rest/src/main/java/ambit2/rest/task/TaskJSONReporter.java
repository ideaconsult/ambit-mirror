package ambit2.rest.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.json.JSONUtils;
import ambit2.rest.reporters.TaskURIReporter;

public class TaskJSONReporter<USERID> extends TaskURIReporter<USERID> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 9136099541989811170L;
    protected String comma = null;

    public TaskJSONReporter(ITaskStorage<USERID> storage) {
	super(storage);
    }

    public TaskJSONReporter(ITaskStorage<USERID> storage, Request request) {
	super(storage, request);
    }

    protected TaskJSONReporter(ITaskStorage<USERID> storage, Reference baseRef) {
	super(storage, baseRef);
    }

    private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": \"%s\",\n\t\"name\": \"%s\",\n\t\"error\": \"%s\",\n\t\"policyError\": \"%s\",\n\t\"status\": \"%s\",\n\t\"started\": %d,\n\t\"completed\": %d,\n\t\"result\": \"%s\",\n\t\"user\": \"%s\",\n\t\"errorCause\": %s\n}";

    @Override
    public void processItem(UUID item, Writer output) {
	try {
	    if (comma != null)
		output.write(comma);

	    ITask<ITaskResult, USERID> task = storage.findTask(item);
	    String uri = task.getUri() == null ? null : task.getUri().toString();

	    StringWriter errorTrace = null;
	    if (task.getError() != null) {
		try {
		    ResourceException exception = task.getError();
		    errorTrace = new StringWriter();
		    if (exception.getCause() == null)
			exception.printStackTrace(new PrintWriter(errorTrace));
		    else
			exception.getCause().printStackTrace(new PrintWriter(errorTrace));
		} catch (Exception x) {

		}
	    }
	    output.write(String.format(format, uri, item.toString(),
		    task.getName() == null ? "" : JSONUtils.jsonEscape(task.getName()), task.getError() == null ? ""
			    : JSONUtils.jsonEscape(task.getError().toString()), task.getPolicyError() == null ? ""
			    : JSONUtils.jsonEscape(task.getPolicyError().toString()), task.getStatus() == null ? ""
			    : task.getStatus(), task.getStarted(), task.getTimeCompleted(), task.getUri() == null ? ""
			    : task.getUri(), task.getUserid() == null ? "" : task.getUserid(),
		    errorTrace == null ? null : JSONUtils.jsonQuote(JSONUtils.jsonEscape(errorTrace.toString()))));
	    comma = ",";
	} catch (IOException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
    }

    @Override
    public void footer(Writer output, Iterator<UUID> query) {
	try {
	    output.write("\n]\n}");
	} catch (Exception x) {
	}
    };

    @Override
    public void header(Writer output, Iterator<UUID> query) {
	try {
	    output.write("{\"task\": [");
	} catch (Exception x) {
	}

    };
}