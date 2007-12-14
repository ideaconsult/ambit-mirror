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

package ambit.data.qmrf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import ambit.data.qmrf.QMRFAttachment;
import ambit.data.qmrf.InterfaceQMRF.Answer;


public class QMRFAttachmentTest extends TestCase {
	public void test() {
		try {
			QMRFAttachment a = new QMRFAttachment("molecules");
			a.setEmbedded(true);
			//a.setContent("אבצהופ\u0900xxxabcdef");
			a.setContent(null);
			//a.setUrl("http://perfsonar.acad.bg/MA.conf");
			//a.setUrl("file:///d:\\nina\\IdeaConsult\\jrc-2006-cancmut\\benigni\\sa1_l_iss2_nina.sdf");
			a.setUrl("file:///C:\\Documents and Settings\\nina\\My Documents\\My Pictures\\aer.jpg");
			
			a.setFiletype("xxx");
			QMRFAttachment a1 = transform(a);
	        System.out.println(a1);
	        System.out.println("Content\t"+a1.getContent());
	        assertEquals(a.getEmbedded(),a1.getEmbedded());
	        assertEquals(a.getContent(),a1.getContent());
	        
	        a.setContent(null);
	        InputStream r = a.getContentAsStream();
	        OutputStream w = new FileOutputStream(new File("test.jpg"));
	        System.out.println("bytes "+a.writeContent(r, w));
	        r.close();
	        w.close();
	        
	        r = a1.getContentAsStream();
	        w = new FileOutputStream(new File("test1.jpg"));
	        System.out.println("bytes "+a1.writeContent(r, w));
	        r.close();
	        w.close();
	        
		} catch (Exception x) {
			x.printStackTrace();
			fail(x.getMessage());
		}
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


