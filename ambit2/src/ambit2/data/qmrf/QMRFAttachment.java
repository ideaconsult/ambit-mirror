/* QMRFAttachment.java
 * Author: Nina Jeliazkova
 * Date: Feb 24, 2007 
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

package ambit2.data.qmrf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.io.XMLException;

/**
 * Attachments can be linked or embedded, if embedded, they are expected to be base64 encoded.
 * This class uses jakarta commons codec for base64 encoding/decoding.
 * @author nina
 *
 */
public class QMRFAttachment  implements InterfaceQMRF {

	protected static final String attribute_embedded = "embedded";
    protected static final String attribute_url = "url";
    protected static final String attribute_filetype = "filetype";
    protected static final String attribute_description = "description";
    protected String name = "document";
    protected String url;
    protected String filetype;
    protected String description;
    protected Object content=null;
    protected boolean embedded;

    public QMRFAttachment(String elementID,File file) throws Exception {
    	this(elementID,"file:///"+file.getAbsolutePath(),getSuffix(file.getName()),file.getName());
    	setContent(retrieveContent());
    	setEmbedded(content!=null);
    }    
    
    public QMRFAttachment(String elementID,String url, String filetype, String description) {
        this(elementID);

        setUrl(url);
        setFiletype(filetype);
        setDescription(description);
        setContent(null);
    }    
    public QMRFAttachment(String elementID) {
        super();
        setName(elementID);
        setUrl("");
        setFiletype("");
        setDescription("");
        setContent("");
        setEmbedded(false);
    }
    
    public synchronized String getFiletype() {
        return filetype;
    }
    public synchronized void setFiletype(String filetype) {
        if (filetype == null) this.filetype = "text";
        else this.filetype = filetype;
    }
    public synchronized String getUrl() {
        return url;
    }
    public synchronized void setUrl(String url) {
        if (url == null) this.url = "";
        else this.url = url;
    }
    
    public void fromXML(Element xml) throws XMLException {
        setFiletype(xml.getAttribute(attribute_filetype));
        setUrl(xml.getAttribute(attribute_url));
        setDescription(xml.getAttribute(attribute_description));
        String s = xml.getAttribute(attribute_embedded);
        
        if (s==null) setEmbedded(false);
        else
	        setEmbedded("YES".equals(s.toString()));
        if (getEmbedded()) {
        	try {
        		String c = xml.getTextContent();
        		content = new String(Base64.decodeBase64(c.getBytes(UTF8charset)),UTF8charset);
           	} catch (Exception x) {
        		throw new XMLException(x);
        		
        	}
        	
        } else setContent(null);
    }

	public void writeContent(File file) throws Exception {
		InputStream in = getContentAsStream();
		FileOutputStream out = new FileOutputStream(file);
		writeContent(in, out);
		out.flush();
		out.close();
		in.close();		
	}
    public long writeContent(InputStream in,OutputStream out) throws Exception {
		byte[] bytes = new byte[512];
		int len;
		long count = 0;
//		 read() returns -1 on EOF
		while ((len = in.read(bytes, 0, bytes.length)) >= 0) {
			out.write(bytes, 0, len);
			count += len;
		}
		out.flush();
		return count;
    }    

    public InputStream getContentAsStream() throws Exception {
    	System.out.println("getContentAsStream");
    	if (getEmbedded() && (content != null)) {
    		System.out.println("content!=null");
    		return new ByteArrayInputStream(content.toString().getBytes(UTF8charset));
    	} else {
    		
    		if (url.startsWith("file:///")) {
    			System.out.println("retrieving from file");
    			return new FileInputStream(new File(url.substring(8)));
    		} else {
    			System.out.println("retrieving from url");
				URL u = new URL(url);
				return new BufferedInputStream(u.openStream());
    		}
    	}
    }    
    public Object retrieveContent() throws Exception {
    	System.out.println("retrieveContent");
    	if (content != null) return content;
    	
		InputStream in = getContentAsStream();
		byte[] bytes = new byte[512];
		int len;
		OutputStream out = new ByteArrayOutputStream();
		writeContent(in, out);
	    in.close();
		String c = out.toString();
		out.close();
		return c;

    }
    public Element toXML(Document document) throws XMLException {
        Element element = document.createElement(getName());
        element.setAttribute(attribute_filetype,filetype);
        element.setAttribute(attribute_description,description);
        element.setAttribute(attribute_url,url);
        
        if (getEmbedded()) {
        	try {
        		if (content == null) {
        			content = retrieveContent();
        		}
        		System.out.println("encoding");
        		byte[] encoded = Base64.encodeBase64(content.toString().getBytes(UTF8charset));
        		

        		CDATASection  cdata = document.createCDATASection(
	            		new String(encoded,UTF8charset)
	            		);
		
	            if (cdata != null)
	            	element.appendChild(cdata);
	            	
        	} catch (Exception x) {
        		x.printStackTrace();
        		setEmbedded(false);
        	}
        } else setEmbedded(false);
        if (getEmbedded())
        	element.setAttribute(attribute_embedded,Answer.YES.toString());
        else element.setAttribute(attribute_embedded,Answer.NO.toString());
        return element;
    }    
    @Override
    public String toString() {
        return url + " (" + filetype + ")" + " embedded " + embedded;
    }
    @Override
    public boolean equals(Object obj) {
        QMRFAttachment a = (QMRFAttachment) obj;
        return name.equals(a.getName()) && url.equals(a.getUrl()) && filetype.equals(a.getFiletype());
    }
    public void setName(String name) {
        this.name = name;
        
    }
    public String getName() {
        return name;
    }
    public synchronized String getDescription() {
        return description;
    }
    public synchronized void setDescription(String description) {
        this.description = description;
    }
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	public boolean getEmbedded() {
		return embedded;
	}
	public void setEmbedded(boolean v) {
		System.out.println("embedded "+v);
		this.embedded = v;
	}
    public static String getSuffix(String fname) {
        String suffix = null;
        int i = fname.lastIndexOf('.');

        if(i > 0 && i < fname.length() - 1)
          suffix = fname.substring(i+1).toLowerCase();

        return suffix;
      }
	
}
