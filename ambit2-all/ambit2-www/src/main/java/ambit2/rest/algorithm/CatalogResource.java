package ambit2.rest.algorithm;

import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;

import org.apache.poi.hssf.record.formula.functions.T;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.StringConvertor;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public abstract class CatalogResource<T extends Serializable> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {


	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.TEXT_HTML
				});
		
	}


	public static String getAlgorithmURI(String category) {
		return String.format("%s%s/{%s}",AllAlgorithmsResource.algorithm,category,AllAlgorithmsResource.algorithmKey);
	}
	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new CatalogURIReporter<T>(getRequest()) {
				@Override
				public void processItem(T src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest()),MediaType.TEXT_HTML);
		
	}
	
	
	protected CallableQueryProcessor createCallable(Form form, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	
	protected Reference getSourceReference(Form form,T model) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			
			Form form = entity.isAvailable()?new Form(entity):new Form();
			
			//models
			Iterator<T> query = queryObject;
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			
			while (query.hasNext()) 
			try {
				T model = query.next();
				Reference reference = getSourceReference(form,model);
				Reference ref =  ((AmbitApplication)getApplication()).addTask(
						String.format("Apply %s to %s",model.toString(),reference),
						createCallable(form,model),
						getRequest().getRootRef());		
				getResponse().setLocationRef(ref);
				//getResponse().setStatus(Status.SUCCESS_CREATED);
				getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				getResponse().setEntity(null);
			} catch (Exception x) {
				if (x.getCause() instanceof ResourceException)
					getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
				else
					getResponse().setStatus(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
			}
			return null;
		}
	}
	
}
