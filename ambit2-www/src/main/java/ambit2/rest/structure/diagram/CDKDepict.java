package ambit2.rest.structure.diagram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
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
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.AbstractReporter;
import ambit2.rendering.CompoundImageTools;
import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.AmbitResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SmartsPatternAmbit;



/**
 * 2D depiction based on CDK
 * @author nina
 *
 */
public class CDKDepict extends AbstractDepict implements ISmartsDepiction {
	protected CompoundImageTools depict = new CompoundImageTools();
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		depict.setImageSize(new Dimension(410,210));
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws ResourceException {
		try {
			depict.setImageSize(new Dimension(w,h));
			
			if (displayMode==null) {
				return depict.generateImage(smiles,
						(smarts == null)||("".equals(smarts.trim()))?null:new SmartsPatternSelector(smarts),false,false,null);
			} else 
				return depict.generateImage(smiles,
							(smarts == null)||("".equals(smarts.trim()))?null:new SmartsPatternSelector(smarts),false,false,displayMode);

		} catch (ResourceException x) {throw x; 
		} catch (Exception x) { 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x); 
		}
	}
	@Override
	public Representation get(Variant variant) {
		try {
		if(variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			Form form = getParams();
			int w = 400; int h = 200;
			try { w = Integer.parseInt(form.getFirstValue("w"));} catch (Exception x) {w =400;}
			try { h = Integer.parseInt(form.getFirstValue("h"));} catch (Exception x) {h =200;}
				try {
					displayMode = Mode2D.valueOf(form.getFirstValue("mode"));
				} catch (Exception x) {displayMode = null;}
			smiles = form.getFirstValue(QueryResource.search_param);
			setSmarts(form.getFirstValue("smarts"));
			StringConvertor convertor = new StringConvertor(new AbstractReporter<String,Writer>() {
				public void close() throws Exception {};
				public Writer process(String target) throws AmbitException {
					try {
					AmbitResource.writeTopHeader(output, smiles==null?"2D structure diagram":smiles, getRequest(),getResourceRef(getRequest()), "",null);
					writeSearchForm(output, smiles, getRequest(), "",Method.GET,params);	    					
					output.write(target);
					AmbitResource.writeHTMLFooter(output, smiles, getRequest());
					} catch (Exception x) {}
					return output;
				};
			},MediaType.TEXT_HTML);
			return convertor.process(getTitle(getResourceRef(getRequest()),smiles));
		} else {
			Representation image = super.get(variant);
			if (image !=null) return image;
			if (variant.getMediaType().equals(MediaType.IMAGE_PNG))  {
				
	        	return new OutputRepresentation(MediaType.IMAGE_PNG) {
	    		@Override
	    		public void write(OutputStream out) throws IOException {
	    			try {
	    				ImageIO.write( (BufferedImage)depict.getDefaultImage(), "PNG", out);
	    			} catch (IOException x) {
	    				throw x;
	    			} catch (Exception x) {
	    				
	    			} finally {
	        			try { out.flush(); } catch (Exception x) {}
	        			try { out.close(); } catch (Exception x) {}
	    			}
	    		}
	        	};
			};
	
		}
		} catch (Exception x) {
			
		}
    	return null;

	}
	@Override
	protected String getTitle(Reference ref, String smiles) {
		StringBuilder b = new StringBuilder();
		b.append("<table width='100%'>");
		for (Mode2D mode : Mode2D.values()) {
			if ((mode.ordinal() %2) == 0) b.append("<tr>");
			b.append("<td>");
			b.append(AmbitResource.printWidget(
					String.format("<a href='%s&mode=%s&media=image/png' target='%s' title='%s'>%s</a>",
							ref,mode.name(),mode.name(),mode.getDescription(),mode.toString()), 
					String.format("<img src='%s&mode=%s' alt='%s' title='%s'>", ref,mode.getURIParameter(),smiles==null?"":smiles,smiles==null?"":smiles)));
			b.append("</td>");
			if ((mode.ordinal() %2) == 1) b.append("</tr>");
		}
		b.append("</table>");
		/*
		b.append("<table><tr><td>");
		b.append(AmbitResource.printWidget("As it is", String.format("<img src='%s' alt='%s' title='%s'>", ref,smiles==null?"":smiles,smiles==null?"":smiles)));
		b.append("</td><td>");
		b.append(AmbitResource.printWidget("Aromatic", String.format("<img src='%s&mode=aromatic' alt='%s' title='%s'>", ref,smiles==null?"":smiles,smiles==null?"":smiles)));
		b.append("</td></tr><tr><td>");
		b.append(AmbitResource.printWidget("Kekule (CDK 1.4.8)", String.format("<img src='%s&mode=kekule' alt='%s' title='%s'>", ref,smiles==null?"":smiles,smiles==null?"":smiles)));
		b.append("</td><td>");
		b.append(AmbitResource.printWidget("Kekule (CDK<1.4.8)", String.format("<img src='%s&mode=kekuleold' alt='%s' title='%s'>", ref,smiles==null?"":smiles,smiles==null?"":smiles)));
		b.append("</td></tr></table>");
		*/
		return b.toString();
		
	}

	@Override
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
			if ((form != null) && (form.size()>0))
				query_smiles = form.getFirstValue(QueryResource.search_param);
			else query_smiles = null;
		} catch (Exception x) {
			query_smiles = "";
		}
		
		
		w.write(String.format("<form action='' method='%s'>\n",method));
		w.write("<table width='100%'>");
		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>",QueryResource.search_param,"SMILES or InChI"));
		w.write("<td>");
		w.write(String.format("<input name='%s' size='80' value='%s'>\n",
				QueryResource.search_param,query_smiles==null?"":query_smiles));
		w.write("</td>");
		w.write("<td><input type='submit' value='Display'></td>");
		w.write("</tr>\n");

		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>","smarts","SMARTS (optional)"));
		w.write("<td>");
			w.write(String.format("<input name='%s' size='80' value='%s' title='Highlights the substructure, specified by SMARTS'>",
					"smarts",getSmarts()==null?"":getSmarts()));
		w.write("</td>");			
		w.write("<td>&nbsp;</td></tr>\n");
		w.write("</table>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
		w.write("<b title='These pages offer minimalistic user interface to AMBIT implementation of OpenTox REST services. Full featured user interface is available via external applicaiton, like ToxPredict (http://toxpredict.org), ToxCreate (http://toxcreate.org) and QPRF editor. More applications are under development.'><i>These pages and AMBIT REST services are under development!</i></b>");		
		w.write("</td>");
		w.write("<td align='center' valign=='bottom' width='256px'>");
//		w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		
		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
	}	

}

class SmartsPatternSelector implements IProcessor<IAtomContainer,IChemObjectSelection> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2781667048103591227L;
	protected String smarts;
	public SmartsPatternSelector(String smarts) {
		this.smarts = smarts;
	}
	public long getID() {
		return 0;
	}

	public boolean isEnabled() {
		return true;
	}

	public IChemObjectSelection process(IAtomContainer target)
			throws AmbitException {
		ISmartsPattern pattern = new SmartsPatternAmbit(smarts);
		if(pattern.match(target)>0) {
			IAtomContainer selected = pattern.getMatchingStructure(target);
			return new SingleSelection<IAtomContainer>(selected);
		} else return null;
	}

	public void setEnabled(boolean value) {
		
	}
	
}
