/* PBTPage.java
 * Author: Nina Jeliazkova
 * Date: Oct 4, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.plugin.pbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class PBTPage extends JPanel {
    protected Document document;
    
    public PBTPage(String definition) {
        try {
            document = parse(definition);
        } catch (Exception x) {
            document = null;
        }
    }
    public Document parse(String definition) throws IOException, ParserConfigurationException, SAXException {
        InputStream in = PBTPage.class.getClassLoader().getResourceAsStream(definition);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new InputStreamReader(in,"UTF-8")));     
        
            /*
             * Element e = doc.getDocumentElement();
            if ("template".equals(e.getNodeName())) {
                setName(e.getAttribute("name"));
                NodeList nodes = e.getChildNodes();
                for (int i=0; i < nodes.getLength();i++) {
                    Node node = nodes.item(i);
                    if (node instanceof Element) {
                        Element element = (Element) node;
                        String name = element.getNodeName();
                        if ("field".equals(name)) {
                            TemplateField field = new TemplateField(element.getAttribute("name").toString());
                            field.setUnits(element.getAttribute("units").toString());
                            String numeric=element.getAttribute("numeric").toString();
                            field.setNumeric(numeric.toLowerCase().equals("true"));
                            String result=element.getAttribute("result").toString();
                            field.setResult(result.toLowerCase().equals("true"));
                            add(field);
                        }
                    }
                }
                return true;
            } else return false;
            */
        
    }
    @Override
    public String toString() {
        if (document == null)
            return super.toString();
        else 
            return document.getDocumentElement().getAttribute("name");
    }
}
