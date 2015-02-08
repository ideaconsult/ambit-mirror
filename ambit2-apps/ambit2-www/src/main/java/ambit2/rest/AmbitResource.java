package ambit2.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.freemarker.FreeMarkerResource;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.sparqlendpoint.SPARQLPointerResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.smirks.ReactionDepict;
import ambit2.rest.structure.tautomers.TautomersDepict;
import ambit2.rest.template.OntologyResource;

/**
 * window.setInterval(function() {
 alert('test');
}, 1000);

 * @author nina
 *
 */
public class AmbitResource extends FreeMarkerResource {
	protected static String sparqlEndpoint = null;
	protected static String jsGoogleAnalytics = null;
	String format = "<tr ><td>%s</td><td><a href=\"%s%s\">%s</a></td><td>%s</td><td>%s</td></tr>";
	String formatHeader = "<tr bgcolor=\"#EEEEEE\" align=\"left\"><th>%s</th><th %s>API <a href=\"%s\" target=\"_blank\">%s</a></th><th>%s</th><th>%s</th></tr>";
	
	//meta
	private final static String[] metaTag = new String[] {
		"<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,noFOLLOW\">\n",
		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n",
	};
	
	private final static String[] css = new String[] {
		"<!--[if IE 7]><link rel='stylesheet' type='text/css' media='all' href='%s/style/ambit-msie7.css'><![endif]-->",
		"<link href=\"%s/style/jquery-ui-1.10.4.custom.min.css\" rel=\"stylesheet\" type=\"text/css\">\n",

		"<link href=\"%s/style/jquery.ui.theme.css\" rel=\"stylesheet\" type=\"text/css\">\n",
		
		"<link href=\"%s/style/jquery.dataTables.css\" rel=\"stylesheet\" type=\"text/css\">\n",
		"<link href=\"%s/style/skeleton/table.css\" rel=\"stylesheet\" type=\"text/css\">\n",
		"<link href=\"%s/style/skeleton/ambit2.css\" rel=\"stylesheet\" type=\"text/css\">\n",
		"<link href=\"%s/images/favicon.ico\" rel=\"shortcut icon\" type=\"image/ico\">\n",
	};

	private final static String[] js = new String[] {
		"<script type='text/javascript' src='%s/jquery/jquery-1.10.2.js'></script>\n",
		"<script type='text/javascript' src='%s/jquery/jquery-ui-1.10.4.custom.min.js'></script>\n",
		"<script type='text/javascript' charset='utf8' src='%s/jquery/jquery.dataTables-1.9.4.min.js'></script>\n",
		"<script type='text/javascript' src='%s/scripts/jopentox.js'></script>\n",
		"<script type='text/javascript' src='%s/scripts/jopentox-ui-model.js'></script>\n",
		"<script type='text/javascript' src='%s/scripts/jopentox-ui-feature.js'></script>\n",
		"<script type='text/javascript' src='%s/scripts/jopentox-ui-misc.js'></script>\n",
		"<script type='text/javascript' src='%s/scripts/jopentox-ui-compound.js'></script>\n",
		//"<script type='text/javascript' src='%s/scripts/jendpoints.js'></script>\n",
		//"<script type=\"text/javascript\" src=\"%s/jquery/jquery.MultiFile.pack.js\"></script>\n",
		"<script type='text/javascript' src='%s/jme/jme.js'></script>\n",
		"<script type='text/javascript' src='%s/jmol/Jmol.js'></script>\n",
	};

