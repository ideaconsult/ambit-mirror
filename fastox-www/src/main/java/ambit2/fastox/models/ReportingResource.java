package ambit2.fastox.models;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.opentox.dsl.OTAlgorithm;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTModel;
import org.opentox.dsl.OTModels;
import org.opentox.dsl.OTValidation;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.task.dsl.OTDatasetRDFReport;
import ambit2.rest.task.dsl.OTDatasetReport;
import ambit2.rest.task.dsl.OTDatasetScrollableReport;
import ambit2.rest.task.dsl.OTDatasetTableReport;

import com.hp.hpl.jena.ontology.OntModel;

public class ReportingResource  extends FastoxStepResource {
	protected static OTFeatures endpoints;
	protected static String[] endpoints_names = {
		"http://www.opentox.org/echaEndpoints.owl#AcuteInhalationToxicity",
		"http://www.opentox.org/echaEndpoints.owl#Acute_toxicity_to_fish_lethality",
		"http://www.opentox.org/echaEndpoints.owl#BCF_fish",
		"http://www.opentox.org/echaEndpoints.owl#Carcinogenicity",
		"http://www.opentox.org/echaEndpoints.owl#Dissociation_constant_pKa",
		"http://www.opentox.org/echaEndpoints.owl#Endpoints",
		"http://www.opentox.org/echaEndpoints.owl#Eye_irritation_corrosion",
		"http://www.opentox.org/echaEndpoints.owl#Gastrointestinal_absorption",
		"http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",
		"http://www.opentox.org/echaEndpoints.owl#In_vivo_pre-_peri-_post_natal_development_and_or_fertility_1_or_2_gen._Study_or_enhance",
		"http://www.opentox.org/echaEndpoints.owl#MolecularWeight",
		"http://www.opentox.org/echaEndpoints.owl#Mutagenicity",
		"http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow",
		"http://www.opentox.org/echaEndpoints.owl#PersistenceBiodegradation",
		"http://www.opentox.org/echaEndpoints.owl#Receptor-binding_specify_receptor",
		"http://www.opentox.org/echaEndpoints.owl#Receptor_binding_and_gene_expression",
		"http://www.opentox.org/echaEndpoints.owl#SkinIrritationCorrosion",
		"http://www.opentox.org/echaEndpoints.owl#SkinSensitisation",
		"http://www.opentox.org/echaEndpoints.owl#ToxicoKinetics"
		
	};
	public static final String resource = "/report";
	public static final String resourceType = "type";
	protected String[] search;
	protected OTFeatures features;
	protected int page = 0;
	protected int pageSize = 10;
	protected display_mode mode = display_mode.scrollable;
	protected boolean header = true;
	protected Form params;
	protected OTModels models;
	protected boolean showEndpoints = true;
	protected boolean showModels = true;
	

	protected enum display_mode {
		table,
		scrollable
	}
	
	protected enum report_type {
		Validation {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception {
				return OTValidation.validation(q).report(ontology);
			}
		},
		Model {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception  {
				return OTModel.model(q).report(ontology);
			}
		},
		Algorithm {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception  {

				return OTAlgorithm.algorithm(q).report(ontology);
			}
		},
		Dataset {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception  {

				return "";
			}
		},		
		/*
		Stats {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception  {

				OTDatasetComparisonReport report = OTDatasetComparisonReport.report(new Reference(application),OTDataset.dataset(q));
				report.setDatasetReportRef(baseRef);
				return report.report(ontology);
			}
		},		
		*/	
		Feature {
			@Override
			public String report(Reference baseRef,String q,String ontology,String application) throws Exception  {
				return OTModel.model(q).report(ontology);
			}
		};
		public abstract String report(Reference baseRef,String q,String ontology,String application) throws Exception ;

		public String getTitle(String url) {
			return String.format("%s&nbsp;<a href='%s'>%s</a>",toString(),url,url);
		}
	}
	protected report_type type = report_type.Validation;

	
	
