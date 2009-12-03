package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.StringConvertor;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public abstract class AlgorithmCatalogResource<T> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {


	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {MediaType.TEXT_HTML,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES	
				});
		
	}


	public static String getAlgorithmURI(String category) {
		return String.format("%s%s/{%s}",AlgorithmResource.algorithm,category,AlgorithmResource.algorithmKey);
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
	
}
