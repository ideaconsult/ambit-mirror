package ambit2.rest.loom;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.i5.cli.I5LightClient;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;
import net.idea.restnet.i.task.TaskStatus;

import org.apache.poi.ss.formula.functions.T;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.DBConnection;
import ambit2.rest.StringConvertor;
import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.reporters.CatalogURIReporter;

public class PingResource extends CatalogResource<ITask<ITaskResult, String>> {
    ITask<ITaskResult, String> task = null;
    protected List<ITask<ITaskResult, String>> tasks = new ArrayList<ITask<ITaskResult, String>>();
    protected String server = null;
    protected String user = null;
    protected String pass = null;

    enum _supportedresource {
	i5
    }

    public PingResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "task.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();

	getVariants().clear();
	getVariants().add(new Variant(MediaType.TEXT_HTML));
	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	getVariants().add(new Variant(MediaType.TEXT_JAVASCRIPT));
	getVariants().add(new Variant(MediaType.APPLICATION_RDF_XML));
	getVariants().add(new Variant(MediaType.TEXT_RDF_N3));
    }

    @Override
    protected Iterator<ITask<ITaskResult, String>> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	if (tasks.size() == 0) {
	    _supportedresource restype;
	    try {
		restype = _supportedresource.valueOf(request.getAttributes().get(LoomResource.resourceType).toString());
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	    try {
		Form form = request.getResourceRef().getQueryAsForm();
		server = form.getFirstValue("server");
	    } catch (Exception x) {
		server = null;
	    }
	    try {
		Form form = request.getResourceRef().getQueryAsForm();
		user = form.getFirstValue("user");
	    } catch (Exception x) {
		user = null;
	    }
	    try {
		// not good, but it is for testing only
		Form form = request.getResourceRef().getQueryAsForm();
		pass = form.getFirstValue("pass");
	    } catch (Exception x) {
		pass = null;
	    }
	    try {
		// ok, only i5 supported for now, so don't bother
		tasks.add(pingI5(request.getResourceRef(), context));
	    } catch (ResourceException x) {
		throw x;
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	}
	return tasks.iterator();
    }

    @Override
    public IProcessor<Iterator<ITask<ITaskResult, String>>, Representation> createConvertor(Variant variant)
	    throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    return new StringConvertor(createHTMLReporter(), MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    Reporter reporter = new CatalogURIReporter<ITask>() {
		/**
			     * 
			     */
		private static final long serialVersionUID = -8184545519930067423L;
		protected String comma = null;

		@Override
		public void processItem(ITask item, Writer output) {
		    try {
			if (comma != null)
			    output.write(comma);
			output.write(item.toJSON());
			comma = ",";
		    } catch (Exception x) {
		    }
		    ;
		}

		@Override
		public void footer(Writer output, Iterator<ITask> query) {
		    try {
			output.write("\n]\n}");
		    } catch (Exception x) {
		    }
		};

		@Override
		public void header(Writer output, Iterator<ITask> query) {
		    try {
			output.write("{\"task\": [");
		    } catch (Exception x) {
		    }

		};
	    };
	    return new StringConvertor(reporter, MediaType.APPLICATION_JSON);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    return new StringConvertor(new CatalogURIReporter<T>(getRequest()) {

		private static final long serialVersionUID = -1928996919350493544L;

		@Override
		public void processItem(T src, Writer output) {
		    super.processItem(src, output);
		    try {
			output.write('\n');
		    } catch (Exception x) {
		    }
		}
	    }, MediaType.TEXT_URI_LIST, filenamePrefix);

	} else
	    // html
	    return new StringConvertor(createHTMLReporter(), MediaType.TEXT_HTML);

    }

    protected ITask<ITaskResult, String> pingI5(Reference uri, Context context) throws ResourceException {
	ITask<ITaskResult, String> task;
	DBConnection dbc = new DBConnection(context);
	I5LightClient cli = null;
	long now = System.currentTimeMillis();
	if (server == null)
	    server = dbc.getProperty("i5.server");
	if (user == null)
	    user = dbc.getProperty("i5.user");
	if (pass == null)
	    pass = dbc.getProperty("i5.pass");
	task = new Task<ITaskResult, String>(user);
	task.setName("Ping " + server);
	task.setStatus(TaskStatus.Running);
	task.setUri(new TaskResult(uri.toString(), false));
	try {
	    cli = new I5LightClient(server);
	    if (cli.login(user, pass)) {
		task.setStatus(TaskStatus.Completed);
	    } else {
		task.setStatus(TaskStatus.Error);
		task.setError(new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED));
	    }
	} catch (Exception x) {
	    task.setError(new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage()));
	    task.setStatus(TaskStatus.Error);
	} finally {
	    try {
		cli.logout();
	    } catch (Exception x) {
	    }
	}
	task.setTimeCompleted(System.currentTimeMillis());
	return task;
    }

}