	protected String[][] uri = {
			
			{"http://opentox.org/dev/apis/api-1.2/structure","Chemical compounds",formatHeader,null,"Implemented"},
			{String.format("%s/%d",CompoundResource.compound,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s",CompoundResource.compound),"create a new chemical compound",format,"POST","Yes"},
			{String.format("%s/%d",CompoundResource.compound,100),"Update the representation of chemical compound",format,"PUT","No"},
			{String.format("%s/%d",CompoundResource.compound,100),"Remove chemical compound",format,"DELETE","No"},

			{"http://opentox.org/dev/apis/api-1.2/structure","Chemical compounds query",formatHeader,null,"Implemented"},
			
			{"/query/compound/55-55-0/all","Search for a compound by any property value",format,"GET","Yes"},
			{"/query/compound/phenolphthalein/all","Search for a compound by any property value",format,"GET","Yes"},
			{String.format("/query/compound/search/all?search=%s","formaldehyde"),"Search for a compound by name",format,"GET","Yes"},
			{String.format("/query/compound/search/all?search=%s",Reference.encode("C=O")),"Search for a compound by SMILES",format,"GET","Yes"},
			{String.format("/query/compound/search/all?search=%s",Reference.encode("InChI=1S/CH2O/c1-2/h1H2")),"Search for a compound by InChI",format,"GET","Yes"},

			{String.format("/compound?property=%s&search=%s",Reference.encode("species common name"),Reference.encode("common carp")),
					"Search for a compound with data for species 'common carp'",format,"GET","Yes"},
			{String.format("/compound?property=%s&search=%s",Reference.encode("LogP"),Reference.encode("7 .. 8")),
						"Search for a compound with property value of LogP between 7 and 8",format,"GET","Yes"},

			
			{"http://opentox.org/dev/apis/api-1.2/structure","Conformers",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"Get the representation of chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"update the representation of a structure",format,"PUT","No"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100)," Remove structure",format,"DELETE","Yes"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"get available conformers of a chemical compound",format,"GET","Yes"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"create a new structure",format,"POST","No"},
			{String.format("%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey),"Remove conformers",format,"DELETE","Yes"},

			/*
			{"[ambit]","Compound properties",formatHeader,null,"Implemented"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"update the value for a specific feature",format,"PUT","Yes"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"update the value for a specific feature",format,"PUT","Yes"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per compound",format,"POST","Yes"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per conformer",format,"POST","Yes"},
			{String.format("%s/compound/{cid}%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a new feature per compound",format,"POST","No"},
			{String.format("%s/compound/{cid}/conformer/{cid}%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"save the value for a given feature per conformer",format,"POST","No"},
			{String.format("%s/compound/{cid}%s/{f_def_id}",PropertyValueResource.featureKey,PropertyResource.featuredef),"Delete feature value",format,"DELETE","No"},
			*/
			
			{"http://opentox.org/dev/apis/api-1.2/dataset","Datasets",formatHeader,null,"Implemented"},
			{DatasetsResource.datasets,"get list of datasets available",format,"GET","Yes"},
			{DatasetsResource.datasets+"?search=Skin","Search for datasets by name",format,"GET","Yes"},			
			{DatasetsResource.datasets,"create a new dataset application/x-www-form-urlencoded with compound_uris[] or dataset_uri; multipart form with 'file' parameter with supported mime type",format,"POST","Yes"},
			{"/dataset/{id}","update dataset application/x-www-form-urlencoded with compound_uris[] or dataset_uri; multipart form with 'file' parameter with supported mime type",format,"PUT","Yes"},
			{"/dataset/{id}","remove dataset",format,"DELETE","Yes"},
			{"/dataset/{id}?compound_uris[]=uri-of-a-compound-or-conformer","remove compounds from the dataset",format,"DELETE","Yes"},
			
			{"/filter?dataset_uri={dataset uri}&filter={features uri}&condition={yes/no}","Returns only compounds, which have/have not any values for the specified features",format,"GET","Yes"},
			
			{"http://opentox.org/dev/apis/api-1.2/dataset","A Dataset",formatHeader,null,"Implemented"},
			{DatasetResource.dataset+"/8","get dataset",format,"GET","Yes"},
			{DatasetResource.dataset+"/R100","get dataset",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/metadata","get dataset metadata",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/features","get dataset features",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/compound","get compounds only",format,"GET","Yes"},	
			
			{"[ambit]","Chemical compounds in a dataset",formatHeader,null,"Implemented"},			
			{DatasetResource.dataset+"/8/compound/413","get compound",format,"GET","Yes"},
			//{DatasetResource.dataset+"/{id}/compound","add compound",format,"POST","No"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}","update compound",format,"PUT","No"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}","remove compound from a dataset",format,"DELETE","No"},
			//{DatasetResource.dataset+"/{id}/compound","remove all compounds in a dataset",format,"DELETE","No"},

			{"[ambit]","Conformers in a dataset",formatHeader,null,"Implemented"},
			{DatasetResource.dataset+"/8/compound/413/conformer","get conformers",format,"GET","Yes"},
			{DatasetResource.dataset+"/8/compound/413/conformer/100617","get conformer",format,"GET","Yes"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}","add conformer",format,"POST","No"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}/conformer/{id}","update conformer",format,"PUT","No"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}/conformer/{id}","remove conformer from the dataset",format,"DELETE","No"},
			//{DatasetResource.dataset+"/{id}/compound/{cid}","remove all conformers",format,"DELETE","No"},

			{"http://opentox.org/development/wiki/dataset","Features in a dataset",formatHeader,null,"Implemented"},
			{String.format("%s/8%s",DatasetResource.dataset,PropertyResource.featuredef),"get features available in the dataset",format,"GET","Yes"},

			{"http://opentox.org/dev/apis/api-1.2/Feature","Features",formatHeader,null},
			{String.format("%s?%s=LogP",PropertyResource.featuredef,QueryResource.search_param),"Search for a feature by name",format,"GET","Yes"},
			{String.format("%s?page=0&pagesize=10",PropertyResource.featuredef),"Retrieve all features",format,"GET","Yes"},
			{PropertyResource.featuredef+"/12142","Get description of a specific feature",format,"GET","Yes"},
			{PropertyResource.featuredef+"?search=LogP&condition=regexp","Get description of feature where dc:title regexp 'LogP'",format,"GET","Yes"},
			{String.format(PropertyResource.featuredef+"?sameas=%s",Reference.encode("http://www.opentox.org/api/1.1#CASRN")),
				"Get description of feature which is same as a resource, defined in some ontology'",format,"GET","Yes"},
			{PropertyResource.featuredef,"Create a new feature<ul><li>RDF representation of ot:Feature in the content, RDF mime type<li>feature_uris[]=feature-URL in application/x-www-form-urlencoded</ul>",format,"POST","Yes"},
	
			{"http://opentox.org/dev/apis/api-1.2/structure","Features per compound/conformer",formatHeader,null,"Implemented"},
			{String.format("%s/%d?feature_uris[]=%s",CompoundResource.compound,100,"featureuri"),"features and values ",format,"GET","Yes"},
			

			{"http://opentox.org/dev/apis/api-1.2/Algorithm","Algorithms",formatHeader,null},
			{String.format("%s?page=0&pagesize=10",AllAlgorithmsResource.algorithm),"get a list of all available algorithms",format,"GET","Yes"},
			{String.format("%s/pka",AllAlgorithmsResource.algorithm),"get the representation of an pKa algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecramer",AllAlgorithmsResource.algorithm),"get the representation of an \"ToxTree: Cramer rules\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecramer2",AllAlgorithmsResource.algorithm),"get the representation of an \"ToxTree: Extended Cramer rules\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreeeye",AllAlgorithmsResource.algorithm),"get the representation of an \"ToxTree: Eye irritation\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreeskinirritation",AllAlgorithmsResource.algorithm),"get the representation of an \"ToxTree: Skin irritation\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreemic",AllAlgorithmsResource.algorithm),"get the representation of \"ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreemichaelacceptors",AllAlgorithmsResource.algorithm),"get the representation of \"ToxTree: Michael acceptors\" algorthm",format,"GET","Yes"},
			{String.format("%s/toxtreecarc",AllAlgorithmsResource.algorithm),"get the representation of \"ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity\" algorithm",format,"GET","Yes"},
			{String.format("%s/toxtreekroes",AllAlgorithmsResource.algorithm),"get the representation of \"ToxTree: ILSI/Kroes decision tree for TTC\" algorithm",format,"GET","Yes"},
			{String.format("%s/pka",AllAlgorithmsResource.algorithm),"apply a model to a dataset for prediction",format,"POST","Yes"},
			
