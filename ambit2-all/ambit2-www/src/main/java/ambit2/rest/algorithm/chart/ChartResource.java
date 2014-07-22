package ambit2.rest.algorithm.chart;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.restlet.routing.Template;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.chart.BarChartGeneratorDataset;
import ambit2.db.chart.FingerprintHistogramDataset;
import ambit2.db.chart.HistogramChartGenerator;
import ambit2.db.chart.PieChartGenerator;
import ambit2.db.chart.PropertiesChartGenerator;
import ambit2.db.search.StoredQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.ProtectedResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.property.ProfileReader;

public class ChartResource extends ProtectedResource {
	public static final String resource = "/chart";
	public static final String resourceKey = "mode";
	public enum ChartMode {
		pie,
		xy,
		bar,
		histogram
	}
	protected Form params;
	protected ISourceDataset dataset;
	protected String[] property;

	protected boolean legend = false;
	protected boolean thumbnail = false;
	protected int w = 400;
	protected int h = 400;
	protected ChartMode mode = ChartMode.pie;
	protected String param;
	protected boolean logX = false;
	protected boolean logY = false;
	
	protected Double minX = null;
	protected Double maxX = null;
	
	public ChartMode getMode() {
		return mode;
	}

	public void setMode(ChartMode mode) {
		this.mode = mode;
	}

	protected ISourceDataset getDataset(String uri) throws InvalidResourceIDException {
		Map<String, Object> vars = new HashMap<String, Object>();
		Template template = OpenTox.URI.dataset.getTemplate(getRequest().getRootRef());
		String id = null;
		try {
			template.parse(uri, vars);
			id = vars.get(OpenTox.URI.dataset.getKey()).toString();
		} catch (Exception x) { return null; }
		
		if (id != null)  try {
			Integer idnum = new Integer(Reference.decode(id.toString()));
			SourceDataset dataset = new SourceDataset();
			dataset.setID(idnum);
			return dataset;
		} catch (NumberFormatException x) {
			if (id.toString().startsWith(DatasetStructuresResource.QR_PREFIX)) {
				String key = id.toString().substring(DatasetStructuresResource.QR_PREFIX.length());
				try {
					ISourceDataset dataset = new StoredQuery();
					dataset.setID(Integer.parseInt(key.toString()));
					return dataset;
				} catch (NumberFormatException xx) {
					throw new InvalidResourceIDException(id);
				}
			}
		} catch (Exception x) {
			throw new InvalidResourceIDException(id);
		}
		return null;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		
		String uri = getParams().getFirstValue(OpenTox.params.dataset_uri.toString());
		
		try {
			dataset = getDataset(uri);

		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,uri);
		}
		
		property = getParams().getValuesArray(OpenTox.params.feature_uris.toString());
		
		thumbnail = false;
		try { thumbnail = Boolean.parseBoolean(getParams().getFirstValue("thumbnail"));} catch (Exception x) {thumbnail = false;}	
		
		w = thumbnail?100:400; h = thumbnail?100:400;
		try { w = Integer.parseInt(getParams().getFirstValue("w"));} catch (Exception x) {}
		try { h = Integer.parseInt(getParams().getFirstValue("h"));} catch (Exception x) {}
		
		logX = false; logY = false;
		try { logX = Boolean.parseBoolean(getParams().getFirstValue("logX"));} catch (Exception x) {}
		try { logY = Boolean.parseBoolean(getParams().getFirstValue("logY"));} catch (Exception x) {}

		minX = null; maxX = null;
		try { minX = Double.parseDouble(getParams().getFirstValue("minX"));} catch (Exception x) {minX = null;}
		try { maxX = Double.parseDouble(getParams().getFirstValue("maxX"));} catch (Exception x) {maxX = null;}

		
		legend = false;
		try { legend = Boolean.parseBoolean(getParams().getFirstValue("legend"));} catch (Exception x) {legend = false;}	

