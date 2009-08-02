package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.StringConvertor;

/**
 * Algorithms as per http://opentox.org/wiki/opentox/Algorithm
 * @author nina
 *
 */
public class AlgorithmResource extends AbstractResource<List<String>,String,IProcessor<List<String>, Representation>> {
	public final static String algorithm = "/algorithm";	
	public final static String algorithmKey =  "algorithm_id";

	public enum algorithmtypes  {
		util,preprocessing,clustering,descriptorcalculation,learning
	};
	
	protected String category = "";
	public AlgorithmResource(Context context, Request request, Response response) {
		super(context,request,response);
		query = new ArrayList<String>();
		for (algorithmtypes d : algorithmtypes.values())
			query.add(String.format("%s/%s","algorithm",d.toString()));		
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		

	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public static String getAlgorithmURI(String category) {
		return String.format("%s%s/{%s}",algorithm,category,algorithmKey);
	}
	@Override
	public IProcessor<List<String>, Representation> createConvertor(
			Variant variant) throws AmbitException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
			*/
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		
			return new StringConvertor(	new AlgorithmURIReporter(getRequest().getRootRef()) {
				@Override
				public void processItem(Object src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
		
	}
	
}
