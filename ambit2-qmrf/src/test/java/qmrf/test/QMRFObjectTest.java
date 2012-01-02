/* QMRFObjectTest.java
 * Author: Nina Jeliazkova
 * Date: Feb 19, 2007 
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

package qmrf.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import ambit2.base.log.AmbitLogger;
import ambit2.qmrf.QMRFObject;
import ambit2.qmrf.attachments.QMRFAttachment;
import ambit2.qmrf.attachments.QMRFAttachments;
import ambit2.qmrf.chapters.QMRFChapter;
import ambit2.qmrf.chapters.QMRFSubChapterText;
import ambit2.qmrf.converters.Qmrf_Xml_Excel;
import ambit2.qmrf.converters.Qmrf_Xml_Pdf;

public class QMRFObjectTest {

	
	@BeforeClass
    public static void setUp() throws Exception {
        AmbitLogger.configureLog4j(true);
    }

    
    public void xtestRoundtrip() throws Exception  {
        
        QMRFObject o = new QMRFObject();
        File file = new File("testroundtrip.xml");	
        FileOutputStream out = new FileOutputStream(file);
            
        o.write(out);
        out.close();
            
        QMRFObject o1 = new QMRFObject();
        FileInputStream in = new FileInputStream(file);
        o1.read(in);
        out.close();
            
        Assert.assertEquals(o,o1);
            
            //ArrayList<QMRFChapter> c = o1.chapters;
        Assert.assertEquals(o.getChapters().size(),o1.getChapters().size());

    }
    
    @Test
    public void testRead() throws Exception {
        QMRFObject o = new QMRFObject();
        
        InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/qmrfeditor/qmrf.xml"); //"data/qmrf/test_1_2.xml");
            o.read(in);
            in.close();
            
            Assert.assertEquals(10,o.getChapters().size());
            Assert.assertNotSame("",o.getChapters().get(0).getName());
            
            QMRFSubChapterText subc = (QMRFSubChapterText) ((QMRFChapter)o.getChapters().get(0)).getSubchapters().getItem(0);
            subc.setText("My test");
            
            File file = new File("test.xml");
            file.deleteOnExit();
            FileOutputStream out = new FileOutputStream(file);
            
            o.write(out);
            out.close();
            
            QMRFObject o1 = new QMRFObject();
            in = new FileInputStream(file);
            o1.read(in);
            in.close();
            
            subc = (QMRFSubChapterText) ((QMRFChapter)o1.getChapters().get(0)).getSubchapters().getItem(0);
            Assert.assertEquals("My test",subc.getText());            
            

    }
   
    public void xtestQMRFAttachment() throws Exception {
            QMRFAttachments attachments  = new QMRFAttachments("attachments");
            QMRFAttachment a = new QMRFAttachment("data","file","pdf","");
            attachments.addAttachment(2, a);
            attachments.addAttachment(0, new QMRFAttachment("data","url","sdf",""));            
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element e = a.toXML(document);
            
            QMRFAttachment aa = new QMRFAttachment("data");
            aa.fromXML(e);
            
            Assert.assertEquals(a.getUrl(),aa.getUrl());
            Assert.assertEquals(a.getFiletype(),aa.getFiletype());
            
            
            e = attachments.toXML(document);
            //System.out.println(attachments);
            QMRFAttachments attachments1  = new QMRFAttachments("attachments");
            attachments1.fromXML(e);
            //System.out.println(attachments1);
            Assert.assertEquals(attachments,attachments1);

        
    }
    

    public void xtestXSLT() throws Exception{
    	QMRFObject o = new QMRFObject();
    	InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/qmrfeditor/qmrf.xml"); 
    		o.read(in); //""web_draft-qrf_v1.02_BCFGramatica_ManuelaPavan.xml"));
    		in.close();
    		File out = new File("qmrf.fo");
            //String xsl = "qmrffo.xsl";
            //String xsl = "QMRF_Template_FO.xml";
            String xsl = "qmrffo.xsl";
    		o.xsltTransform(new FileInputStream(xsl),new FileOutputStream(out));
    		Assert.assertTrue(out.exists());

    }
    /*
    public void PDF_FO() throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

 	         //InputStream stream = this.getClass().getClassLoader().getResourceAsStream("ambit2/qmrfeditor/qmrf.fo");
	         //InputStream stream = new FileInputStream("qmrf_transformed.xml");
             
             InputStream stream = new FileInputStream("qmrf.fo") ; //"QMRF_Template_FO.xml");
             DocumentBuilder builder = factory.newDocumentBuilder();
	         Document fodom = builder.parse(stream);    	
	         QMRFObject o = new QMRFObject();
	         o.read(new FileInputStream("qmrf_empty.xml"));	      
    		 o.convertDOM2PDF(fodom,new FileOutputStream("qmrf.pdf"));
             
             //QMRFObject o = new QMRFObject();
             //(new File("qmrf.pdf")).delete();
             //o.export2PDF(new FileInputStream("qmrffo.xsl"), new FileOutputStream("qmrf.pdf"));
                        
             assertTrue((new File("qmrf.pdf")).exists());
 
    }   

    public void PDF1() throws Exception{
            QMRFObject o = new QMRFObject();
            (new File("qmrf.pdf")).delete();
            o.export2PDF(new FileInputStream("qmrffo.xsl"), new FileOutputStream("qmrf.pdf"));
           
            assertTrue((new File("qmrf.pdf")).exists());
    }
    */
    
    @Test
    public void testPDF() throws Exception {
    	//URL ttf = getClass().getClassLoader().getResource("ambit2/qmrfeditor/fonts/times.ttf");
    	
    	Qmrf_Xml_Pdf qpdf = new Qmrf_Xml_Pdf("C:/Windows/Fonts/times.ttf");
    	
         File pdf = new File("qmrf_1_2.pdf");
         pdf.deleteOnExit();
         if (pdf.exists()) pdf.delete();
         
         StringBuffer b = new StringBuffer();
         InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/qmrfeditor/qmrf.xml"); //"data/qmrf/test_1_2.xml");
         BufferedReader r = new BufferedReader(new InputStreamReader(in));
         String line;
         while ( (line = r.readLine()) != null)
        	 b.append(line);
         r.close();
         
         qpdf.xml2pdf(b.toString().trim(),new FileOutputStream(pdf));
        
         Assert.assertTrue(pdf.exists());
         Assert.assertTrue(pdf.length()>0);
    }   
    
    @Test
    public void testPDFReader() throws Exception {
    	//URL ttf = getClass().getClassLoader().getResource("ambit2/qmrfeditor/fonts/times.ttf");
    	Qmrf_Xml_Pdf qpdf = new Qmrf_Xml_Pdf("C:/Windows/Fonts/times.ttf");
         File pdf = new File("qmrf_1_2.pdf");
         pdf.deleteOnExit();
         if (pdf.exists()) pdf.delete();
         
         URL url = getClass().getClassLoader().getResource("ambit2/qmrfeditor/qmrf.xml");
         File xml = new File(url.getFile());
         qpdf.xml2pdf(new InputSource(new FileReader(xml)),new FileOutputStream(pdf));
         //qpdf.xml2pdf(new FileReader("data/qmrf/test_1_2.xml"),new FileOutputStream(pdf));
         Assert.assertTrue(pdf.exists());
         Assert.assertTrue(pdf.length()>0);
    }   
    
    @Test
    public void xtestXLS() throws  Exception {
	         Qmrf_Xml_Excel qpdf = new Qmrf_Xml_Excel();
	     	InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/qmrfeditor/qmrf.xml"); 

	         File xls = new File("qmrf_1_2.xls");
	         xls.deleteOnExit();
	         if (xls.exists()) xls.delete();
	         qpdf.xml2excel(new InputSource(in),new FileOutputStream(xls));	      
             Assert.assertTrue(new File("qmrf_1_2.xls").exists());
    }      
    /*
    public void testReadDTD()  throws Exception  {
            QMRFObject o = new QMRFObject();
            //o.readSchema(new FileInputStream("qmrf_empty.xml"));
            
            SAXParser parser = new SAXParser();
            
            DeclHandler handler = new CustomDeclHandler(o);
            parser.setProperty("http://xml.org/sax/properties/declaration-handler",
            handler);
            
            InputStream stream = new FileInputStream("qmrf_empty.xml");
            parser.parse(new InputSource(stream));

    }
    */
    
}
class CustomDeclHandler implements org.xml.sax.ext.DeclHandler {
    protected QMRFObject o;
    public CustomDeclHandler(QMRFObject o) {
        this.o = o;
    }
    public void attributeDecl(java.lang.String elementName,
                              java.lang.String attributeName,
                              java.lang.String type,
                              java.lang.String valueDefault,
                              java.lang.String value)
    {
    
       System.out.println("ATTRIBUTE: ");
       System.out.println("Element Name: " + elementName);
       System.out.println("Attribute Name: " + attributeName);
       System.out.println("Type: " + type);
       System.out.println("Default Value: " + valueDefault);
       System.out.println("Value: " + value);
       System.out.println();
    }
    
    public void elementDecl(java.lang.String name,
                            java.lang.String model)
    {
    
       System.out.println("ELEMENT: ");
       System.out.println("Name: " + name);
       System.out.println("Model: " + model);
       System.out.println();
    }
    
    public void externalEntityDecl(java.lang.String name,
                                   java.lang.String publicId,
                                   java.lang.String systemId)
    {
      System.out.println("EXTERNAL ENTITY: " + name + publicId + systemId);
    }
    
    public void internalEntityDecl(java.lang.String name,
                                  java.lang.String value)
    {
      
      if (name.startsWith("help")) {
          System.out.println("INTERNAL ENTITY HELP: " + name + value);
      } else System.out.println("INTERNAL ENTITY: " + name + value);
    }

}