			{String.format("%s/leverage",AllAlgorithmsResource.algorithm),"Applicability domain by leverage. Requires dataset_uri as parameter. Creates a model, which can be used for AD estimation of other datasets",format,"POST","Yes"},
						
			{"http://opentox.org/dev/apis/api-1.2/Model","Models",formatHeader,null},
			{String.format("%s?page=0&pagesize=10",ModelResource.resource),"get a list of all available models",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"get the representation of a model",format,"GET","Yes"},
			{String.format("%s/{id}",ModelResource.resource),"delete a model",format,"DELETE","No"},
			{String.format("%s/{id}",ModelResource.resource),"apply a model to a dataset for prediction",format,"POST","Yes"},

			{"http://opentox.org/dev/apis/api-1.2/Model","Model variables",formatHeader,null},
			{String.format("%s/1/independent",ModelResource.resource),"Independent variables",format,"GET","Yes"},
			{String.format("%s/1/dependent",ModelResource.resource),"Dependent variables",format,"GET","Yes"},
			{String.format("%s/1/predicted",ModelResource.resource),"Predicted variables",format,"GET","Yes"},
			
			{"http://opentox.org/dev/apis/api-1.2/AsyncTask","Asynchronous jobs",formatHeader,null},
			{"/task","List of asynchronous jobs and their status",format,"GET","Yes"},
			{"/task/{task id}","Information about a specific task",format,"GET","Yes"},		
			
