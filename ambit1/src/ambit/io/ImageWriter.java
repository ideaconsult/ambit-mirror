package ambit.io;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitResult;
import ambit.processors.results.AmbitResultsList;
import ambit.processors.results.FingerprintProfile;

/**
 * Writes image out of {@link ambit.processors.IAmbitResult}.
 * TODO refactor.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class ImageWriter extends DefaultChemObjectWriter implements
		IAmbitResultWriter {

	
	protected OutputStream stream;
	public ImageWriter(Writer out) {
		super();
		stream = null;
		//TODO
	}
    public ImageWriter(OutputStream output) {
    	super();
    	stream = output;

    }
    
	public void writeResult(IAmbitResult result) throws AmbitException {
		if (result instanceof AmbitResultsList) {
			AmbitResultsList results = (AmbitResultsList) result;
			for (int i=0; i < results.size(); i++) 
				if ((IAmbitResult) results.get(i) != null)
					writeResult(((IAmbitResult) results.get(i)));	
			
		} else if (result instanceof FingerprintProfile) {

			String[] seriesNames = new String[] {((FingerprintProfile) result).toString()};
			
			FingerprintProfile fp = (FingerprintProfile) result;
			String[] categoryNames = new String[fp.getLength()];
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			
			for (int i=0; i < fp.getLength();i++) {
				dataset.addValue(fp.getBitFrequency(i), seriesNames[0], new Integer(i));
			}	
			JFreeChart chart = ChartFactory.createBarChart3D(
					"Consensus fingerprint",
					"Bits", 
					"Frequency", 
					dataset, 
					PlotOrientation.VERTICAL,
					true, false, false);
			chart.setBackgroundPaint(Color.white);
			chart.setAntiAlias(true);
		

			BufferedImage image = chart.createBufferedImage(400,200);
			
			try {
				//ImageIO.write(image, "jpeg", stream);
				ImageIO.write(image, "png", stream);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
	
		}

	}

	public void write(IChemObject object) throws CDKException {
		// TODO Auto-generated method stub

	}

	public IResourceFormat getFormat() {
		// TODO Define image format
		return null;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
	protected BufferedImage generateTestImage(int width, int height,
			Color background) {
		String[] slices = new String[] { "60", "300" };
		Color[] colors = new Color[] { Color.red, Color.blue };
		int[] sizes = new int[slices.length];

		for (int i = 0; i < slices.length; i++) {
			sizes[i] = Integer.parseInt(slices[i]);
		}

		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		int arc = 0;
		for (int i = 0; i < sizes.length; i++) {
			g.setColor(colors[i]);
			g.fillArc(0, 0, width, height, arc, sizes[i]);
			arc += sizes[i];
		}
		return buffer;
	}
	public String toString() {
		return "Writing to image";
	}
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        // TODO Auto-generated method stub

    }
}
