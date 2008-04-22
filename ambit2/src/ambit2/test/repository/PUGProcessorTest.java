/* PUGProcessorTest.java
 * Author: Nina Jeliazkova
 * Date: Apr 20, 2008 
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

package ambit2.test.repository;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit2.repository.StructureRecord;
import ambit2.repository.processors.PUGProcessor;

public class PUGProcessorTest extends TestCase {
    protected PUGProcessor pug;
    @Before
    public void setUp() throws Exception {
        pug = new PUGProcessor();
    }

    @After
    public void tearDown() throws Exception {
    }
    public void testGetInput() throws Exception {
        List<StructureRecord> sids = new ArrayList<StructureRecord>();
        sids.add(new StructureRecord(-1,-1,"1",PUGProcessor.PUBCHEM_CID));
        sids.add(new StructureRecord(-1,-1,"99",PUGProcessor.PUBCHEM_CID));
        StringWriter out = new StringWriter();
        PUGProcessor.createDownloadRequest(sids,out);
        System.out.println(out.toString());
    }
    public void testProcess() throws Exception {
        List<StructureRecord> sids = new ArrayList<StructureRecord>();
        sids.add(new StructureRecord(-1,-1,"111",PUGProcessor.PUBCHEM_CID));
        sids.add(new StructureRecord(-1,-1,"992",PUGProcessor.PUBCHEM_CID));
        List<StructureRecord> results = pug.process(sids);
        for (StructureRecord record: results) {
        	assertEquals("sdf",record.getFormat());
        	System.out.println(record.getContent());
        }
        assertEquals(2,results.size());        
    }    
    /*
    public void xtestParseOutput() throws Exception {
    	StringBuilder b = new StringBuilder();
    	b.append("<?xml version=\"1.0\"?>");
    	b.append("<!DOCTYPE PCT-Data PUBLIC \"-//NCBI//NCBI PCTools/EN\" \"http://pubchem.ncbi.nlm.nih.gov/pug/pug.dtd\">");
    	b.append("<PCT-Data>");
    	b.append("  <PCT-Data_output>");
    	b.append("    <PCT-OutputData>");
    	b.append("      <PCT-OutputData_status>");
    	b.append("        <PCT-Status-Message>");
    	b.append("          <PCT-Status-Message_status>");
    	b.append("            <PCT-Status value=\"success\"/>");
    	b.append("          </PCT-Status-Message_status>");
    	b.append("        </PCT-Status-Message>");
    	b.append("      </PCT-OutputData_status>");
    	b.append("      <PCT-OutputData_output>");
    	b.append("        <PCT-OutputData_output_download-url>");
    	b.append("          <PCT-Download-URL>");
    	b.append("            <PCT-Download-URL_url>ftp://ftp-private.ncbi.nlm.nih.gov/pubchem/.fetch/707367541683016070.sdf.gz</PCT-Download-URL_url>");
    	b.append("          </PCT-Download-URL>");
    	b.append("        </PCT-OutputData_output_download-url>");
    	b.append("      </PCT-OutputData_output>");
    	b.append("    </PCT-OutputData>");
    	b.append("  </PCT-Data_output>");
    	b.append("</PCT-Data>");    	
    	
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(b.toString())));
        doc.normalize();    	
        List<StructureRecord> results = PUGProcessor.parseOutput(doc);
        for (StructureRecord result: results) {
            URL url = new URL(StructureRecord.getc);
            URLConnection connection= url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
            String input;
            while((input = in.readLine()) != null) {
            //    response.append(input + "\r");
                System.out.println(input + "\r");  
            }
        }
        System.out.println(results);
    }
    */
    public void testParseOutputWaiting() throws Exception {
    	StringBuilder b = new StringBuilder();
    
	    b.append("<?xml version=\"1.0\"?>");
	    b.append("<!DOCTYPE PCT-Data PUBLIC \"-//NCBI//NCBI PCTools/EN\" \"http://pubchem.ncbi.nlm.nih.gov/pug/pug.dtd\">");
	    b.append("	    <PCT-Data>");
	    b.append("  <PCT-Data_output>");
	    b.append("    <PCT-OutputData>");
	    b.append("      <PCT-OutputData_status>");
	    b.append("        <PCT-Status-Message>");
	    b.append("          <PCT-Status-Message_status>");
	    b.append("            <PCT-Status value=\"success\"/>");
	    b.append("          </PCT-Status-Message_status>");
	    b.append("        </PCT-Status-Message>");
	    b.append("      </PCT-OutputData_status>");
	    b.append("      <PCT-OutputData_output>");
	    b.append("        <PCT-OutputData_output_waiting>");
	    b.append("          <PCT-Waiting>");
	    b.append("            <PCT-Waiting_reqid>1130503662625978916</PCT-Waiting_reqid>");
	    b.append("          </PCT-Waiting>");
	    b.append("        </PCT-OutputData_output_waiting>");
	    b.append("      </PCT-OutputData_output>");
	    b.append("    </PCT-OutputData>");
	    b.append("  </PCT-Data_output>");
	    b.append("</PCT-Data>");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(b.toString())));
        doc.normalize();    	
        List<StructureRecord> results = PUGProcessor.parseOutput(doc);
        assertNotNull(results);
        assertEquals(1,results.size());
        assertEquals("1130503662625978916", results.get(0).getContent());
    }
    /**
     * poll
<PCT-Data>
<PCT-Data_input>
<PCT-InputData>
<PCT-InputData_request>
<PCT-Request>
<PCT-Request_reqid>402936103567975582</PCT-Request_reqid>
<PCT-Request_type value="status"/>
</PCT-Request>
</PCT-InputData_request>
</PCT-InputData>
</PCT-Data_input>
</PCT-Data>
     */
}