			{"[ambit]","Features per compound/conformer",formatHeader,null,"Implemented"},
			{String.format("%s/%d%s",CompoundResource.compound,100,PropertyResource.featuredef),"All features, available for a compound",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100,PropertyResource.featuredef),"All features, available for a conformer",format,"GET","Yes"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100304,PropertyResource.featuredef),"All features, available for a conformer",format,"GET","Yes"},
			
			{"[ambit]","Actions on datasets (split, merge, subset)",formatHeader,null,"Implemented"},
			{String.format("%s/{dataset1}?%s=%s",DatasetResource.dataset,DatasetResource.dataset_intersection_uri,
							"datasetURI"),
							"intersection of two datasets (only compounds common to both datasets)",format,"GET","Yes"},
			{String.format("%s/8?%s=%s",DatasetResource.dataset,DatasetResource.dataset_complement_uri,
							"datasetURI"),"Finds compounds which are in the dataset 8, but not in the second dataset",format,"GET","Yes"},
					
			{"[ambit]","Features in a dataset",formatHeader,null,"Implemented"},
			{String.format("%s/8%s/12142",DatasetResource.dataset,PropertyResource.featuredef),"get specific feature",format,"GET","Yes"},
			{String.format("%s/{id}%s",DatasetResource.dataset,PropertyResource.featuredef),"add feature",format,"POST","No"},
			{String.format("%s/{id}%s/{fdid}",DatasetResource.dataset,PropertyResource.featuredef),"update feature ",format,"PUT","No"},
			{String.format("%s/{id}%s/{fdid}",DatasetResource.dataset,PropertyResource.featuredef),"remove feature ",format,"DELETE","No"},

				/*
			{"[ambit]","Feature ontology (This is available at ontology service)",formatHeader,null},
			{String.format("%s/All/Endpoints",OntologyResource.resource),"Hierarchical view of endpoints",format,"GET","Yes"},
			{String.format("%s/All/Descriptors",OntologyResource.resource),"Hierarchical view of descriptors",format,"GET","Yes"},
			{String.format("%s/All/Dataset",OntologyResource.resource),"Hierarchical view of features in datasets",format,"GET","Yes"},
			{String.format("%s/All/Identifiers",OntologyResource.resource),"Hierarchical view of identifiers",format,"GET","Yes"},
			{String.format("%s/{subject}/{object}",OntologyResource.resource),"Delete entry",format,"DELETE","Yes"},
			
			
			{"[ambit]","Feature values",formatHeader,null},
			{String.format("%s/compound/1%s/1",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for a specific feature",format,"GET","Yes"},
			{String.format("%s/compound/1%s",PropertyValueResource.featureKey,PropertyResource.featuredef),"get values for all features ",format,"GET","No"},
			{String.format("%s/dataset/1%s/1",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for all compounds in a dataset for a given feature definition in a dataset",format,"GET","No"}, 
			{String.format("%s%s/{fid}",PropertyValueResource.featureKey,PropertyResource.featuredef),"get the value for a all compounds for a given feature definition",format,"GET","No"},
*/
			{QueryResource.query_resource,"List available search options",format,"GET","Under development"},
			{"/query/pubchem/50-00-0","Queries PubChem for specific term and retrieves structure(s) as SDF file",format,"GET","Yes"},
			{"/query/cir/50-00-0/smiles","Queries Chemical Identifier Resolver /query/cir/{term}/{representation} http://cactus.nci.nih.gov/chemical/structure/documentation",format,"GET","Yes"},
			{"/query/compound/50-00-0/smiles","Queries this database /query/lookup/{term}/{representation}",format,"GET","Yes"},
			{"/query/similarity?search=c1ccccc1&threshold=0.8","Similarity search",format,"GET","Yes"},
			{"/query/qlabel?search=ProbablyERROR","Search compounds by Quality Labels",format,"GET","Yes"},
			{String.format("/query/structure?search=%s&max=100",Reference.encode("CC1=CC1")),"Search by exact structure 	CC1=CC1",format,"GET","Yes"},
			{String.format("/query/smarts?search=%s&max=100",Reference.encode("[NX3][CX3](=[OX1])[#6]")),"Search by SMARTS [NX3][CX3](=[OX1])[#6]",format,"GET","Yes"},
			{String.format("/dataset/2/smarts?search=%s&max=100",Reference.encode("[NX3][CX3](=[OX1])[#6]")),"Search by SMARTS [NX3][CX3](=[OX1])[#6] within /dataset/2",format,"GET","Yes"},
			
			{"[ambit - algorithms]","Structure diagram generation (DEMO)",formatHeader,null},
			{"/depict/cdk?search=c1ccccc1O&smarts=aO","Structure diagram (based on CDK) (with SMARTS highlighting)",format,"GET"},
			
			//{"/depict/daylight?search=c1ccccc1","Structure diagram (based on Daylight depict",format,"GET"},
			//{"/build3d?search=c1ccccc1","Generate 3D structure given a smiles",format,"GET","Under development"},

			{"[ambit - stats]","Statistics",formatHeader,null},
			{"/admin/stats/dataset","Number of datasets",format,"GET"},
			{"/admin/stats/structures","Number of structures",format,"GET"},
			{"/admin/stats/chemicals_in_dataset?dataset_uri=","Number of chemicals in a dataset",format,"GET"},
			{"/admin/stats/dataset_intersection?dataset_uri=..&dataset_uri=..","Number of common chemicals in two datasets",format,"GET"},
			{"/admin/stats/properties","Number of properties",format,"GET"},
			{"/admin/stats/values","Number of values",format,"GET"},
			{"/admin/stats/models","Number of models",format,"GET"},


			{"[ambit - chart]","Charts",formatHeader,null},
			{"/chart/pie?dataset_uri=...&feature_uris[]=...","Pie chart",format,"GET"},
			{"/chart/xy?dataset_uri=...&feature_uris[]=...&feature_uris[]=...","XY Scatter plot",format,"GET"},
			{"/chart/bar?dataset_uri=...&feature_uris[]=...&feature_uris[]=...","Bar chart",format,"GET"},
			
			{"[ambit - bookmarks]","Bookmarks",formatHeader,null},
			{"/bookmark/creator/{id}?search={name}&hasTopic={Model,Dataset,Algorithm,Feature,Compound}","Bookmark",format,"GET"},


			
	};

	public AmbitResource() {
		super();
		setHtmlbyTemplate(false);
	}
	@Override
	public String getTemplateName() {
		return "query.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
        //List<Variant> variants = new ArrayList<Variant>();
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        getVariants().add(new Variant(MediaType.APPLICATION_WADL));
        //getVariants().put(Method.GET, variants);
	
	}
	protected String getSearchString() {
		Form form = getResourceRef(getRequest()).getQueryAsForm();
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
	protected Representation getRepresentation(Variant variant)
			throws ResourceException {

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
				writeHTMLHeader(writer, "AMBIT", getRequest(), getResourceRef(getRequest()), null);
				
				
				writer.write(printWidgetHeader("Welcome"));
				
				writer.write(printWidgetContentHeader(""));
				writer.write(
						String.format("<h5 align='center'>These pages offer minimalistic user interface to <a href='http://ambit.sf.net'>AMBIT</a> implementation of <a href='http://opentox.org'>OpenTox</a> REST services</h5><p> <br>Full featured user interface is available via external applications: <ul><li><a href='http://toxpredict.org' target='_blank'>ToxPredict</a></li><li><a href='http://http://toxcreate.org' target='_blank'>ToxCreate</a></li><li><a href='http://opentox.ntua.gr/blog/78-q-edit'>QPRF editor</a></li><li><a href='http://bioclipse.net/opentox'>Bioclipse</a></li><li><a href='http://qsardb.jrc.it'>QMRF Inventory</a></li><li><a href='%s/ui/query?search=caffeine'>Ambit structure search</a></li></ul></p><p>More applications are under development.</p>",
						getRequest().getRootRef()));
				

				writer.write(String.format("<p><a href='#' onClick=\"javascript:toggleDiv('help');\">REST API details</a></p>"));
				
				writer.write(String.format("<div id='%s' class='help' style='display: %s;''>","help","none"));
				writer.write("<table border='0'>");
				
				writer.write(String.format("<tr align='center'><th colspan='4'>%s%s</th></tr>",
						"Services listed below are an implementation of <a href=\"http://opentox.org/dev/apis/api-1.2\" target=\"blank\">OpenTox REST API 1.2</a>",
						String.format("<h6>All services support MIME types '%s','%s','%s','%s','%s','%s' on GET<br>Services listing chemical structures support Chemical MIME types ('%s','%s','%s','%s') as well</h6>",
						MediaType.APPLICATION_RDF_XML,
						MediaType.APPLICATION_RDF_TURTLE,
						MediaType.TEXT_RDF_N3,
						MediaType.TEXT_RDF_NTRIPLES,
						MediaType.TEXT_HTML,
						MediaType.TEXT_URI_LIST,
						ChemicalMediaType.CHEMICAL_MDLSDF,
						ChemicalMediaType.CHEMICAL_SMILES,
						ChemicalMediaType.CHEMICAL_MDLMOL,
						ChemicalMediaType.CHEMICAL_CML
						)
						));
				
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
				writer.write("</div>");
				writer.write(printWidgetContentFooter());
				writer.write(printWidgetFooter());
				/*
				writer.write(String.format("<a href='%s/ontology' title='BlueObelisk, endpoints, algorithm types ontology'>Ontology</a>&nbsp;",getRequest().getRootRef()));
				writer.write(String.format("<a href='%s/ontology/test' title='Reads RDF output from an URI and displays clickable statements. Enter URI in the search box.'>RDF playground</a>&nbsp;",getRequest().getRootRef()));
				
				writer.write(String.format("<a href='%s' title='This resource URI'>This resource URI</a>&nbsp;",
						getRequest().getResourceRef()));
				writer.write(String.format("<a href='%s' title='Original URI'>Original URI</a>&nbsp;",getRequest().getOriginalRef()));
				writer.write(String.format("<a href='%s' title='Host'>Host</a>&nbsp;",getRequest().getHostRef()));
				writer.write(String.format("<a href='%s' title='Client'>Client address</a>&nbsp;",getRequest().getClientInfo().getAddress()));
			    */
				writeHTMLFooter(writer, "AMBIT", getRequest());
				return new StringRepresentation(writer.toString(),MediaType.TEXT_HTML);				
			}
			
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}
	
	public static void writeHTMLHeader(Writer w,String title,Request request,Reference resourceRef) throws IOException {
		writeHTMLHeader(w, title, request,resourceRef, "");
	}
	public static void writeHTMLHeader(Writer w,String title,Request request,Reference resourceRef,String meta) throws IOException {

		writeTopHeader(w, title, request,resourceRef, meta);
		writeSearchForm(w, title, request,resourceRef, meta);
		
	}


	public static String jsGoogleAnalytics() {
		if (jsGoogleAnalytics==null) try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					AmbitResource.class.getClassLoader().getResourceAsStream("ambit2/rest/config/googleanalytics.js"))
			);
			StringBuilder b = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
            	b.append(line);
            	b.append('\n');
            }
            jsGoogleAnalytics = b.toString();
			reader.close();
			
		} catch (Exception x) { jsGoogleAnalytics = null;}
		return jsGoogleAnalytics;
	}

	public static String js(String url, Reference baseReference) {
		return "";
	
	}
	
	public static void writeTopHeader(Writer w,String title,Request request,Reference resourceRef, String meta) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		
		w.write(String.format("<html %s %s %s %s>",
				"xmlns=\"http://www.w3.org/1999/xhtml\"",
				"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"",
				"xmlns:ot=\"http://opentox.org/api/1.1/\"",
				"itemscope itemtype=\"http://schema.org/Product\"")
				);
		
		w.write(String.format("<head> <meta property=\"dc:creator\" content=\"%s\"/> <meta property=\"dc:title\" content=\"%s\"/>",
				resourceRef,
				title
				)
				);
		
		Reference ref = resourceRef.clone();
		ref.addQueryParameter("media", Reference.encode("application/rdf+xml"));
		w.write(String.format("<link rel=\"meta\" type=\"application/rdf+xml\" title=\"%s\" href=\"%s\"/>",
				title,
				ref
				)); 
		
		w.write(String.format("<link rel=\"primarytopic\" type=\"application/rdf+xml\" href=\"%s\"/>",
				ref
				)); 		
		//<link rel="primarytopic" href="http://opentox.org/api/1_1/opentox.owl#Compound"/>
		
		w.write(String.format("<title>%s</title>",title));
		
		//meta		
		for (String tag : metaTag ) w.write(String.format(tag,baseReference));
		//css			
		for (String style : css ) w.write(String.format(style,baseReference));
		//js
		for (String script : js ) w.write(String.format(script,baseReference));
		w.write(meta);

		w.write("</head>\n");
		w.write("<body>");
		w.write("\n");
		w.write("<div style= \"width: 100%; background-color: #516373;");
		w.write("border: 1px solid #333; padding: 0px; margin: 0px auto;\">");
		w.write("<div class=\"spacer\"></div>");
		
		String top= "";
		
		w.write(String.format("<div class=\"row\"><span class=\"left\">&nbsp;%s",top));
		w.write("</span>");

		w.write(String.format("	<span class=\"right\">%s&nbsp;<a style=\"color:#99CC00\" href='%s/opentoxuser'>%s</a>",
				"",
				baseReference.toString(),
				request.getClientInfo().getUser()==null?"Login":"My account"));
		
		
		//w.write(String.format("&nbsp;<a href=\"%s/help\">Help</a>",baseReference.toString()));
		w.write("</span></div>");
		w.write("	<div class=\"spacer\"></div>");
		w.write("</div>");
		w.write("<div>");		
		
		//w.write(String.format("<a href='%s/ttc?text=50-00-0&search=%s' title='Threshold of toxicological concern prediction'>TTC</a>&nbsp;",baseReference,Reference.encode("C=O")));
		w.write(String.format("&nbsp;<a href='%s/ui'>Home</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/ui/query'>Query compounds</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/compound/100'>Chemical&nbsp;compounds</a>&nbsp;",baseReference));

		w.write(String.format("<a href='%s/dataset?max=25'>Datasets</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/algorithm' title='Predictive algorithms'>Algorithms</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/model?page=0&pagesize=10' title='Models'>Models</a>&nbsp;",baseReference));
		//w.write(String.format("<a href='%s%s'>References</a>&nbsp;",baseReference,ReferenceResource.reference));
		w.write(String.format("<a href='%s%s?page=0&pagesize=10' title='Compound properties'>Features</a>&nbsp;",baseReference,PropertyResource.featuredef));
		w.write(String.format("<a href='%s%s/Taxonomy' title='Features grouped in several categories'>Templates</a>&nbsp;",baseReference,OntologyResource.resource));

		w.write(String.format("<a href='%s/query/similarity?search=c1ccccc1Oc2ccccc2&threshold=0.9' title='Search for similar structures'>Similarity</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/smarts?text=\"\"' title='Substructure search by SMARTS patterns'>Substructure</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s' title='Ontology service, SPARQL endpoint'>Ontology service</a>&nbsp;",getSparql(request)));
		
		w.write(String.format("&nbsp;<a href='http://toxpredict.org' title='Predict'>ToxPredict</a>&nbsp;"));
		w.write(String.format("<a href='%s/depict?search=%s' title='Structure diagram'>Depiction</a>&nbsp;",baseReference,Reference.encode("O=c2c1ccccc1c3ccccc23")));
		w.write(String.format("<a href='%s/depict%s?search=c1ccccc1' title='SMIRKS test'>Reactions</a>&nbsp;",baseReference,ReactionDepict.resource));
		w.write(String.format("<a href='%s/depict%s?search=%s' title='Tautomer test'>Tautomers</a>&nbsp;",baseReference,TautomersDepict.resource,Reference.encode("NC=1N=CN=C2N=CNC2=1")));



		w.write(String.format("&nbsp;<a href='%s/help'>Help</a>&nbsp;",baseReference));


		w.write("</div>");

		w.write("\n<div id=\"targetDiv\"></div>\n");
		w.write("\n<div id=\"statusDiv\"></div>\n");
		//w.write("\n<textarea id=\"targetDiv\"></textarea>\n");
	}
	
	public static String getSparql(Request request) {
		
		return SPARQLPointerResource.getOntologyServiceURI();
	
	}
	public static void writeSearchForm(Writer w,String title,Request request ,Reference resourceRef,String meta) throws IOException {
		writeSearchForm(w, title, request, resourceRef,meta,Method.GET);
	}
	
	protected static Form getParams(Form params,Request request) {
		if (params == null) 
			if (Method.GET.equals(request.getMethod()))
				params = request.getResourceRef().getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = request.getEntityAsForm();
		return params;
	}
	public static void writeSearchForm(Writer w,String title,Request request ,Reference resourceRef,String meta,Method method) throws IOException {
		writeSearchForm(w, title, request, resourceRef, meta,method,null);
	}
	public static void writeSearchForm(Writer w,String title,Request request ,Reference resourceRef,String meta,Method method,Form params) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("<td align='center'>");
		String query_smiles = "";
		try {
			Form form = getParams(params,request);
			if ((form != null) && (form.size()>0))
				query_smiles = form.getFirstValue(QueryResource.search_param);
			else query_smiles = null;
		} catch (Exception x) {
			query_smiles = "";
		}
		w.write(String.format("<form action='' method='%s'>\n",method));
		w.write(String.format("<input name='%s' size='80' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
		w.write("<input type='submit' value='Search'><br>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
	
		w.write("</td>");
		w.write("<td align='left' valign='bottom' width='256px'>");
//		w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write(AmbitResource.disclaimer);
		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		//w.write("<hr>");
		
	}	
	public static void writeHTMLFooter(Writer output,String title,Request request) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		
		output.write("<div class=\"footer\">");

		output.write("<span class=\"right\">");
		output.write(String.format("<a href='http://www.cefic.be'><img src=%s/images/logocefic.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		output.write(String.format("<a href='http://www.cefic-lri.org'><img src=%s/images/logolri.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		output.write(String.format("<a href='http://www.opentox.org'><img src=%s/images/logo.png border='0' width='115' height='60'></a>",baseReference));
		output.write("<br>Developed by Ideaconsult Ltd. (2005-2014)"); 
		output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>&nbsp; ");
		output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>");

		output.write("</span>");		
		output.write("</div>");
		output.write("\n");
		output.write(jsGoogleAnalytics()==null?"":jsGoogleAnalytics());
		output.write("</body>");
		output.write("</html>");

	}	

	
	public static String printWidgetHeader(String header) {
		return printWidgetHeader(header, null);
	}
	public static String printWidgetHeader(String header, String id) {
		return	String.format(
				"<div %s%s class=\"ui-widget \" style=\"margin-top: 20px;\">\n"+
				"<div class=\"ui-widget-header ui-corner-top \">%s</div>\n",id==null?"":"id=",id==null?"":id,header);
	}
	public static String printWidgetFooter() {
		return	String.format("</div>\n");
	}
	public static String printWidgetContentHeader(String style) {
		return	String.format("<div class=\"ui-widget-content ui-corner-bottom %s\">\n",style);
	}
	public static String printWidgetContentFooter() {
		return	String.format("</div>\n");
	}	
	public static String printWidgetContentContent(String content) {
		return
		String.format("<p>%s</p>\n",content);
	}	
	public static String printWidgetContent(String content,String style) {
		return String.format("%s\n%s\n%s",
				printWidgetContentHeader(style),
				printWidgetContentContent(content),
				printWidgetContentFooter());
	}
	
	
	public static String printWidget(String header,String content,String style) {
		return String.format("%s\n%s\n%s",
				printWidgetHeader(header),
				printWidgetContent(content,style),
				printWidgetFooter());

	}
	
	public static String printWidget(String header,String content) {
		return String.format("%s\n%s\n%s",
				printWidgetHeader(header),
				printWidgetContent(content,""),
				printWidgetFooter());

	}		
	public static final String disclaimer = "<a href='#' onClick=\"javascript:toggleDiv('underdev');\">Disclaimer</a><div id='underdev'  style='display: none;'>These pages offer minimalistic user interface to <a href='http://ambit.sourceforge.net/download_ambitrest.html' target=blank>AMBIT</a>  implementation of <a href='http://opentox.org/dev/apis' target=blank>OpenTox</a> <a href='http://ambit.sourceforge.net/rest.html' target=blank>REST</a> services.<br> Full featured user interface is available via external applications, e.g. <a href='http://toxpredict.org' target=blank>ToxPredict</a>, <a href='http://toxcreate.org' target=blank>ToxCreate</a> , <a href='http://opentox.ntua.gr/blog/78-q-edit' target=blank>QPRF editor</a>, <a href='http://www.jcheminf.com/content/4/1/7' target=blank>CheS-Mapper</a>, <a href='http://www.bioclipse.net/opentox' target=blank>Bioclipse-OpenTox</a>.</div>";
	
	/*
	public static final String header_facebook = 
		"<div id='fb-root'></div>\n"+
		"<script>(function(d, s, id) {\n"+
		" var js, fjs = d.getElementsByTagName(s)[0];\n"+
		" if (d.getElementById(id)) return;\n"+
		"  js = d.createElement(s); js.id = id;\n"+
		"  js.src = '//connect.facebook.net/en_US/all.js#xfbml=1';\n"+
		"  fjs.parentNode.insertBefore(js, fjs);\n"+
		"}(document, 'script', 'facebook-jssdk'));</script>";
		
		public static final String header_social = String.format("%s\n%s", header_facebook,header_gplus);
	*/	
	/*
	//
	
	
	public final static String facebook = "<div class='fb-like' data-href='%s' data-send='false' data-layout='button_count' data-width='255' data-show-faces='true' data-font='trebuchet ms'></div>";
	*/
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
		if (getClientInfo().getUser() == null) {
			OpenSSOUser ou = new OpenSSOUser();
			ou.setUseSecureCookie(useSecureCookie(getRequest()));
			getClientInfo().setUser(ou);
		}
        setTokenCookies(variant, useSecureCookie(getRequest()));
        configureTemplateMap(map,getRequest(),(IFreeMarkerApplication)getApplication());
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
}
