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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;

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
}
