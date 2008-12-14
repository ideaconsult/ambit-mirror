/*
Copyright (C) 2005-2008  

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

package ambit2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;

/**
 * Useful functions. TODO - move to core package?
 * @author nina
 *
 */
public class Utils {
	
    public static ImageIcon createImageIcon(String path) throws Exception {
            java.net.URL imgURL = ClassLoader.getSystemResource(path);
    
            if (imgURL != null) 
                return new ImageIcon(imgURL);
            else 
                throw new FileNotFoundException(path);

    }
    public static String getHelp(Class clazz) {
        try {
            InputStream stream = clazz.getClassLoader().getResourceAsStream("ambit2/help/"+clazz.getName()+".help");
            return getText(stream,"Help TODO "+ clazz.getName());
        } catch (Exception x) {
            return x.getMessage();
        }       
    }    
    public static String getTitle(Class clazz) {
        try {
            InputStream stream = clazz.getClassLoader().getResourceAsStream("ambit2/help/"+clazz.getName()+".title");
            return getText(stream,clazz.getName());
        } catch (Exception x) {
            return x.getMessage();
        }       
    }
    public static String getText(InputStream stream, String defaultText) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));        
            StringBuffer buffer = new StringBuffer();
            String line = defaultText;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            return buffer.toString();
    }    
    /**
     * TODO resolve Duplicate with CompoundImageTools method
     * @param g2
     * @param background
     * @param borderColor
     * @param shadowWidth
     * @param shape
     */
    public static void paintBorderShadow(Graphics2D g2, Color background, Color borderColor, int shadowWidth, Shape shape) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int sw = shadowWidth*2;
        for (int i=sw; i >= 2; i-=2) {
            float pct = (float)(sw - i) / (sw - 1);
            g2.setColor(getMixedColor(borderColor, pct,
                                      background, 1.0f-pct));
            g2.setStroke(new BasicStroke(i));
            g2.draw(shape);
        }
    }
    /**
     * TODO resolve Duplicate with CompoundImageTools method 
     * @param c1
     * @param pct1
     * @param c2
     * @param pct2
     * @return color
     */
    public static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }    
}
