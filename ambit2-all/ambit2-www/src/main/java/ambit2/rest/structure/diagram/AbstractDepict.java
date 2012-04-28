package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.base.processors.AbstractReporter;
import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.AmbitResource;
import ambit2.rest.ProtectedResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * Returns PNG given a smiles
 * @author nina
 *
 */
public class AbstractDepict extends ProtectedResource {
	public static final String resource = "/depict";
	protected Form params;
	protected String smiles ;
	protected String smarts ;
	protected String smirks ;
	protected String recordType = null;
	protected boolean headless = false;
	/**
	 * Might be ignored, currently only CDK depict considers the flags
	 */
	protected Mode2D displayMode = null;


	public String getSmirks() {
		return smirks;
	}
	public void setSmirks(String smirks) {
		this.smirks = smirks;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	protected BufferedImage getImage(String smiles,int width,int height,String recordType) throws ResourceException {
		return null;
	}

	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getResourceRef(getRequest()).getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = getRequest().getEntityAsForm();
		try { headless = Boolean.parseBoolean(params.getFirstValue("headless")); } 
		catch (Exception x) { headless = false;}
		return params;
	}
	protected String getTitle(Reference ref, String smiles) throws ResourceException {

		String style = "depictBox";
		StringBuilder b = new StringBuilder();
		b.append("<table width='100%'>");
		b.append("<tr><td>");
		
		String uri = String.format("%s/daylight",ref.getHierarchicalPart());
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
						uri,Reference.encode(smiles),"Daylight depiction",String.format(AmbitResource.gplus,uri)),
				String.format("<img id='daylight' src='%s?search=%s' alt='%s' title='%s' onError=\"hideDiv('daylight')\">",
						uri,
						Reference.encode(smiles),
						smiles,smiles
						),
				style
					));
		b.append("</td><td>");
		
		uri = String.format("%s/cdk",ref.getHierarchicalPart());
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s?search=%s%s%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
						uri,
						Reference.encode(smiles),
						smarts==null?"":"&smarts=",
						smarts==null?"":Reference.encode(smarts),"CDK depiction",String.format(AmbitResource.gplus,uri)),
				String.format("<img id='cdk' src='%s/any?search=%s&smarts=%s' alt='%s' title='%s' onError=\"hideDiv('cdk')\">",
						uri,
						Reference.encode(smiles),
						smarts==null?"":Reference.encode(smarts),
						smiles,smiles),
				style
					)
				);
		b.append("</td></tr>");
		b.append("<tr><td>");
		uri = String.format("%s/cactvs",ref.getHierarchicalPart());
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
						uri,
						Reference.encode(smiles),
						"Cactvs depiction",String.format(AmbitResource.gplus,uri)),
				String.format("<img id='cactvs' src='%s?search=%s' alt='%s' title='%s' onError=\"hideDiv('cactvs')\">",
						uri,
						Reference.encode(smiles),
						smiles,smiles),
				style
					));
		b.append("</td><td>");
		uri = String.format("%s/obabel",ref.getHierarchicalPart());
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
						uri,
						Reference.encode(smiles),
						"Open Babel depiction",String.format(AmbitResource.gplus,uri)),
				String.format("<img id='obabel' src='%s/obabel?search=%s' alt='%s' title='%s' onError=\"hideDiv('obabel')\">",
						ref.getHierarchicalPart(),
						Reference.encode(smiles),
						smiles,smiles),
				style
					));
		b.append("</td></tr>");
		
		b.append("<tr><td>");

		String recordTypeOption = recordType==null?"":String.format("&record_type=%s", recordType);
		uri = String.format("%s/pubchem",ref.getHierarchicalPart());
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s?search=%s%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
						uri,
						Reference.encode(smiles),
						recordTypeOption,
						"PubChem depiction",String.format(AmbitResource.gplus,uri)),
				String.format("<img id='pubchem' src='%s?search=%s%s' alt='%s' title='%s' onError=\"hideDiv('pubchem')\">",
						uri,
						Reference.encode(smiles),
						recordTypeOption,
						smiles,smiles),
				style
					));
		b.append("</td><td>");

		b.append("</td></tr>");		
		
		b.append("</table>");
		return b.toString();
		
	}
	
	@Override
	public Representation get(Variant variant) {
		return process(variant);
	}

	public Representation process(Variant variant) {

		try {
			Form form = getParams();
			int w = 400; int h = 200;
			recordType = "2d";
			try { w = Integer.parseInt(form.getFirstValue("w"));} catch (Exception x) {w =400;}
			try { h = Integer.parseInt(form.getFirstValue("h"));} catch (Exception x) {h =200;}
			try { recordType = form.getFirstValue("record_type");} catch (Exception x) {}

			smiles = form.getFirstValue(QueryResource.search_param);
			setSmarts(form.getFirstValue("smarts"));
			setSmirks(null);
			String[]  smirks_patterns = form.getValuesArray("smirks");
			for (String sm: smirks_patterns)
				if (sm!=null) {
					setSmirks(sm);
					break;
				}
        	
	    		if(variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    			StringConvertor convertor = new StringConvertor(new AbstractReporter<String,Writer>() {
	    				public void close() throws Exception {};
	    				public Writer process(String target) throws AmbitException {
	    					try {
	    					if (headless) output.write(target);
	    					else {
	    						
		    					AmbitResource.writeTopHeader(output, smiles, getRequest(),getResourceRef(getRequest()), AmbitResource.header_gplus,null);
		    					writeSearchForm(output, smiles, getRequest(), "",Method.GET,params);	    					
		    					output.write(target);
		    					AmbitResource.writeHTMLFooter(output, smiles, getRequest());
	    					}
	    					} catch (Exception x) {}
	    					return output;
	    				};
	    			},MediaType.TEXT_HTML);
	    			return convertor.process(getTitle(getResourceRef(getRequest()),smiles));
	    		}
					    		
	    	if (smiles != null) {
	        	final BufferedImage image = getImage(smiles.trim(),w,h,recordType);
	        	if (image ==  null) {
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid smiles %s",smiles));
	        		return null;
	        	}
	        	return new OutputRepresentation(MediaType.IMAGE_PNG) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			try {
	        				ImageIO.write(image, "PNG", out);
	        			} catch (IOException x) {
	        				throw x;
	        			} catch (Exception x) {
	        				
	        			} finally {
		        			try { out.flush(); } catch (Exception x) {}
		        			try { out.close(); } catch (Exception x) {}
	        			}
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, new EmptyMoleculeException());
	        	return null;   	
	        }
		} catch (ResourceException x) {
			getResponse().setStatus(x.getStatus(),x,x.getMessage());
			return null;
		} catch (Exception x) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,x,x.getMessage());
			return null;
		
		}
	}	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			if (params==null) params = new Form(entity);
			return get(variant);
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("%s not supported",entity==null?"":entity.getMediaType()));
	}
	
	public void writeSearchForm(Writer w,String title,Request request ,String meta,Method method,Form params) throws IOException {
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
			if ((form != null) && (form.size()>0)) {
				query_smiles = form.getFirstValue(QueryResource.search_param);
				if (query_smiles!=null) query_smiles = query_smiles.trim();
			} else query_smiles = null;
		} catch (Exception x) {
			query_smiles = "";
		}
		
		
		w.write(String.format("<form action='' method='%s'>\n",method));
		w.write("<table width='100%'>");
		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label>",QueryResource.search_param,"SMILES or InChI"));
		w.write(String.format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");'>",
				request.getRootRef(),request.getRootRef()));
		w.write("</th>");		
		w.write("<td>");
		w.write(String.format("<input name='%s' size='70' value='%s'>\n",
				QueryResource.search_param,query_smiles==null?"":query_smiles.trim()));

		w.write("</td>");
		w.write("<td><input type='submit' value='Display'></td>");
		w.write("</tr>\n");

		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>","smarts","SMARTS (optional)"));
		w.write("<td>");
			w.write(String.format("<input name='%s' size='70' value='%s' title='Highlights the substructure, specified by SMARTS'>",
					"smarts",getSmarts()==null?"":getSmarts()));
		w.write("</td>");			
		w.write("<td>&nbsp;</td></tr>\n");
		w.write("</table>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
		
		w.write("</td>");
		w.write("<td align='left' valign='bottom' width='256px'>");
		w.write(AmbitResource.disclaimer);
		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
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
	
	public String getSmarts() {
		return smarts;
	}

	public void setSmarts(String smarts) {
		this.smarts = smarts;
		
	}
}