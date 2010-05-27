package ambit2.rest.algorithm.chart;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Iterator;

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

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.chart.PieChartGenerator;
import ambit2.db.chart.PieChartGeneratorDataset;
import ambit2.db.chart.PropertiesChartGenerator;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.property.ProfileReader;

public class ChartResource extends ServerResource {
	public static final String resource = "/chart";
	public static final String resourceKey = "mode";
	public enum ChartMode {
		pie,
		xy
	}
	protected Form params;
	protected SourceDataset dataset;
	protected String[] property;

	protected int w = 400;
	protected int h = 400;
	protected ChartMode mode = ChartMode.pie;

	public ChartMode getMode() {
		return mode;
	}

	public void setMode(ChartMode mode) {
		this.mode = mode;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
		
		String uri = getParams().getFirstValue(OpenTox.params.dataset_uri.toString());
		dataset = new SourceDataset(uri);
		dataset.setId((Integer)OpenTox.URI.dataset.getId(uri,getRequest().getRootRef()));
		
		property = getParams().getValuesArray(OpenTox.params.feature_uris.toString());
		
		w = 400; h = 400;
		try { w = Integer.parseInt(getParams().getFirstValue("w"));} catch (Exception x) {w =400;}
		try { h = Integer.parseInt(getParams().getFirstValue("h"));} catch (Exception x) {h =400;}		
		
		try {
			mode = ChartMode.valueOf(getRequest().getAttributes().get(resourceKey).toString());
		} catch (Exception x) {
			mode = ChartMode.pie;
		}
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
	
	protected BufferedImage createImage()  throws ResourceException {
		BufferedImage image= null;
		Connection connection = null;
		try {

    		DBConnection dbc = new DBConnection(getContext());
    		connection = dbc.getConnection(getRequest());    		
    		
    		Template profile = new Template();
    		ProfileReader reader = new ProfileReader(getRequest().getRootRef(),profile);
    		reader.setCloseConnection(false);
    		reader.setConnection(connection);
    		for (String p : property) reader.process(new Reference(p));
    		
    		switch (mode) {
    		case pie: {
    			Iterator<Property> i = profile.getProperties(true);
    			while (i.hasNext()) {
	    			PieChartGenerator chart = new PieChartGeneratorDataset();
	    			chart.setProperty(i.next());    
	    			chart.setConnection(connection);
	    			chart.setWidth(w);
	    			chart.setHeight(h);    
	    			image = chart.process(dataset);
	    			break;
    			}
    			
    			break;
    		}
    		case xy: {
    			Property[] p = new Property[2];
    			int i=0;
    			Iterator<Property> it = profile.getProperties(true);
    			while (it.hasNext()) {
    				p[i] = it.next();
    				i++;
    				if (i>=2) break;
    			}
    			PropertiesChartGenerator chart = new PropertiesChartGenerator();
    			chart.setPropertyX(p[0]);
    			chart.setPropertyY(p.length<2?p[0]:p[1]);   
    			chart.setConnection(connection);
    			chart.setWidth(w);
    			chart.setHeight(h);    
    			image = chart.process(dataset);
    			break;
    		}    		
    		default: {
    			image = null;
    		}
    		}
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}	
       	if (image ==  null) 
	        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
       	return image;
	}
	public Representation get(Variant variant) throws ResourceException {
		final BufferedImage image = createImage();
       	
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

	}		
}
