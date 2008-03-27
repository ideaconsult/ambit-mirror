/* DetailsPanel.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import ambit2.processors.results.FingerprintProfile;
import ambit2.processors.results.SimilarityMatrix;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 10, 2006
 */
public class AmbitResultViewer extends JPanel implements PropertyChangeListener {
	protected Image image = null;
	protected String caption = "";
	protected Object result = null;
	protected JScrollPane scrollPane;
	protected JLabel label; 
    public AmbitResultViewer(String caption) {
        super();
        addWidgets();
        this.caption = caption;
        setBorder(BorderFactory.createTitledBorder(caption));
    }
    protected void addWidgets() {
    	setLayout(new BorderLayout());
    	label = new JLabel();

		// Create a tabbed pane
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add( label );
        setPreferredSize(new Dimension(200,200));
        add(scrollPane,BorderLayout.CENTER);

    }
    
    /*
    public void paint(Graphics g) {
    	super.paint(g);
    	if (image != null)
    		g.drawImage( image, 0, 0, this );
    	g.drawString(caption, 0,0);
    }
    */
    public void propertyChange(PropertyChangeEvent e) {
    	
    	result = e.getNewValue();

    	System.out.println(e.getPropertyName());
    	if (result == null) { image = null; return; }

    	if (result instanceof FingerprintProfile) {
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
		
	
			image = chart.createBufferedImage(450,200);
			chart = null;
    	}
    	if (result instanceof SimilarityMatrix) {
    		image = ((SimilarityMatrix) result).toImage();
    	}
    	if (image == null) label.setIcon(null);
    	else label.setIcon(new ImageIcon(image));
    	
    	
    }
}