	public ReportingResource() {
		super(0);
		helpResource = null;
	}
	protected Form getParams() {
		if (params!=null) return params;
		if (Method.GET.equals(getRequest().getMethod())) {
			params = getRequest().getResourceRef().getQueryAsForm();	
		} else {
			params = getRequest().getEntityAsForm();
		}
		return params;
		
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));
		getVariants().add(new Variant(MediaType.TEXT_CSV));
		getVariants().add(new Variant(MediaType.APPLICATION_PDF));
		Form form = getParams();
		search = form.getValuesArray("search");
		if (endpoints==null) try {
			endpoints = OTFeatures.features();
			for (String name:endpoints_names) {
			try {				
				Reference featureRef = new Reference(wizard.getService(SERVICE.feature));
				featureRef.addQueryParameter(OpenTox.params.sameas.toString(),name);
				endpoints.read(featureRef.toString());
			} catch (Exception x) {
				System.out.println(name);
				x.printStackTrace();
			}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}	

		try {
			type = report_type.valueOf(getRequest().getAttributes().get(resourceType).toString());
		} catch (Exception x) {
			type = report_type.Validation;
		}
		try {
			page = Integer.parseInt(form.getFirstValue(OpenTox.params.page.toString()));
		} catch (Exception x) {
			page = 0;
		}	
		try {
			pageSize = Integer.parseInt(form.getFirstValue(OpenTox.params.pagesize.toString()));
		} catch (Exception x) {
			try { pageSize = Integer.parseInt(getSession(getUserKey()).getPageSize()); } 
			catch (Exception xx) { pageSize =1; }
		}	
		try {
			mode = display_mode.valueOf(form.getFirstValue("mode"));
		} catch (Exception x) {
			mode = display_mode.scrollable;
		}	
		try {
			header = Boolean.parseBoolean(form.getFirstValue("header"));
		} catch (Exception x) {
			header = false;
		}
		try {
			showEndpoints = Boolean.parseBoolean(form.getFirstValue("endpoints"));
		} catch (Exception x) {
			showEndpoints = true;
		}	
		try {
			showModels = Boolean.parseBoolean(form.getFirstValue("models"));
		} catch (Exception x) {
			showModels = true;
		}			
		try {
			String[] ff = form.getValuesArray(OpenTox.params.feature_uris.toString());
			if ((ff!=null) && (ff.length>0))
			for (String f:ff) 
				if ((f!=null) && (!"".equals(f))) {
					if (features==null) features = OTFeatures.features();
					features.add(OTFeature.feature(f.trim()));
				}
			
		} catch (Exception x) {
			features =null;
		}
		/*
		if ((features == null) || (features.size()==0))
		try {
			
			OTModels models = getSession(getUserKey()).getSelectedModels();
		
			models = OTModels.models();
			models.
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/2")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/8")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/9")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/17")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/16")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/13")).
				add(OTModel.model("http://apps.ideaconsult.net:8080/ambit2/model/33"));
				//getSession(getUserKey()).getSelectedModels()		
			models.load(new OTProperty[] {OTProperty.predictedVariables});
			
			features = models.predictedVariables();
			
			

		} catch (Exception xx) {
			features = null;
		}
		*/
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}

	 public static String js() {
		 return String.format(
		 "<script type=\"text/javascript\">\n"+
		 "function contentDisp(url,page,data)\n"+
		 "{\n"+
		 "$.ajax({\n"+
		 //"      type: \"POST\", \n"+
		 "      data: data, \n"+
		 "      beforeSend: function(xhr){\n"+
		 "        xhr.setRequestHeader(\"Accept\",\"text/html\");\n"+
		 "      },\n"+
		 "url : url,\n"+
		 "success : function (data) {\n"+
		 "    $(\"#BROWSER\").html(data);\n"+
		 "    $(\"#page\").text(page);\n"+
	 	 "}\n"+
		 "});\n"+
		 "}\n"+
		 "</script>\n");
	 }
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			Reference datasetReport =
				new Reference(
						String.format("%s/user/%s/report/Dataset",
								getRequest().getRootRef(),session.getUser().getId()));
			if (report_type.Dataset.equals(type)) {
				if (header) {
					writer.write(
							String.format(
					"<html><head>\n<script src=\"%s/jquery/jquery-1.4.2.min.js\"></script>\n"+
					"<meta name=\"robots\" content=\"index,nofollow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,NOFOLLOW\">"+
					"</head>"+
					"\n<body>\n<link rel=\"stylesheet\" href=\"%s/style/scrollable.css\" type=\"text/css\" media=\"screen\">",
					getRequest().getRootRef(),
					getRequest().getRootRef()
					));
					writer.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.tablesorter.min.js\"></script>\n",getRequest().getRootRef()));
					writer.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">\n",getRequest().getRootRef()));
					writer.write(String.format("<script src=\"%s/jquery/jquery.blockUI.js\"></script>\n",getRequest().getRootRef()));
					writer.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/stats.js\"></script>\n",getRequest().getRootRef()));
					writer.write(ReportingResource.js());
					
				} else {
					writer.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">\n",getRequest().getRootRef()));
				}
				OntModel store_models;
				try {
					store_models = ModelTools.retrieveModels(null,session, MediaType.APPLICATION_RDF_XML);
				} catch (Exception x) {
					store_models = null;
					session.setError(key,x);
				}	
			
				for (String q : search) {
					
					OTDatasetReport report;
					switch (mode) {
					case scrollable: {
						OTModels models = getSession(getUserKey()).getSelectedModels();
						OTDatasetRDFReport rep = OTDatasetRDFReport.
						report(OTDataset.dataset(q),
									showEndpoints,
									endpoints,
									showModels,
									models==null?null:models.predictedVariables(),
									wizard.getService(SERVICE.application).toString(),page,pageSize);
						rep.setRequestref(getRequest().getResourceRef());

						rep.setJenaModel(store_models);
						//((ToxPredictDatasetReport)report).setModels(models);
						rep.write(writer);
						break;
					}
					case table : {
						report = OTDatasetTableReport.
						report(OTDataset.dataset(q),features,wizard.getService(SERVICE.application).toString(),page,pageSize).
						setRequestref(getRequest().getResourceRef());
						report.write(writer);
						break;
					}
					default: {
						report = OTDatasetScrollableReport.
						report(OTDataset.dataset(q),features,wizard.getService(SERVICE.application).toString(),page,pageSize).
						setRequestref(getRequest().getResourceRef());
						report.write(writer);
					}
				    }
					
					
				}	
				if (header) {
					writer.write("</body></html>");
				}
				
			} else 
				if ((search==null) || (search.length==0)) {
					if (header) {
						writer.write(
								String.format(
						"<html><head>\n<script src=\"%s/jquery/jquery-1.4.2.min.js\"></script>\n</head>"+
						"\n<body>\n<link rel=\"stylesheet\" href=\"%s/style/scrollable.css\" type=\"text/css\" media=\"screen\">",
						getRequest().getRootRef(),
						getRequest().getRootRef()
						));
						writer.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.tablesorter.min.js\"></script>\n",getRequest().getRootRef()));
						writer.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">\n",getRequest().getRootRef()));
						writer.write(String.format("<script src=\"%s/jquery/jquery.blockUI.js\"></script>\n",getRequest().getRootRef()));
						writer.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/stats.js\"></script>\n",getRequest().getRootRef()));
						writer.write(ReportingResource.js());
						
					} else {
						writer.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">\n",getRequest().getRootRef()));
					}					
					String s = type.report(datasetReport,null, 
							String.format("%s?results=yes&title=%s",
							wizard.getService(SERVICE.ontology).toString(),
							Reference.encode(type.getTitle(""))),
							wizard.getService(SERVICE.application).toString());
					writer.write(s);
					writer.write("<div id='BROWSER'></div>");
					if (header) {
						
						writer.write("</body></html>");
					}					
				} else {

		
					for (String q : search) {
						String s = type.report(
								datasetReport,
								q, String.format("%s?results=yes&title=%s",
								wizard.getService(SERVICE.ontology).toString(),
								Reference.encode(type.getTitle(q))),
								wizard.getService(SERVICE.application).toString());
						writer.write(s);
					}
				}

		} catch (Exception x) {
			writer.write(x.getMessage());
		}
	}
	@Override
	public void renderFormHeader(Writer writer, String key) throws IOException {
	}
	@Override
	public void renderFormFooter(Writer writer, String key) throws IOException {

	}
	@Override
	public void renderErrorsTab(Writer writer, String key) throws IOException {
	}
	@Override
	public void renderTabs(Writer writer) throws IOException {
	}	
	@Override
	public void header(Writer w, String meta) throws IOException {
	
	}
	@Override
	public void footer(Writer w) throws IOException {
		
	}
	@Override
	public void navigator(Writer writer) throws IOException {
	}
	@Override
	public void top(Writer writer) throws IOException {
	};
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {

		if (params==null) params = new Form(entity);
		return super.get(variant);
	}
	@Override
	public void help(Writer writer) throws IOException {
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (MediaType.TEXT_HTML.equals(variant.getMediaType())) 
			return super.get(variant);
		final MediaType mediaType = variant.getMediaType();
		return new OutputRepresentation(mediaType) {
			@Override
			public void write(OutputStream out) throws IOException {
				try {
					OTModels models = getSession(getUserKey()).getSelectedModels();
					for (String q : search) {
						OTDatasetRDFReport rep = OTDatasetRDFReport.
						report(OTDataset.dataset(q),
									showEndpoints,
									endpoints,
									showModels,
									models==null?null:models.predictedVariables(),
									wizard.getService(SERVICE.application).toString(),page,pageSize);
						rep.setRequestref(getRequest().getResourceRef());
	
						//rep.setJenaModel(store_models);
						//((ToxPredictDatasetReport)report).setModels(models);
						rep.download(out,mediaType);
					}
						
					
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					if ((x.getMessage()!=null) && x.getMessage().equals("Not Found")) {
						ResourceException xx = new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
								"We did not find any matching entries for the search you performed in the OpenTox database. Please go back to Step 1 of your ToxPredict workflow and try again.");
						session.setError(step.getTitle(),xx);
						throw xx;
						
					} else throw new ResourceException(x);
				} finally {

					out.close();
				}
			}
		};
	}	
}
