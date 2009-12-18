package ambit2.rest.fastox;

import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.AmbitResource;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.structure.CompoundHTMLReporter;

public class FastToxStep1 extends SmartsQueryResource {
	public static String resource = "/fasttox";

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
				new CompoundHTMLReporter(
						getRequest(),
						false) {
					
					
					public void header(Writer output, QueryStructureByID query) {
						try {
							AmbitResource.writeHTMLHeader(output,
									collapsed?"Chemical compounds":"Chemical compound"
									,
									getRequest()
									);
							
						} catch (Exception x) {}		
					};						
					@Override
							public String toURI(IStructureRecord item) {
								StringBuilder b = new StringBuilder();
								b.append(String.format("<form action=\"%s%s\" method=\"POST\">",getUriReporter().getBaseReference(),getFormAction()));
								b.append(super.toURI(item));
								
								b.append(String.format("<input name=\"idstructure\" value=\"%d\" type=\"hidden\">",item.getIdstructure()));
								b.append(String.format("<input name=\"idchemical\" value=\"%d\" type=\"hidden\">",item.getIdchemical()));
								b.append("<input type=\"submit\" value=\"Select models for prediction\"></form>");
								b.append("</form>");
								
								return b.toString();
							}
	
				},
				MediaType.TEXT_HTML);
	}
	protected String getFormAction() {
		return String.format("%s/step2",resource);
	}
}

