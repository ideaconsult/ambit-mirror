package ambit2.fastox.models;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.OpenTox;
import ambit2.rest.task.dsl.OTAlgorithm;
import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTDatasetReport;
import ambit2.rest.task.dsl.OTDatasetScrollableReport;
import ambit2.rest.task.dsl.OTDatasetTableReport;
import ambit2.rest.task.dsl.OTFeature;
import ambit2.rest.task.dsl.OTFeatures;
import ambit2.rest.task.dsl.OTModel;
import ambit2.rest.task.dsl.OTValidation;

public class ReportingResource  extends FastoxStepResource {
	public static final String resource = "/report";
	public static final String resourceType = "type";
	protected String[] search;
	protected OTFeatures features;
	protected int page = 0;
	protected int pageSize = 10;
	protected display_mode mode = display_mode.scrollable;
	protected boolean header = true;
	protected enum display_mode {
		table,
		scrollable
	}
	
	protected enum report_type {
		Validation {
			@Override
			public String report(String q,String ontology) throws Exception {
				return OTValidation.validation(q).report(ontology);
			}
		},
		Model {
			@Override
			public String report(String q,String ontology) throws Exception  {
				return OTModel.model(q).report(ontology);
			}
		},
		Algorithm {
			@Override
			public String report(String q,String ontology) throws Exception  {

				return OTAlgorithm.algorithm(q).report(ontology);
			}
		},
		Dataset {
			@Override
			public String report(String q,String ontology) throws Exception  {

				return "";
			}
		},		
		Feature {
			@Override
			public String report(String q,String ontology) throws Exception  {
				return OTModel.model(q).report(ontology);
			}
		};
		public abstract String report(String q,String ontology) throws Exception ;

		public String getTitle(String url) {
			return String.format("%s&nbsp;<a href='%s'>%s</a>",toString(),url,url);
		}
	}
	protected report_type type = report_type.Validation;

	
	
	public ReportingResource() {
		super(0);
		helpResource = null;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		search = form.getValuesArray("search");
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
			pageSize = 10;
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
			String[] ff = form.getValuesArray(OpenTox.params.feature_uris.toString());
			for (String f:ff) 
				if ((f!=null) && (!"".equals(f))) {
					if (features==null) features = OTFeatures.features();
					features.add(OTFeature.feature(f.trim()));
				}
			
		} catch (Exception x) {
			features = null;
		}					
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}

	 public String js() {
		 return String.format(
		 "<script type=\"text/javascript\">\n"+
		 "function contentDisp(url,page,data)\n"+
		 "{\n"+
		 "$.ajax({\n"+
		 "      data: data, \n"+
		 "      beforeSend: function(xhr){\n"+
		 "        xhr.setRequestHeader(\"Accept\",\"text/csv\");\n"+
		 "      },\n"+
		 "url : url,\n"+
		 "success : function (data) {\n"+
		 "$(\"#BROWSER\").html(data);\n"+
		 "$(\"#page\").text(page);\n"+
	 	 "}\n"+
		 "});\n"+
		 "}\n"+
		 "</script>\n");
	 }
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			if (report_type.Dataset.equals(type)) {
				if (header) {
					writer.write(
							String.format(
					"<html><head>\n<script src=\"%s/jquery/jquery-1.4.2.min.js\"></script>\n</head>"+
					"\n<body>\n<link rel=\"stylesheet\" href=\"%s/style/scrollable.css\" type=\"text/css\" media=\"screen\">",
					getRequest().getRootRef(),
					getRequest().getRootRef()
					));
					writer.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">",getRequest().getRootRef()));
					writer.write(js());
					
				}
				for (String q : search) {
					OTDatasetReport report;
					switch (mode) {
					case scrollable: {
						report = OTDatasetScrollableReport.
						report(OTDataset.dataset(q),features,wizard.getService(SERVICE.application).toString(),page,pageSize).
						setRequestref(getRequest().getResourceRef());
						break;
					}
					case table : {
						report = OTDatasetTableReport.
						report(OTDataset.dataset(q),features,wizard.getService(SERVICE.application).toString(),page,pageSize).
						setRequestref(getRequest().getResourceRef());
						break;
					}
					default: {
						report = OTDatasetScrollableReport.
						report(OTDataset.dataset(q),features,wizard.getService(SERVICE.application).toString(),page,pageSize).
						setRequestref(getRequest().getResourceRef());
					}
				    }
					
					report.write(writer);
				}	
				if (header) {
					writer.write("</body></html>");
				}
				
			} else 
				for (String q : search) {
					String s = type.report(q, String.format("%s?results=yes&title=%s",
							wizard.getService(SERVICE.ontology).toString(),
							Reference.encode(type.getTitle(q))));
					writer.write(s);
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
}
