package ambit2.rest;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class AmbitResource extends Resource {
	protected String[][] uri = {
			{AmbitApplication.datasets,"Datasets"},
			{AmbitApplication.query,"Queries"},
			{"/endpoints","Endpoints"},
			{"/descriptors","Descriptors"},
			{"/structure/100","Structures"},
			{"/query/similarity/method/fp1024/distance/tanimoto/0.5/smiles/c1ccccc1","Demo similarity search"}
	};
	public AmbitResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		
		//consumer = new DatasetConsumer();

	}
	@Override
	public Representation getRepresentation(Variant variant) {
		try {
			if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				StringBuilder xml = new StringBuilder();
				xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
				xml.append("<ambit xmlns=\"http://ambit.sourceforge.net/ambit/rest/v2\">");
				for (String[] s:uri) {
					xml.append("<uri>");
					xml.append(getRequest().getRootRef());
					xml.append(s[0]);
					xml.append("</uri>");
				}
				xml.append("</ambit>");
				return new StringRepresentation(xml.toString());
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				StringBuilder xml = new StringBuilder();
				for (String[] s:uri) {
					xml.append(getRequest().getRootRef());
					xml.append(s[0]);
				}
				return new StringRepresentation(xml.toString());				
			} else { //if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
				StringBuilder xml = new StringBuilder();
				xml.append("<html><title>Ambit REST</title>");
				xml.append("<body>");
				for (String[] s:uri) {
					xml.append("<a href=\"");
					xml.append(getRequest().getRootRef());
					xml.append(s[0]);
					xml.append("\">");
					xml.append(s[1]);
					xml.append("</a>");
					xml.append("<br>");
				}
				xml.append("</body>");
				return new StringRepresentation(xml.toString());				
			}
			
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("An error retrieving the dataset "+x.getMessage(),
			MediaType.TEXT_PLAIN);			
		}
	}
	@Override
	public boolean allowPost() {
		return false;
	}	
}
