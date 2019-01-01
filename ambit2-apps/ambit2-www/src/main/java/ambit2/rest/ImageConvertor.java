package ambit2.rest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.AbstractObjectConvertor;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

/**
 * An IProcessor} , converting between arbitrary Content and restlet Representation.
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public class ImageConvertor<T,Q extends IQueryRetrieval<T>>  extends AbstractObjectConvertor<T,Q,BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public ImageConvertor(QueryReporter<T, Q, BufferedImage> reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}

	@Override
	public Representation processDoc(final BufferedImage image) throws AmbitException {
		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
	            	try {
	            		if (MediaType.IMAGE_PNG.equals(mediaType))
	            			ImageIO.write(image,"png",stream);
	            		else if (MediaType.IMAGE_JPEG.equals(mediaType))
	            			ImageIO.write(image,"jpeg",stream);
	            		else if (MediaType.IMAGE_GIF.equals(mediaType))
	            			ImageIO.write(image,"gif",stream);	
	            		else if (MediaType.IMAGE_TIFF.equals(mediaType))
	            			ImageIO.write(image,"tiff",stream);	  
	            		else if (MediaType.IMAGE_BMP.equals(mediaType))
	            			ImageIO.write(image,"bmp",stream);	   
	            		else ImageIO.write(image,"png",stream);	            		
	            	} catch (Exception x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		
	            	} finally {
	            		try {
	            			if (stream !=null) stream.flush(); 
	            		}
	            		catch (IOException x) {
	            			Context.getCurrentLogger().warning(x.getMessage());
	            		}
	            		catch (Exception x) {
	            			Context.getCurrentLogger().warning(x.getMessage());

	            		}
	            	}
	            }
	        };		
	}

	@Override
	protected BufferedImage createOutput(Q query) throws AmbitException {
		return null;
	};	


}