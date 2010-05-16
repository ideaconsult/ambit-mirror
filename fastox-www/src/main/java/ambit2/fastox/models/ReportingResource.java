package ambit2.fastox.models;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.task.dsl.OTAlgorithm;
import ambit2.rest.task.dsl.OTModel;
import ambit2.rest.task.dsl.OTValidation;

public class ReportingResource  extends FastoxStepResource {
	public static final String resource = "/report";
	public static final String resourceType = "type";
	protected String[] search;
	
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
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			for (String q : search) {
				String s = type.report(q, String.format("%s?results=yes&title=%s",
						wizard.getService(SERVICE.ontology).toString(),
						Reference.encode(type.getTitle(q))));
				writer.write(s);
				/*
				String s = OTValidation.validation(q).report(
					);
						writer.write(s);
						*/
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
