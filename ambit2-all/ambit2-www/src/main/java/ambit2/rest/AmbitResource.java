package ambit2.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.template.OntologyResource;

public class AmbitResource extends Resource {
	String format = "<tr ><td>%s</td><td><a href=\"%s%s\">%s</a></td><td>%s</td><td>%s</td></tr>";
	String formatHeader = "<tr bgcolor=\"#EEEEEE\" align=\"left\"><th>%s</th><th %s>API <a href=\"%s\" target=\"_blank\">%s</a></th><th>%s</th><th>%s</th></tr>";
	protected String[][] uri = {
			
			{"http://opentox.org/dev/apis/structure","Chemical compounds",formatHeader,null,"Implemented"},
			{String.format("%s/%d",CompoundResource.compound,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s",CompoundResource.compound),"create a new chemical compound",format,"POST","No"},
			{String.format("%s/%d",CompoundResource.compound,100),"Update the representation of chemical compound",format,"PUT","No"},
			{String.format("%s/%d",CompoundResource.compound,100),"Remove chemical compound",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/structure","Conformers",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"update the representation of a structure",format,"PUT","No"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100)," Remove structure",format,"DELETE","No"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"get available conformers of a chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"create a new structure",format,"POST","No"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"Remove conformers",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/feature","Feature definitions",formatHeader,null},
			{PropertyResource.featuredef,"Retrieve all feature definitions",format,"GET","Yes"},
			{PropertyResource.featuredef+"/12142","Get description of a specific feature definition",format,"GET","Yes"},
			{PropertyResource.featuredef,"Create a new feature definition",format,"POST","Yes"},
			
			{"http://opentox.org/dev/apis/structure","Feature definitions per compound/conformer",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s",CompoundResource.compound,100,PropertyResource.featuredef),"All feature definitions, available for a compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100,PropertyResource.featuredef),"All feature definitions, available for a conformer",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100304,PropertyResource.featuredef),"All feature definitions, available for a conformer",format,"GET","Yes"},
			
			{"http://opentox.org/dev/apis/dataset","Dataset",formatHeader,null,"Implemented"},
			{DatasetsResource.datasets,"get list of datasets available",format,"GET","Yes"},
			{DatasetsResource.datasets+"/8","get dataset",format,"GET","Yes"},
			{DatasetsResource.datasets,"create a new dataset",format,"POST","Yes"},
			{"/dataset/{id}","update dataset",format,"PUT","No"},
			{"/dataset/{id}","remove dataset",format,"DELETE","No"},
			
			
			{"http://opentox.org/dev/apis/dataset","Chemical compounds in a dataset",formatHeader,null,"Implemented"},			
			{DatasetsResource.datasets+"/8/compound","get compounds",format,"GET","Yes"},
			{DatasetsResource.datasets+"/8/compound/413","get compound",format,"GET","Yes"},
			{DatasetsResource.datasets+"/{id}/compound","add compound",format,"POST","No"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}","update compound",format,"PUT","No"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}","remove compound from a dataset",format,"DELETE","No"},
			{DatasetsResource.datasets+"/{id}/compound","remove all compounds in a dataset",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/dataset","Conformers in a dataset",formatHeader,null,"Implemented"},
			{DatasetsResource.datasets+"/8/compound/413/conformer/all","get conformers",format,"GET","Yes"},
			{DatasetsResource.datasets+"/8/compound/413/conformer/100617","get conformer",format,"GET","Yes"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}","add conformer",format,"POST","No"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}/conformer/{id}","update conformer",format,"PUT","No"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}/conformer/{id}","remove conformer from the dataset",format,"DELETE","No"},
			{DatasetsResource.datasets+"/{id}/compound/{cid}","remove all conformers",format,"DELETE","No"},

			{"http://opentox.org/development/wiki/dataset","Feature definitions in a dataset",formatHeader,null,"Implemented"},
			{DatasetsResource.datasets+"/8/feature_definition","get feature definitions available ion the dataset",format,"GET","No"},
			{DatasetsResource.datasets+"/8/feature_definition/12142","get specific feature definition",format,"GET","No"},
			{DatasetsResource.datasets+"/{id}/feature_definition/","add feature definition",format,"POST","No"},
			{DatasetsResource.datasets+"/{id}/feature_definition/{fdid}","update feature definition",format,"PUT","No"},
			{DatasetsResource.datasets+"/{id}/feature_definition/{fdid}","remove feature_definition",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/dataset","Actions on datasets (split, merge, subset)",formatHeader,null,"Implemented"},
			{"?","?",format,"?","No"},

			{"http://opentox.org/dev/apis/feature","References",formatHeader,null},
			{ReferenceResource.reference,"read all references",format,"GET","Yes"},
			{ReferenceResource.reference+"/11845","read information on a specific reference",format,"GET","Yes"},
			{ReferenceResource.reference,"create a new referenc",format,"POST","Yes"},
			{ReferenceResource.reference+"/11845","update information on a specific reference",format,"PUT","No"},
			
			{"http://opentox.org/dev/apis/feature","Features",formatHeader,null},
			{"/feature/compound/1/feature_definition/1","get the value for a specific feature",format,"GET","Yes"},
			{"/feature/compound/1/feature_definition","get values for all features ",format,"GET","No"},
			{"/feature/dataset/1/feature_definition/1","get the value for all compounds in a dataset for a given feature definition in a dataset",format,"GET","No"}, 
			{"/feature/feature_definition/{fid}","get the value for a all compounds for a given feature definition",format,"GET","No"},
			
			{"/feature/compound/{cid}/feature_definition/{f_def_id}","update the value for a specific feature",format,"PUT","No"},
			{"/feature/compound/{cid}/conformer/{cid}/feature_definition/{f_def_id}","update the value for a specific feature",format,"PUT","Yes"},
			{"/feature/compound/{cid}/feature_definition/{f_def_id}","save the value for a given feature per compound",format,"POST","No"},
			{"/feature/compound/{cid}/conformer/{cid}/feature_definition/{f_def_id}","save the value for a given feature per conformer",format,"POST","Yes"},
			{"/feature/compound/{cid}/feature_definition","save the value for a new feature per compound",format,"POST","No"},
			{"/feature/compound/{cid}/conformer/{cid}/feature_definition","save the value for a given feature per conformer",format,"POST","No"},
			{"feature/compound/{cid}/feature_definition/{f_def_id}","Delete feature value",format,"DELETE","No"},

			{"(to be discussed)","Feature tuples (PROPOSAL)",formatHeader,null},
			{"/compound/1/tuple","All available feature tuples",format,"GET","Yes"},
			{"/compound/1/tuple/264168","Specific feature tuple",format,"GET","Yes"},
			{"TODO","create/update/delete",format,"POST/PUT/DELETE","Under development"},
			
			{"http://www.opentox.org/dev/apis/Algorithm","Algorithms",formatHeader,null},
			{AlgorithmResource.algorithm,"All types of algorithms",format,"GET","Yes"},
			{String.format("%s/%s",AlgorithmResource.algorithm,AlgorithmResource.algorithmtypes.descriptorcalculation.toString()),"Descriptor calculation algorithms",format,"GET","Under development"},
			{String.format("%s/%s",AlgorithmResource.algorithm,"preprocessing"),"Feature preprocessing",format,"GET","Under development"},
			{String.format("%s/%s",AlgorithmResource.algorithm,"Learinng algorithms"),"Learning algorithms",format,"GET","Under development"},
			{String.format("%s/%s",AlgorithmResource.algorithm,"clustering"),"Clustering algorithms",format,"GET","Under development"},
			{String.format("%s/%s",AlgorithmResource.algorithm,"util"),"Utility algorithms",format,"GET","Under development"},
			
			{"http://opentox.org/dev/apis/Models","Models",formatHeader,null},
			{String.format("%s",ModelResource.resource),"get a list of all available models",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"get the representation of a model",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"delete a model",format,"DELETE","No"},
			{String.format("%s/{id}",ModelResource.resource),"apply a model to a dataset for prediction",format,"POST","Test implementation only"},
					

			{"http://opentox.org/dev/apis/feature-ontology","Feature ontology (PROPOSAL)",formatHeader,null},
			{"/template/Endpoints","Hierarchical view of endpoints",format,"GET","Yes"},
			{"/template/Descriptors","Hierarchical view of descriptors",format,"GET","Yes"},
			{"/template/Dataset","Hierarchical view of features in datasets",format,"GET","Yes"},
			{"/template/Identifiers","Hierarchical view of identifiers",format,"GET","Yes"},
			

			{"(to be discussed)","Dataset search",formatHeader,null,"Implemented"},			
			{"/dataset?search=Skin","Search for datasets by name",format,"GET","Yes"},

			{"(to be discussed)","Compound search",formatHeader,null,"Implemented"},			
			{"/compound?search=55-55-0","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=phenolphthalein","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=NTP465","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=KJFMBFZCATUALV-UHFFFAOYAH","Search for a compound by any property value",format,"GET","Yes"},
			{"/query/feature/=/50-00-0","Search by property (another option)",format,"GET","Yes"},
			{"/query/feature/like/phenol","Search by property (another option)",format,"GET","Yes"},
			{String.format("/query/smarts?search=%s",Reference.encode("[NX3][CX3](=[OX1])[#6]")),"Search by SMARTS NX3][CX3](=[OX1])[#6]",format,"GET","Under development"},
			{"/query/qlabel?search=ProbablyERROR","Search compounds by Quality Labels",format,"GET","Yes"},
			{"/query/similarity/method/fp1024/distance/tanimoto/0.5/smiles/c1ccccc1","Demo similarity search, to be refactored",format,"GET","Yes"},
			{QueryResource.query_resource,"List available search options",format,"GET","Under development"},
			
			{"http://opentox.org/dev/apis/dataset","Search results as datasets; remap into Datasets",formatHeader,null,"Implemented"},
			{"/query/results/1","Display previous search results",format,"GET","Yes"},
			{"/query/results","Save a search result",format,"POST","Under development"},
			{"/query/results/{id}","Delete a search result",format,"DELETE","Under development"},
			{"/query/results/{id}","Update a search result",format,"PUT","Under development"},
			
			{"(to be discussed)","Feature definition search",formatHeader,null,"Implemented"},			
			{PropertyResource.featuredef+"?search=LogP","Search for a feature definition by name",format,"GET","Yes"},
			
			{"(to be discussed)","Reference search",formatHeader,null,"Implemented"},			
			{ReferenceResource.reference+"?search={query}","Search for a reference by name",format,"GET","Under development"},

			
			{"(OpenTox wiki to be updated)","Asynchronous jobs",formatHeader,null},
			{"/task","List of asynchronous jobs and their status",format,"GET","Yes"},
			{"/task/{task id}","Information about a specific task",format,"GET","Yes"},			

			{"(to be discussed/remapped into algorithms)","Structure diagram generation (DEMO)",formatHeader,null},
			{"/depict/cdk?search=c1ccccc1","Structure diagram (based on CDK)",format,"GET"},
			{"/depict/daylight?search=c1ccccc1","Structure diagram (based on Daylight depict",format,"GET"},

			{"(to be discussed/remapped into algorithms)","Generation of 3D structure (DEMO)",formatHeader,null},
			{"/build3d/smiles/c1ccccc1","Generate 3D structure given a smiles",format,"GET","Under development"},
		
			{"(to be discussed)","PubChem query (DEMO)",formatHeader,null},
			{"/query/pubchem/50-00-0","Queries PubChem for specific term and retrieves structure(s) as SDF file",format,"GET","Yes"}
			
	};
	public AmbitResource() {
		super();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		
		//consumer = new DatasetConsumer();

	}
	
	protected String getSearchString() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue("search");
		if (key != null) {
	        return Reference.decode(key.toString());
		} else return null;		
	}
	
