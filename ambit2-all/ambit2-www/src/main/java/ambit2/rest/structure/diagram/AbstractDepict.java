package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.base.processors.AbstractReporter;
import ambit2.rest.AmbitResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * Returns PNG given a smiles
 * @author nina
 *
 */
public class AbstractDepict extends ServerResource {
	protected Form params;
	protected String smiles ;
	protected String smarts ;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	protected BufferedImage getImage(String smiles,int width,int height) throws ResourceException {
		return null;
	}
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getRequest().getResourceRef().getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = getRequest().getEntityAsForm();
		return params;
	}
	protected String getTitle(Reference ref, String smiles) {
		StringBuilder b = new StringBuilder();
		b.append(String.format("SMILES %s<br>",	smiles==null?"":smiles));
		b.append("<table width='100%'><tr>");
		b.append(String.format("<td><a href='%s/daylight?search=%s'>%s</a></td><td><a href='%s/cdk?search=%s'>%s</a></td>",
				ref.getHierarchicalPart(),
				Reference.encode(smiles),
				"Daylight depiction",ref.getHierarchicalPart(),smiles,"CDK depiction"));		
		b.append("</tr><tr>");
		
		b.append(String.format("<td><img src='%s/daylight?search=%s' alt='%s' title='%s'></td><td><img src='%s/cdk?search=%s' alt='%s' title='%s'></td>",
				ref.getHierarchicalPart(),
				Reference.encode(smiles),
				smiles,smiles,ref.getHierarchicalPart(),smiles,smiles,smiles));
		
		b.append("</tr><tr>");
		b.append(String.format("<td><a href='%s/cactvs?search=%s'>%s</a></td><td></td>",
				ref.getHierarchicalPart(),
				Reference.encode(smiles),
				"Cactvs depiction"));		
				
		b.append("</tr><tr>");
		
		b.append(String.format("<td><img src='%s/cactvs?search=%s' alt='%s' title='%s'></td><td></td>",
				ref.getHierarchicalPart(),smiles,smiles,smiles));
		
		b.append("</tr></table>");
		return b.toString();
	}
	
	
	public Representation get(Variant variant) {

		try {
			Form form = getParams();
			int w = 400; int h = 200;
			try { w = Integer.parseInt(form.getFirstValue("w"));} catch (Exception x) {w =400;}
			try { h = Integer.parseInt(form.getFirstValue("h"));} catch (Exception x) {h =200;}
			smiles = form.getFirstValue(QueryResource.search_param);
			smarts = form.getFirstValue("smarts");	
        	
	    		if(variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    			StringConvertor convertor = new StringConvertor(new AbstractReporter<String,Writer>() {
	    				public void close() throws Exception {};
	    				public Writer process(String target) throws AmbitException {
	    					try {
	    					AmbitResource.writeTopHeader(output, smiles, getRequest(), "");
	    					AmbitResource.writeSearchForm(output, smiles, getRequest(), "",Method.GET,params);	    					
	    					output.write(target);
	    					AmbitResource.writeHTMLFooter(output, smiles, getRequest());
	    					} catch (Exception x) {}
	    					return output;
	    				};
	    			},MediaType.TEXT_HTML);
	    			return convertor.process(getTitle(getRequest().getOriginalRef(),smiles));
	    		}
					    		
	    	if (smiles != null) {
	        	final BufferedImage image = getImage(smiles,w,h);
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
}