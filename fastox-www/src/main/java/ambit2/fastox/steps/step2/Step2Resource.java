package ambit2.fastox.steps.step2;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.step1.Step1Resource;
import ambit2.fastox.steps.step3.Step3Resource;
import ambit2.fastox.wizard.WizardResource;

/**
 * Browse & check structure
 * @author nina
 *
 */
public class Step2Resource extends WizardResource {
	public enum params {
		dataset,
		compound
	};	
	public enum structure_mode {
		d1 {
			public String getName() {
				return "1D";
			}
		},
		d2 {
			public String getName() {
				return "2D";
			}
		},
		d3 {
			public String getName() {
				return "3D";
			}
		};	
		public abstract String getName();
	};		
	public enum structure_browser {
		prev {
			public String getName() {
				return "Previous compound";
			}
			@Override
			public String getShortcut() {
				return "<";
			}
		},
		next {
			public String getName() {
				return "Next compound";
			}
			@Override
			public String getShortcut() {
				return ">";
			}
		};	
		public abstract String getName();
		public abstract String getShortcut();
	};		
	public static final String resource = "/step2";
	public static final String resourceTab = String.format("%s/{%s}",resource,tab);
	public Step2Resource() {
		super("Verify structure",Step1Resource.resource,Step3Resource.resource);
	}
	protected String getTopRef() {
		return resource;
	}
	@Override
	protected String getDefaultTab() {

		return "Verify structure";
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form query = getRequest().getResourceRef().getQueryAsForm();
		int rows = 10;
		writer.write("<table width='100%'>");
		writer.write("<tr>");
		writer.write("<th width='20%' colspan='2'>");
		writer.write("???");
		writer.write("</th>");
		writer.write("<td width='20%' colspan='2'>");
		writer.write("???");
		writer.write("</td>");		
		writer.write(String.format("<td width='40%%' rowspan='%d'>",rows));
		writer.write(String.format("<img src='%s?media=%s&w=400&h=400' width='250' height='250'>",
				query.getFirstValue(params.compound.toString()),
				Reference.encode("image/png")));
		writer.write("</td>");
		writer.write(String.format("<td width='10%%' rowspan='%d'>",rows+1));
		writer.write("</td>");		
		writer.write("</tr>");
		
		for (int i=1;i < rows;i++) {
			writer.write("<tr>");
			writer.write("<th colspan='2'>");
			writer.write(Integer.toString(i));	
			writer.write("</th>");	
			writer.write("<td colspan='2'>");
			writer.write(Integer.toString(i));		
			writer.write("</td>");		
			writer.write("</tr>");
		}
		
		
		writer.write("<tr>");
		writer.write("<td align='right'>");
		writer.write(String.format("<a href='?%s' title='%s'>%s</a>", 
				query.getQueryString(),
				structure_browser.prev.getName(),
				structure_browser.prev.getShortcut()
				));	
		writer.write("</td>");
		writer.write("<td align='right'>");
		writer.write("<input type='text' size='5' name='num' value='1'>");	
		writer.write("</td>");		
		writer.write("<td align='left'>");
		writer.write("<input type='text' size='5' name='all' value='20'>");	
		writer.write("</td>");
		writer.write("<td align='left'>");
		writer.write(String.format("<a href='?%s' title='%s'>%s</a>", 
				query.getQueryString(),
				structure_browser.next.getName(),
				structure_browser.next.getShortcut()
				));	
		writer.write("</td>");		
		writer.write("<td>");
		for (structure_mode mode : structure_mode.values())
			writer.write(String.format("<input type='radio' name='mode' value='%s'>%s",mode.toString(),mode.getName()));
		writer.write("</td>");			
		writer.write("</tr>");
		writer.write("</table>");
		
		writer.write(String.format("<input type='hidden' name='compound' value='%s'>", query.getFirstValue(params.compound.toString())));
		super.renderFormContent(writer, key);
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		String text = form.getFirstValue(Step1Resource.params.text.toString());
		if (text != null) {
			Form query = new Form();
			query.add(Step1Resource.params.search.toString(), text);
			query.add("max","1");
			Representation r = null;
			try {
				ClientResource client = new ClientResource(compound_service+"?"+query.getQueryString());
				r = client.get(MediaType.TEXT_URI_LIST);
				if (r.isAvailable()) {
					query.add(params.compound.toString(),r.getText());
					getRequest().getResourceRef().setQuery(query.getQueryString());
					return get(variant);					
				} else return super.processForm(entity, variant);
				

			}catch (Exception x) {
				
			} finally {
				try {r.release();} catch (Exception x) {}
			}
		}
		
		return super.processForm(entity, variant);
	}
}
