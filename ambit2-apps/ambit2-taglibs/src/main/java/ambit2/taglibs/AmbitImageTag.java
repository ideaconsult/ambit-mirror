package ambit2.taglibs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.rendering.CompoundImageTools;

public class AmbitImageTag extends AmbitMolTag {
	protected int width = 100;
	protected int height = 100;
	protected Color bgcolor = Color.white;
	
	public String getBgcolor() {
		return "#FFFFFF";
	}

	//TODO parse
	public void setBgcolor(String bgcolor) {
		this.bgcolor = Color.white;
	}

	@Override
	public void doTag() throws JspException, IOException {
		
		try {
			PageContext pageContext = (PageContext) getJspContext();
			IAtomContainer m = getMolecule(getMol());
		
			pageContext.getResponse().setContentType("image/png");
	   		OutputStream os = pageContext.getResponse().getOutputStream();
	   		
			CompoundImageTools tools = new CompoundImageTools(new Dimension(width,height));
	        tools.setBackground(bgcolor);
	        BufferedImage buffer = tools.getImage(m);
            try {
            	ImageIO.write(buffer, "png", os);
            } catch (Exception x) {
            	throw new JspException(x.toString());
                //ImageIO.write(tools.emptyImage(width, height, bgcolor, x.getMessage()), "png", os);
            }
    		tools = null;
    		
    		//os.close();            
        }
        catch (Exception x)
        {
            throw new JspException(x.toString());
        }
        
	}

	public String getHeight() {
		return Integer.toString(height);
	}

	public void setHeight(String height) {
		try {
			this.height = Integer.parseInt(height);
		} catch (Exception x) {
			this.height = 100;
		}
	}

	public String getWidth() {
		return Integer.toString(width);
	}

	public void setWidth(String width) {
		try {
			this.width = Integer.parseInt(width);
		} catch (Exception x) {
			this.width = 100;
		}
			
	}
	
	/*
	 *         	try {
        		SmartsQuery query = new SmartsQuery(getSmarts());
	        	int match = query.match(getMolecule(getMol()));
	            
	        	JspContext pageContext = getJspContext(); 
	        	pageContext.setAttribute(var, Integer.toString(match));
        	} catch (Exception x) {
        		throw new JspException(x);
        	}	
	 */
}
