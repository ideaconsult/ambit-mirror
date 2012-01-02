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

package qmrf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import ambit2.base.io.DownloadTool;
import ambit2.qmrf.attachments.QMRFAttachment;


public class QMRFAttachmentTest {
	@Test
	public void test() throws Exception {

			QMRFAttachment a = new QMRFAttachment("molecules");
			a.setEmbedded(true);
			a.setContent(null);
			
			URL url = getClass().getClassLoader().getResource("ambit\\ui\\images\\search.png");

			a.setUrl(url.toString());
			
			a.setFiletype("xxx");
			QMRFAttachment a1 = transform(a);
	        System.out.println(a1);
	        System.out.println("Content\t"+a1.getContent());
	        Assert.assertEquals(a.getEmbedded(),a1.getEmbedded());
	        Assert.assertEquals(a.getContent(),a1.getContent());
	        
	        a.setContent(null);
	        InputStream r = a.getContentAsStream();
	        File file = new File("test.png");
	        file.deleteOnExit();
	        
	        OutputStream w = new FileOutputStream(file);
	        a.writeContent(r, w);
	        r.close();
	        w.close();
	        Assert.assertTrue(file.exists());
	        
	        r = a1.getContentAsStream();
	        File filenew = new File("test1.png");
	        filenew.deleteOnExit();
	        w = new FileOutputStream(filenew);
	        a1.writeContent(r, w);
	        r.close();
	        w.close();
	        Assert.assertTrue(filenew.exists());

	}
	public QMRFAttachment transform(QMRFAttachment a) throws Exception  {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        
	        factory.setNamespaceAware(true);      
	        factory.setValidating(true);        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.newDocument();
	        
			Element e = a.toXML(doc);
			doc.appendChild(e);
			
			DOMSource source = new DOMSource(doc);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty(OutputKeys.INDENT,"Yes");
	        xformer.setOutputProperty(OutputKeys.STANDALONE,"Yes");

	        StringWriter w = new StringWriter();
	        Result result = new StreamResult(w);
	        xformer.transform(source, result);
	        w.flush();
	        System.out.println(w.toString());
	        
	        QMRFAttachment a1 = new QMRFAttachment("molecules");
	        Document doc1 = builder.parse(new InputSource(new StringReader(w.toString())));
	        a1.fromXML(doc1.getDocumentElement());
	        return a1;
	        
	        
	}
}


