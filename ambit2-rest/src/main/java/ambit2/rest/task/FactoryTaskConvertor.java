package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;

import org.openscience.jchempaint.action.CreateReactionAction;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.Reporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.reporters.TaskRDFReporter;
import ambit2.rest.reporters.TaskURIReporter;

public class FactoryTaskConvertor<USERID> {
	
	public synchronized IProcessor<Iterator<Task<Reference,USERID>>, Representation> createTaskConvertor(
			Variant variant, Request request) throws AmbitException, ResourceException {

		return new StringConvertor(createTaskReporter(variant, request),variant.getMediaType());
	}
	public synchronized Reporter<Iterator<Task<Reference,USERID>>,Writer> createTaskReporter(
			Variant variant, Request request) throws AmbitException, ResourceException {

		Reporter<Iterator<Task<Reference,USERID>>,Writer> reporter = null;
		
		if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
				variant.getMediaType().equals(MediaType.APPLICATION_JSON)
				) 
			reporter =  createTaskReporterRDF(variant, request);	
		else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) 
			reporter = createTaskReporterURI(request);		
		else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) 
			reporter = createTaskReporterHTML( request);	
		else  //MediaType.TEXT_URI_LIST
			reporter = createTaskReporterURI(request);
		return reporter;
	}
	public synchronized Reporter<Iterator<Task<Reference,USERID>>,Writer> createTaskReporterURI(Request request) throws AmbitException, ResourceException {
		return new TaskURIReporter<USERID>(request) {
			@Override
			public void processItem(Task<Reference, USERID> item, Writer output) {
				super.processItem(item, output);
				try {output.write('\n'); } catch (Exception x) {}
			}
		};
	}	
	public synchronized Reporter<Iterator<Task<Reference,USERID>>,Writer> createTaskReporterRDF(
			Variant variant, Request request) throws AmbitException, ResourceException {
		return new TaskRDFReporter<USERID>(request,variant.getMediaType());
	}		
	public synchronized Reporter<Iterator<Task<Reference,USERID>>,Writer> createTaskReporterHTML(Request request) throws AmbitException, ResourceException {
		return new TaskURIReporter<USERID>(request);
	}	
	
	public synchronized Representation createTaskRepresentation(Task<Reference,USERID> task, Variant variant, Request request, Response response) throws ResourceException {
		try {
			if (task==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			IProcessor<Iterator<Task<Reference,USERID>>,Representation> p = createTaskConvertor(variant,request);
			//task.update();
			//System.out.println("convertor" + task.getUri() + " " + task.getStatus());
			//response.setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
			//task.update();
			//System.out.println("convertor" + response.getStatus());
			return p.process(new SingleTaskIterator<USERID>(task));
		} catch (AmbitException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	
	public synchronized Representation createTaskRepresentation(Iterator<Task<Reference,USERID>> tasks, Variant variant, Request request, Response response) throws ResourceException {
		try {
			System.out.println("convertor" );
			if (tasks==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			IProcessor<Iterator<Task<Reference,USERID>>,Representation> p = createTaskConvertor(variant,request);
			return p.process(tasks);
		} catch (AmbitException x) {
			x.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}	
	
}

