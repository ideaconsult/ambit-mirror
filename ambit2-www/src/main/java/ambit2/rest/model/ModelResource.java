package ambit2.rest.model;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.algorithm.AlgorithmHTMLReporter;
import ambit2.rest.algorithm.AlgorithmURIReporter;

/**
 * Model as in http://opentox.org/wiki/opentox/Model
 * @author nina
 *
 */
public class ModelResource extends AbstractResource<Iterator<String>,String,IProcessor<Iterator<String>, Representation>> {

	public final static String model = "/model";	
	public final static String modelID =  "model_id";

	public enum modeltypes  {
		pka,toxtree
	};
	
	protected String category = "";
	public ModelResource(Context context, Request request, Response response) {
		super(context,request,response);

		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		

	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		ArrayList<String> q = new ArrayList<String>();
		for (modeltypes d : modeltypes.values())
			q.add(String.format("%s/%s","model",d.toString()));		
		return q.iterator();
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public static String getModelURI(String category) {
		return String.format("%s%s/{%s}",model,category,modelID);
	}
	@Override
	public IProcessor<Iterator<String>, Representation> createConvertor(
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
			
		} else 
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
		
	}
}

