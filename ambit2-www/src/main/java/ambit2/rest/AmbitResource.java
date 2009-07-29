package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

public class AmbitResource extends Resource {
	protected String[][] uri = {
			{DatasetsResource.datasets,"Datasets"},
			{AmbitApplication.query,"Similarity search"},
			/*
			{"/endpoints","Endpoints"},
			{"/descriptors","Descriptors"},
			*/
			{String.format("%s/%d",CompoundResource.compound,100),"Chemical compounds"},
			{String.format("%s/%d%s/%s",CompoundResource.compound,100,ConformerResource.conformerKey,"all"),"All conformer of a compound"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"Conformer of a compound"},
			{"/query/similarity/method/fp1024/distance/tanimoto/0.5/smiles/c1ccccc1","Demo similarity search"},
			{"/query/property/like/benzene","Search by name"},
			{"/query/property/=/50-00-0","Search by property or an identifier (CAS, Name, etc.)"},
			{"/query/smarts/[NX3][CX3](=[OX1])[#6]","Search by SMARTS"},
			{"/pubchem/query/50-00-0","PubChem query"},
			{"/cdk/depict/c1ccccc1","Structure diagram (based on CDK)"},
			{"/daylight/depict/c1ccccc1","Structure diagram (based on Daylight depict"},
			{"/build3d/smiles/c1ccccc1","Generate 3D structure"}
	};
	public AmbitResource() {
		super();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		
		//consumer = new DatasetConsumer();

	}
	

	
	@Override
	public Representation getRepresentation(Variant variant) {
	    System.out.println(getRequest().getAttributes());    
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
				variant.setMediaType(MediaType.TEXT_HTML);
				StringBuilder html = new StringBuilder();
				html.append(
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				);
				html.append("<html><head><title>Ambit REST</title></head>\n");
				html.append("<body>\n");
				html.append("<ul>");
				for (String[] s:uri) {
					html.append("<li>");
					html.append(s[1]);
					html.append("&nbsp;<a href=\"");
					html.append(getRequest().getRootRef());
					html.append(s[0]);
					html.append("\">");
					html.append(s[0]);
					html.append("</a>");
					html.append("\n");
				}
				html.append("</ul>");
				html.append("\n<table width='100%'>");
				html.append("<tr><td colspan=\"2\" align=\"right\">");
				html.append("<font color='#D6DFF7'>");
				html.append("Developed by Ideaconsult Ltd. (2005-2009)"); 
				html.append("</font>");
				html.append("</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td colspan=\"2\" align=\"right\">");
				html.append("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
				html.append("    <IMG SRC=\"images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">");
				html.append("  </A>&nbsp; ");

				html.append("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
				html.append("    <IMG SRC=\"images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">");
				html.append("  </A>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</body>");
				html.append("</html>");
				return new StringRepresentation(html.toString(),MediaType.TEXT_HTML);				
			}
			
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("An error retrieving the dataset "+x.getMessage(),
			MediaType.TEXT_PLAIN);			
		}
	}
	
}