		param = null;
		try { param = getParams().getFirstValue("param");} catch (Exception x) {param = null;}	

		
		try {
			mode = ChartMode.valueOf(getRequest().getAttributes().get(resourceKey).toString());
		} catch (Exception x) {
			mode = ChartMode.pie;
		}
	}
	
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getResourceRef(getRequest()).getQueryAsForm();
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
    		connection = dbc.getConnection();    		
    		
    		ambit2.base.data.Template profile = new ambit2.base.data.Template();
    		ProfileReader reader = new ProfileReader(getRequest().getRootRef(),
    				profile,getApplication().getContext(),getToken(),
    				getRequest().getCookies(),
    				getRequest().getClientInfo()==null?null:getRequest().getClientInfo().getAgent()
    						);
    		reader.setCloseConnection(false);
    		reader.setConnection(connection);
    		for (String p : property) reader.process(new Reference(p));
    		
    		switch (mode) {
    		case pie: {
    			Iterator<Property> i = profile.getProperties(true);
    			while (i.hasNext()) {
	    			PieChartGenerator<ISourceDataset> chart = new PieChartGenerator<ISourceDataset>();
	    			chart.setProperty(i.next());    
	    			chart.setConnection(connection);
	    			chart.setLegend(legend);
	    			chart.setThumbnail(thumbnail);
	    			chart.setWidth(w);
	    			chart.setHeight(h);  	    		
	    			chart.setLogX(logX);
	    			chart.setLogY(logY);
	    			image = chart.process(dataset);
	    			//ChartUtilities.writeImageMap(writer, name, info, useOverLibForToolTips)
	    			break;
    			}
    			
    			break;
    		}

    		case histogram: {
    			Iterator<Property> i = profile.getProperties(true);
    			while (i.hasNext()) {
	    			HistogramChartGenerator chart = new HistogramChartGenerator();
	    			chart.setLogX(logX);
	    			chart.setLogY(logY);
	    			chart.setMinX(minX);
	    			chart.setMaxX(maxX);
	    			
	    			chart.setPropertyX(i.next());    
	    			chart.setConnection(connection);
	    			chart.setLegend(legend);
	    			chart.setThumbnail(thumbnail);
	    			chart.setWidth(w);
	    			chart.setHeight(h); 	    			
	    			image = chart.process(dataset);
	    			//ChartUtilities.writeImageMap(writer, name, info, useOverLibForToolTips)
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
    			chart.setLogX(logX);
    			chart.setLogY(logY);
    			chart.setThumbnail(thumbnail);
    			chart.setPropertyX(p[0]);
    			chart.setPropertyY(p.length<2?p[0]:p[1]);   
    			chart.setConnection(connection);
    			chart.setWidth(w);
    			chart.setHeight(h);    
    			chart.setLegend(legend);
    			image = chart.process(dataset);
    			break;
    		}    		
    		case bar: {
    			Property[] p = new Property[2];
    			int i=0;
    			Iterator<Property> it = profile.getProperties(true);
    			while (it.hasNext()) {
    				p[i] = it.next();
    				i++;
    				if (i>=2) break;
    			}
    			if (i==0)  {
    				FingerprintHistogramDataset chart = new FingerprintHistogramDataset();
	    			chart.setLogX(logX);
	    			chart.setLogY(logY);
	    			chart.setConnection(connection);
 
	    			chart.setLegend(legend);
	    			chart.setParam(param);
	    			chart.setThumbnail(thumbnail);
	    			chart.setWidth(w);
	    			chart.setHeight(h); 	    			
	    			image = chart.process(dataset);
    			} else {
	    			BarChartGeneratorDataset chart = new BarChartGeneratorDataset();
	    			chart.setLogX(logX);
	    			chart.setLogY(logY);
	    			chart.setPropertyX(p[0]);
	    			chart.setPropertyY(p.length<2?p[0]:p[1]);   
	    			chart.setConnection(connection);
  
	    			chart.setLegend(legend);
	    			chart.setThumbnail(thumbnail);
	    			chart.setWidth(w);
	    			chart.setHeight(h);  	    			
	    			image = chart.process(dataset);
    			}
   			
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
		setFrameOptions("SAMEORIGIN");
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
