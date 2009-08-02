package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.rest.error.EmptyMoleculeException;

public abstract class AbstractDepict extends Resource {

	protected String smiles = null;
	public AbstractDepict(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
	
	}
	protected abstract BufferedImage getImage(String smiles) throws AmbitException;
	
	public Representation getRepresentation(Variant variant) {
		
		try {
			try {
				this.smiles = Reference.decode(getRequest().getAttributes().get("smiles").toString());
			} catch (Exception x) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, x);
				return null;
			}				
	        if (smiles != null) {
	        	final BufferedImage image = getImage(smiles);
	        	if (image ==  null) {
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Can't generate image");
	        		return null;
	        	}
	        	return new OutputRepresentation(MediaType.IMAGE_PNG) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			ImageIO.write(image, "PNG", out);
	        			out.flush();
	        			out.close();
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, new EmptyMoleculeException());
	        	return null;   	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,x);
			return null;
		
		}
	}		
}