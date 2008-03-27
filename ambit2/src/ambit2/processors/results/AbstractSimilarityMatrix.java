/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.processors.results;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.table.AbstractTableModel;

import ambit2.io.MyIOUtilities;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;

public abstract class AbstractSimilarityMatrix extends AbstractTableModel implements
		IAmbitResult {
	protected String title="";
	protected static final float sat = (float)(200.0/240.0);
	protected static final float lum = (float)(200.0/240.0);
	public static Color valueToColor(Number value) {
        try {    
        	if (value.equals(Float.NaN)) return Color.WHITE;
			return new Color(Color.HSBtoRGB((float)((170-value.floatValue()*170)/240), sat,lum));
        } catch (Exception x) {
            return Color.white;
        }
			/*
			return new Color(Color.HSBtoRGB(0,sat,
					((float)value.floatValue()*180+60)/240
					));
					*/

	}

	public BufferedImage toImage() {
		int cell = 6;
		if ((getColumnCount() > 0) && (getRowCount()>0)) {
			BufferedImage bimage = new BufferedImage(getColumnCount()*cell, getRowCount()*cell, BufferedImage.TYPE_INT_ARGB);
			Graphics g2d = bimage.createGraphics();
            for (int col = 0; col < getColumnCount(); col++) {
                for (int row = 0; row < getRowCount(); row++) {
				
					Object o = getValueAt(row,col);
					g2d.setColor(Color.white);
					g2d.fillRect(col*cell,row*cell, cell,cell);
					if (o instanceof Float) {
						g2d.setColor(valueToColor(((Float)o).floatValue()));
						g2d.fillRect(col*cell+1,row*cell+1, cell-1,cell-1);
					}
				}
			}
			return bimage;
		} else return null;
	}

	public void write(Writer writer) throws AmbitException {
		BufferedWriter w = new BufferedWriter(writer);
		String lineDelimiter =  java.lang.System.getProperty("line.separator");
		char delimiter = ',';
		try {
			for (int col = 0; col < getColumnCount(); col++) {
				if (col > 0)
				w.write(delimiter);
				w.write(getColumnName(col));
			}	
			w.write(lineDelimiter);
			for (int row = 0; row < getRowCount(); row++) {
				for (int col = 0; col < getColumnCount(); col++) {
					if (col > 0)
						w.write(delimiter);
					Object o = getValueAt(row,col);
					w.write(o.toString());
					
				}
				w.write(lineDelimiter);
			}
			w.flush();
		} catch (IOException x) {
			throw new AmbitException(x);
		}


	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		
	}




}


