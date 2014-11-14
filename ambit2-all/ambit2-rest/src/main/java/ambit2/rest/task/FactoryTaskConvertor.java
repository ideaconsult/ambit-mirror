package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.DisplayMode;
import ambit2.rest.StringConvertor;
import ambit2.rest.reporters.TaskRDFReporter;
import ambit2.rest.reporters.TaskURIReporter;

public class FactoryTaskConvertor<USERID> extends net.idea.restnet.c.task.FactoryTaskConvertor<USERID> {
	

	public FactoryTaskConvertor(ITaskStorage<USERID> storage) {
		super(storage);
	}
	
	public synchronized IProcessor<Iterator<UUID>, Representation> createTaskConvertor(
			Variant variant, Request request,ResourceDoc doc,DisplayMode _dmode) throws AmbitException, ResourceException {
		String filenamePrefix = "task";
		return new StringConvertor(createTaskReporter(variant, request,doc,_dmode),variant.getMediaType(),filenamePrefix);
	}
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporter(
			Variant variant, Request request,ResourceDoc doc,DisplayMode _dmode) throws AmbitException, ResourceException {

		
		Reporter<Iterator<UUID>,Writer> reporter = null;
		
		if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) 
			reporter =  createTaskReporterRDF(variant, request,doc);	
		else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT))
			reporter = createTaskReporterJSON(variant, request,doc);
		else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON))
			reporter = createTaskReporterJSON(variant, request,doc);		
		else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) 
			reporter = createTaskReporterURI(request,doc);		
		else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) 
			reporter = createTaskReporterHTML( request,doc,_dmode);	
		else  //MediaType.TEXT_URI_LIST
			reporter = createTaskReporterURI(request,doc);
		return reporter;
	}
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporterURI(Request request,ResourceDoc doc) throws AmbitException, ResourceException {
		
		return new TaskURIReporter<USERID>(storage,request,doc) {
			@Override
			public void processItem(UUID item, Writer output) {

				super.processItem(item, output);
				try {output.write('\n'); } catch (Exception x) {}
			}
		};
	}	
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporterJSON(
			Variant variant, Request request,ResourceDoc doc) throws AmbitException, ResourceException {
		return	new TaskJSONReporter(storage,request,doc);
	}	
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporterRDF(
			Variant variant, Request request,ResourceDoc doc) throws AmbitException, ResourceException {
		return new TaskRDFReporter<USERID>(storage,request,variant.getMediaType(),doc);
	}		
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporterHTML(Request request,ResourceDoc doc,DisplayMode _dmode) throws AmbitException, ResourceException {
		return new TaskURIReporter<USERID>(storage,request,doc);
	}	
	
	private static String refresh = "<html><head><meta http-equiv='refresh' content='2;url=%s'></head><body><a href='%s'>Go</a></body></html>";
	
	public synchronized Representation createTaskRepresentation(UUID task, 
			Variant variant, Request request, Response response,ResourceDoc doc) throws ResourceException {
		try {
			if (task==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
				String locationRef = String.format("%s/task/%s", request.getRootRef(),task);
				Representation r = new StringRepresentation(String.format(refresh,locationRef,locationRef),MediaType.TEXT_HTML);				
				response.redirectSeeOther(locationRef);
				return r;
			}
			IProcessor<Iterator<UUID>,Representation> p = createTaskConvertor(variant,request,doc,DisplayMode.singleitem);

			return p.process(new SingleTaskIterator<USERID>(task));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	
	@Override
	public synchronized Representation createTaskRepresentation(
			Iterator<UUID> tasks, Variant variant, Request request,
			Response response, net.idea.restnet.c.ResourceDoc doc)
			throws ResourceException {
		try {

			if (tasks==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			IProcessor<Iterator<UUID>,Representation> p = createTaskConvertor(variant,request,doc,DisplayMode.table);
			return p.process(tasks);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}	
	
	
}

