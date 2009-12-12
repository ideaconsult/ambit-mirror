package ambit2.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.template.OntologyResource;

public class AmbitResource extends ServerResource {
	String format = "<tr ><td>%s</td><td><a href=\"%s%s\">%s</a></td><td>%s</td><td>%s</td></tr>";
	String formatHeader = "<tr bgcolor=\"#EEEEEE\" align=\"left\"><th>%s</th><th %s>API <a href=\"%s\" target=\"_blank\">%s</a></th><th>%s</th><th>%s</th></tr>";
	protected String[][] uri = {
			
			{"http://opentox.org/dev/apis/api-1.1/structure","Chemical compounds",formatHeader,null,"Implemented"},
			{String.format("%s/%d",CompoundResource.compound,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s",CompoundResource.compound),"create a new chemical compound",format,"POST","No"},
			{String.format("%s/%d",CompoundResource.compound,100),"Update the representation of chemical compound",format,"PUT","No"},
			{String.format("%s/%d",CompoundResource.compound,100),"Remove chemical compound",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/api-1.1/structure","Conformers",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"update the representation of a structure",format,"PUT","No"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100)," Remove structure",format,"DELETE","No"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"get available conformers of a chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"create a new structure",format,"POST","No"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"Remove conformers",format,"DELETE","No"},

			{"[ambit]","Compound properties",formatHeader,null,"Implemented"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"update the value for a specific feature",format,"PUT","Yes"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"update the value for a specific feature",format,"PUT","Yes"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per compound",format,"POST","Yes"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per conformer",format,"POST","Yes"},
			{String.format("%s/compound/{cid}%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a new feature per compound",format,"POST","No"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per conformer",format,"POST","No"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"Delete feature value",format,"DELETE","No"},
			
			{"http://opentox.org/dev/apis/api-1.1/dataset","Datasets",formatHeader,null,"Implemented"},
			{DatasetsResource.datasets,"get list of datasets available",format,"GET","Yes"},
			{DatasetsResource.datasets+"?search=Skin","Search for datasets by name",format,"GET","Yes"},			
			{DatasetsResource.datasets,"create a new dataset",format,"POST","Yes"},
			{"/dataset/{id}","update dataset",format,"PUT","No"},
			{"/dataset/{id}","remove dataset",format,"DELETE","No"},
			
			{"http://opentox.org/dev/apis/dataset","A Dataset",formatHeader,null,"Implemented"},
			{DatasetResource.dataset+"/8","get dataset",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/metadata","get dataset metadata",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/features","get dataset features",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/compound","get compounds only",format,"GET","Yes"},	
			
			{"[ambit]","Chemical compounds in a dataset",formatHeader,null,"Implemented"},			
			{DatasetResource.dataset+"/8/compound/413","get compound",format,"GET","Yes"},
			{DatasetResource.dataset+"/{id}/compound","add compound",format,"POST","No"},
			{DatasetResource.dataset+"/{id}/compound/{cid}","update compound",format,"PUT","No"},
			{DatasetResource.dataset+"/{id}/compound/{cid}","remove compound from a dataset",format,"DELETE","No"},
			{DatasetResource.dataset+"/{id}/compound","remove all compounds in a dataset",format,"DELETE","No"},

			{"[ambit]","Conformers in a dataset",formatHeader,null,"Implemented"},
			{DatasetResource.dataset+"/8/compound/413/conformer","get conformers",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/compound/413/conformer/100617","get conformer",format,"GET","Yes"},
			{DatasetResource.dataset+"/{id}/compound/{cid}","add conformer",format,"POST","No"},
			{DatasetResource.dataset+"/{id}/compound/{cid}/conformer/{id}","update conformer",format,"PUT","No"},
			{DatasetResource.dataset+"/{id}/compound/{cid}/conformer/{id}","remove conformer from the dataset",format,"DELETE","No"},
			{DatasetResource.dataset+"/{id}/compound/{cid}","remove all conformers",format,"DELETE","No"},

			{"http://opentox.org/development/wiki/dataset","Features in a dataset",formatHeader,null,"Implemented"},
			{String.format("%s/8%s",DatasetResource.dataset,PropertyResource.featuredef),"get features available in the dataset",format,"GET","Yes"},

			{"http://opentox.org/dev/apis/api-1.1/Feature","Features",formatHeader,null},
			{String.format("%s?%s=LogP",PropertyResource.featuredef,QueryResource.search_param),"Search for a feature by name",format,"GET","Yes"},
			{PropertyResource.featuredef,"Retrieve all features",format,"GET","Yes"},
			{PropertyResource.featuredef+"/12142","Get description of a specific feature",format,"GET","Yes"},
			{PropertyResource.featuredef,"Create a new feature",format,"POST","Yes"},
	
			{"http://opentox.org/dev/apis/api-1.1/structure","Features per compound/conformer",formatHeader,null,"Implemented"},
			{String.format("%s/%d?feature_uris[]=%s",CompoundResource.compound,100,"featureuri"),"features and values ",format,"GET","Yes"},
			

			{"http://www.opentox.org/dev/apis/Algorithm","Algorithms",formatHeader,null},
			{String.format("%s",AlgorithmResource.algorithm),"get a list of all available algorithms",format,"GET","Yes"},
			{String.format("%s/pka",AlgorithmResource.algorithm),"get the representation of an pKa algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecramer",AlgorithmResource.algorithm),"get the representation of an \"ToxTree: Cramer rules\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecramer2",AlgorithmResource.algorithm),"get the representation of an \"ToxTree: Extended Cramer rules\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreeeye",AlgorithmResource.algorithm),"get the representation of an \"ToxTree: Eye irritation\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreeskinirritation",AlgorithmResource.algorithm),"get the representation of an \"ToxTree: Skin irritation\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreemic",AlgorithmResource.algorithm),"get the representation of \"ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreemichaelacceptors",AlgorithmResource.algorithm),"get the representation of \"ToxTree: Michael acceptors\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecarc",AlgorithmResource.algorithm),"get the representation of \"ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreekroes",AlgorithmResource.algorithm),"get the representation of \"ToxTree: ILSI/Kroes decision tree for TTC\" algorithm",format,"GET","Yes"},
			{String.format("%s/pka",AlgorithmResource.algorithm),"apply a model to a dataset for prediction",format,"POST","Yes"},
					
			{"http://opentox.org/dev/apis/Model","Models",formatHeader,null},
			{String.format("%s",ModelResource.resource),"get a list of all available models",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"get the representation of a model",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"delete a model",format,"DELETE","No"},
			{String.format("%s/{id}",ModelResource.resource),"apply a model to a dataset for prediction",format,"POST","Yes"},

			{"http://opentox.org/dev/apis/api-1.1/AsyncTask","Asynchronous jobs",formatHeader,null},
			{"/task","List of asynchronous jobs and their status",format,"GET","Yes"},
			{"/task/{task id}","Information about a specific task",format,"GET","Yes"},		
			
			{"[ambit]","Features per compound/conformer",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s",CompoundResource.compound,100,PropertyResource.featuredef),"All feature definitions, available for a compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100,PropertyResource.featuredef),"All feature definitions, available for a conformer",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100304,PropertyResource.featuredef),"All feature definitions, available for a conformer",format,"GET","Yes"},
			
			{"[ambit]","Actions on datasets (split, merge, subset)",formatHeader,null,"Implemented"},
			{String.format("%s/8?%s=%s",DatasetResource.dataset,DatasetResource.dataset_intersection_uri,
							"datasetURI"),
							"intersection of two datasets (only compounds common to both datasets)",format,"GET","Yes"},
			{String.format("%s/8?%s=%s",DatasetResource.dataset,DatasetResource.dataset_complement_uri,
							"datasetURI"),"Finds compounds which are in the dataset 8, but not in the second dataset",format,"GET","Yes"},
					
			{"[ambit]","Features in a dataset",formatHeader,null,"Implemented"},
			{String.format("%s/8%s/12142",DatasetResource.dataset,PropertyResource.featuredef),"get specific feature",format,"GET","Yes"},
			{String.format("%s/{id}%s",DatasetResource.dataset,PropertyResource.featuredef),"add feature",format,"POST","No"},
			{String.format("%s/{id}%s/{fdid}",DatasetResource.dataset,PropertyResource.featuredef),"update feature ",format,"PUT","No"},
			{String.format("%s/{id}%s/{fdid}",DatasetResource.dataset,PropertyResource.featuredef),"remove feature ",format,"DELETE","No"},

			{"[ambit]","References",formatHeader,null},
			{ReferenceResource.reference,"read all references",format,"GET","Yes"},
			{ReferenceResource.reference+"/11845","read information on a specific reference",format,"GET","Yes"},
			{ReferenceResource.reference,"create a new reference",format,"POST","Yes"},
			{ReferenceResource.reference+"/11845","update information on a specific reference",format,"PUT","No"},
			{ReferenceResource.reference+"?search={query}","Search for a reference by name",format,"GET","Under development"},
					

			{"[ambit]","Model predictions (PROPOSAL)",formatHeader,null},
			{"/compound/1/model","All available model predictions",format,"GET","Yes"},
			{"/compound/1/model/3","Specific model prediction",format,"GET","Yes"},
			
			{"[ambit]","Feature ontology (PROPOSAL)",formatHeader,null},
			{String.format("%s/All/Endpoints",OntologyResource.resource),"Hierarchical view of endpoints",format,"GET","Yes"},
			{String.format("%s/All/Descriptors",OntologyResource.resource),"Hierarchical view of descriptors",format,"GET","Yes"},
			{String.format("%s/All/Dataset",OntologyResource.resource),"Hierarchical view of features in datasets",format,"GET","Yes"},
			{String.format("%s/All/Identifiers",OntologyResource.resource),"Hierarchical view of identifiers",format,"GET","Yes"},
			{String.format("%s/{subject}/{object}",OntologyResource.resource),"Delete entry",format,"DELETE","Yes"},
			
			{"[ambit]","Feature values",formatHeader,null},
			{String.format("%scompound/1%s/1",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for a specific feature",format,"GET","Yes"},
			{String.format("%s/compound/1%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"get values for all features ",format,"GET","No"},
			{String.format("%s/dataset/1%s/1",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for all compounds in a dataset for a given feature definition in a dataset",format,"GET","No"}, 
			{String.format("%s%s/{fid}",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for a all compounds for a given feature definition",format,"GET","No"},

			{"Data entries","Data entries",formatHeader,null},
			{"/compound/1/dataEntry","All available data entries per compound",format,"GET","Yes"},
			{"/compound/1/dataEntry/264168","Specific data entry",format,"GET","Yes"},
			{"TODO","create/update/delete",format,"POST/PUT/DELETE","Under development"},
			
			{"(to be discussed)","Compound search",formatHeader,null,"Implemented"},			
			{"/compound?search=55-55-0","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=phenolphthalein","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=NTP465","Search for a compound by any property value",format,"GET","Yes"},
			{"/compound?search=KJFMBFZCATUALV-UHFFFAOYAH","Search for a compound by any property value",format,"GET","Yes"},
			{String.format("/query%s/=/50-00-0",PropertyValueResource.featureKey),"Search by property (another option)",format,"GET","Yes"},
			{String.format("/query%s/like/phenol",PropertyValueResource.featureKey),"Search by property (another option)",format,"GET","Yes"},
			{String.format("/query/smarts?search=%s",Reference.encode("[NX3][CX3](=[OX1])[#6]")),"Search by SMARTS NX3][CX3](=[OX1])[#6]",format,"GET","Under development"},
			{"/query/qlabel?search=ProbablyERROR","Search compounds by Quality Labels",format,"GET","Yes"},
			{"/query/similarity?search=c1ccccc1&threshold=0.8","Similarity search",format,"GET","Yes"},
			{"/query/smarts?search=c1ccccc1O","SMARTS search",format,"GET","Yes"},
			{QueryResource.query_resource,"List available search options",format,"GET","Under development"},
			{"[ambit - algorithms]","Structure diagram generation (DEMO)",formatHeader,null},
			{"/depict/cdk?search=c1ccccc1","Structure diagram (based on CDK)",format,"GET"},
			{"/depict/daylight?search=c1ccccc1","Structure diagram (based on Daylight depict",format,"GET"},
			{"/build3d?search=c1ccccc1","Generate 3D structure given a smiles",format,"GET","Under development"},
			{"/query/pubchem/50-00-0","Queries PubChem for specific term and retrieves structure(s) as SDF file",format,"GET","Yes"},
		
			
			{"http://opentox.org/dev/apis/dataset","Search results as datasets; remap into Datasets",formatHeader,null,"Implemented"},
			{"/query/results/1","Display previous search results",format,"GET","Yes"},
			{"/query/results","Save a search result",format,"POST","Under development"},
			{"/query/results/{id}","Delete a search result",format,"DELETE","Under development"},
			{"/query/results/{id}","Update a search result",format,"PUT","Under development"},
			

	
	};

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
        List<Variant> variants = new ArrayList<Variant>();
        variants.add(new Variant(MediaType.TEXT_HTML));
        variants.add(new Variant(MediaType.TEXT_XML));
        variants.add(new Variant(MediaType.TEXT_URI_LIST));
        variants.add(new Variant(MediaType.TEXT_PLAIN));
        getVariants().put(Method.GET, variants);
	
	}
	protected String getSearchString() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String[] keys = form.getValuesArray(QueryResource.search_param);
		if ((keys==null)||(keys.length==0)) return null;
		StringBuilder b = new StringBuilder();
		for (String key:keys) {
			b.append(Reference.decode(key.toString()));
			b.append(" ");
		} 
		return b.toString();		
	}
	
	@Override
	public Representation get(Variant variant) {
		System.out.println(getRequest().getClientInfo().isAuthenticated());
		System.out.println(getRequest().getClientInfo().getSubject().getPrincipals());
		try {
			//TODO redirect with freetext query
			String search = getSearchString();
			if (search != null) {
				getResponse().setLocationRef(String.format("%s/query/smarts?text=%s",getRequest().getRootRef(),Reference.encode(search)));
				getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				return null;
			}
		} catch (Exception x) {}

		try {
			if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			     StringWriter w = new StringWriter();
			     AmbitApplication.printRoutes(getApplication().getRoot(),">",w);
				return new StringRepresentation(w.toString());		
			} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
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
				writeHTMLHeader(writer, "AMBIT", getRequest());
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
				writeHTMLFooter(writer, "AMBIT", getRequest());
				return new StringRepresentation(writer.toString(),MediaType.TEXT_HTML);				
			}
			
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}
	
	public static void writeHTMLHeader(Writer w,String title,Request request) throws IOException {
		writeHTMLHeader(w, title, request,"");
	}
	public static void writeHTMLHeader(Writer w,String title,Request request,String meta) throws IOException {

		writeTopHeader(w, title, request, meta);
		writeSearchForm(w, title, request, meta);
		
	}
	public static String js() {
		String s = 
		"function getURL(url, contentType) {\n"+
		"	 var client = new XMLHttpRequest();\n"+
		"	 client.open(\"GET\", url)\n"+
		"	 client.setRequestHeader(\"Accept\", contentType);"+
		"	 client.send();\n"+
		"	client.onreadystatechange=function() {\n"+
		"       if (client.readyState==4) {\n"+
		"			headers.put('content-type', contentType)	\n"+
		"			document.write(client.responseText); }"+
		"	}\n}";
		return s;

	}
	public static String jsHTTPObject() {
		String s = 
			"\nfunction getHTTPObject() {\n"+
			"if (typeof XMLHttpRequest != 'undefined') { return new XMLHttpRequest(); }\n"+
			"try { return new ActiveXObject(\"Msxml2.XMLHTTP\"); } catch (e) {\n"+
			"try { return new ActiveXObject(\"Microsoft.XMLHTTP\"); } catch (e) {} } return false; }\n";
		return s;

	}
	public static String jsLogout() {
		String s = 
			"\nfunction logout() { "+
			"var http = getHTTPObject();\n"+ 
			"var username = \"guest\";\n"+
			"var password = \"guest\";\n"+
			"http.open(\"get\", \"\", false, username, password);\n"+
			"http.send(\"\"); \n"+
			"	http.onreadystatechange=function() {\n"+
			"       if (client.readyState==4) {\n"+
			"			headers.put('Authorization', '')	\n"+
			"			document.write(client.responseText); }"+
			"	}\n}";			
			//"if (http.status == 200) { document.location = this.action; alert(username); } \n"+
			//"else { alert(\"Incorrect username and/or password.\"); } return false; }\n";
		return s;

	}		
	public static String jsLogin() {
		String s = 
			"\nfunction login() { "+
			"var username = document.getElementById(this.id + \"-username\").value;\n"+
			"var password = document.getElementById(this.id + \"-password\").value;\n"+
			"this.http.open(\"get\", this.action, false, username, password);\n"+
			"this.http.send(\"\"); \n"+
			"if (http.status == 200) { document.location = this.action; } \n"+
			"else { alert(\"Incorrect username and/or password.\"); } return false; }\n";
		return s;

	}	
	public static void writeTopHeader(Writer w,String title,Request request,String meta) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		w.write(String.format("<html><head><title>%s</title>%s\n",title,meta));
		w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
		w.write("<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,FOLLOW\">");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		//w.write(String.format("<script type=\"text/javascript\" src=\"%s/js/dojo.js.uncompressed\" djConfig=\"parseOnLoad:true, isDebug:true\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/js/jquery-1.3.2.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jme/jme.js\"></script>\n",baseReference));
//		w.write("<script language=\"JavaScript\">\nvar smiles = \"\";\n var jme = \"0 0\"></script>\n");


		w.write("</head>\n");
		w.write("<body>\n");
		w.write("<div style= \"width: 100%; background-color: #516373;");
		w.write("border: 1px solid #333; padding: 0px; margin: 0px auto;\">");
		w.write("<div class=\"spacer\"></div>");

		w.write(String.format("<div class=\"row\"><span class=\"left\"><a href=\"%s\">Home</a>",baseReference.toString()));
		w.write("</span>");
		Iterator<Principal> i = request.getClientInfo().getSubject().getPrincipals().iterator();
		Principal p = null;
		while (i.hasNext()) { p = i.next(); break; }
		if (p==null)
		w.write(String.format("	<span class=\"right\"><a href='%s/user/login'>Login</a>",
				baseReference.toString()));
		else
		w.write(String.format("	<span class=\"right\">%s&nbsp;<a href='%s/protected/%s'>Switch user</a>",
				p.getName(),baseReference.toString(),p.getName()));
		
		//w.write(String.format("&nbsp;<a href=\"%s/help\">Help</a>",baseReference.toString()));
		w.write("</span></div>");
		w.write("	<div class=\"spacer\"></div>");
		w.write("</div>");
		w.write("<div>");		
		
		w.write(String.format("<a href='%s/fasttox?text=50-00-0&search=%s' title='Predict'>FastTox</a>&nbsp;",baseReference,Reference.encode("C=O")));
		w.write(String.format("<a href='%s/ttc?text=50-00-0&search=%s' title='Thresholf of toxicological concern prediction'>TTC</a>&nbsp;",baseReference,Reference.encode("C=O")));
		w.write(String.format("<a href='%s/depict?search=c1ccccc1' title='Structure diagram'>Depiction</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/dataset'>Datasets</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/compound'>Chemical&nbsp;compounds</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/similarity?search=c1ccccc1Oc2ccccc2&threshold=0.9' title='Search for similar structures'>Similarity</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/smarts?text=\"\"' title='Substructure search by SMARTS patterns'>Substructure</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/algorithm' title='Predictive algorithms'>Algorithms</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s%s'>References</a>&nbsp;",baseReference,ReferenceResource.reference));
		w.write(String.format("<a href='%s%s' title='Compound properties'>Features</a>&nbsp;",baseReference,PropertyResource.featuredef));
		w.write(String.format("<a href='%s%s/Taxonomy' title='Features grouped in several categories'>Templates</a>&nbsp;",baseReference,OntologyResource.resource));		
		w.write(String.format("<a href='%s/model'>Models</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/ontology' title='BlueObelisk, endpoints, algorithm types ontology'>Ontology</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/ontology/test' title='Reads RDF output from an URI and displays clickable statements. Enter URI in the search box.'>RDF playground</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/help'>Help</a>&nbsp;",baseReference));
		w.write("</div>");

	
	}
	
	public static void writeSearchForm(Writer w,String title,Request request ,String meta) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("<td align='center'>");
		String query_smiles = "";
		try {
			Form form = request.getResourceRef().getQueryAsForm();
			query_smiles = form.getFirstValue(QueryResource.search_param);
		} catch (Exception x) {
			query_smiles = "";
		}
		w.write("<form action='' method='get'>\n");
		w.write(String.format("<input name='%s' size='80' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
		w.write("<input type='submit' value='Search'><br>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
		w.write("<br><b><i>This site and AMBIT REST services are under development!</i></b>");		
		w.write("</td>");
		w.write("<td align='right' width='256px'>");
//		w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));

		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
	}	
	public static void writeHTMLFooter(Writer output,String title,Request request) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		output.write("<div class=\"footer\">");

		output.write("<span class=\"right\">");
		output.write(String.format("<a href='http://www.cefic.be'><img src=%s/images/logocefic.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		output.write(String.format("<a href='http://www.cefic-lri.org'><img src=%s/images/logolri.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		output.write(String.format("<a href='http://www.opentox.org'><img src=%s/images/logo.png border='0' width='115' height='60'></a>",baseReference));
		output.write("<br>Developed by Ideaconsult Ltd. (2005-2009)"); 
		output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>&nbsp; ");
		output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>");

		output.write("</span>");		
		output.write("</div>");
		output.write("</body>");
		output.write("</html>");

	}	
}