	@Override
	public Representation getRepresentation(Variant variant) {
		try {
			String search = getSearchString();
			if (search != null) {
				getResponse().setLocationRef(String.format("%s/compound?search=%s",getRequest().getRootRef(),Reference.encode(search)));
				getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				return null;
			}
		} catch (Exception x) {}
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
				StringWriter writer = new StringWriter();
				writeHTMLHeader(writer, "AMBIT", getRequest().getRootRef());
				writer.write("<table border='0'>");
				writer.write(String.format("<tr align='center'><th colspan='4'>%s</th></tr>",
						"Services listed below are an initial implementation of <a href=\"http://opentox.org/dev/apis\" target=\"blank\">OpenTox REST API</a>"+
						"<h6>All services support MIME types text/xml, text/html, text/uri-list on GET; services providing chemical structures support Chemical MIME types as well</h6>"));
				
				for (String[] s:uri) {
					//writer.write("<li>");
					writer.write(String.format(s[2],
							s[1],
							getRequest().getRootRef().toString(),
							s[0],
							s[0],
							s[3]==null?"REST operation":s[3],
							(s.length<5)?"":
										s[4]==null?"":
										s[4].equals("Yes")?String.format("<img src='%s/images/tick.png' alt='YES' title='YES'/>",getRequest().getRootRef()):
										s[4].equals("No")?String.format("<img src='%s/images/cross.png' alt='NO' title='NO'/>",getRequest().getRootRef()):s[4]
										));
					
					/*
					writer.write(s[1]);
					writer.write("&nbsp;<a href=\"");
					writer.write(getRequest().getRootRef().toString());
					writer.write(s[0]);
					writer.write("\">");
					writer.write(s[0]);
					writer.write("</a>");
					writer.write("\n");
					*/
				}
				writer.write("</table>");
				writeHTMLFooter(writer, "AMBIT", getRequest().getRootRef());
				return new StringRepresentation(writer.toString(),MediaType.TEXT_HTML);				
			}
			
		} catch (Exception x) {
			x.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}
	
	public static void writeHTMLHeader(Writer w,String title,Reference baseReference) throws IOException {
		writeHTMLHeader(w, title, baseReference,"");
	}
	public static void writeHTMLHeader(Writer w,String title,Reference baseReference,String meta) throws IOException {

		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		w.write(String.format("<html><head><title>%s</title>%s\n",title,meta));
		w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
		w.write("<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,FOLLOW\">");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		w.write("</head>\n");
		w.write("<body>\n");
		w.write("<div style= \"width: 100%; background-color: #516373;");
		w.write("border: 1px solid #333; padding: 0px; margin: 0px auto;\">");
		w.write("<div class=\"spacer\"></div>");

		w.write(String.format("<div class=\"row\"><span class=\"left\"><a href=\"%s\">Home</a></span>",baseReference.toString()));
		w.write("	<span class=\"right\"><a href=''>Login</a></span></div>");
		w.write("	<div class=\"spacer\"></div>");
		w.write("</div>");
		w.write("<div>");		
		w.write(String.format("<a href='%s/compound'>Chemical&nbsp;compounds</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/similarity'>Similar&nbsp;structures</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/substructure'>Substructure</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/smarts'>SMARTS&nbsp;patterns</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s%s/Endpoints'>Endpoints</a>&nbsp;",baseReference,OntologyResource.resource));
		w.write(String.format("<a href='%s/dataset'>Datasets</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/algorithm'>Algorithms</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s%s'>References</a>&nbsp;",baseReference,ReferenceResource.reference));
		w.write(String.format("<a href='%s%s'>Feature definitions</a>&nbsp;",baseReference,PropertyResource.featuredef));
		w.write(String.format("<a href='%s/model'>Models</a>&nbsp;",baseReference));
		w.write("</div>");
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("<td align='center'>");

		w.write("<form action='' method='get'>\n");
		w.write("<input name='search' size='80'>\n");
		w.write("<input type='submit' value='Search'><br>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
		w.write("<br><b><i>This site and AMBIT REST services are under development!</i></b>");		
		w.write("</td>");
		w.write("<td align='right' width='256px'>");
		w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));

		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
	}
	public static void writeHTMLFooter(Writer output,String title,Reference baseReference) throws IOException {
		output.write("<div class=\"footer\"><span class=\"right\">");
		output.write("<font color='#D6DFF7'>");
		output.write("Developed by Ideaconsult Ltd. (2005-2009)"); 
		output.write("</font><br>");
		output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>&nbsp; ");

		output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>");
		output.write("</span></div>");
		output.write("</body>");
		output.write("</html>");

	}	
}
