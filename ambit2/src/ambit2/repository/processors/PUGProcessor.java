/* PUGProcessor.java
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

package ambit2.repository.processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ambit2.data.qmrf.SimpleErrorHandler;
import ambit2.repository.IProcessor;
import ambit2.repository.ProcessorException;

/**
 * 
 * Queries Pubchem Power User Gateway. 
 * All communication to PUG is via XML sent to the CGI at the URL:
 * http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 20, 2008
 */
public class PUGProcessor implements IProcessor<List<String>,List<String>> {
    /**
<pre>
Example:   You want to download CID 1 and CID 99 – being uids 1 and 99 in the “pccompound” Entrez database – in SDF format with gzip compression.

The typical flow of information is as follows.  First, the initial input XML is sent to PUG via HTTP POST.  Note the input data container with the download request and uid and format options:

<PCT-Data>
  <PCT-Data_input>
    <PCT-InputData>
      <PCT-InputData_download>
        <PCT-Download>
          <PCT-Download_uids>
            <PCT-QueryUids>
              <PCT-QueryUids_ids>
                <PCT-ID-List>
                  <PCT-ID-List_db>pccompound</PCT-ID-List_db>
                  <PCT-ID-List_uids>
                    <PCT-ID-List_uids_E>1</PCT-ID-List_uids_E>
                    <PCT-ID-List_uids_E>99</PCT-ID-List_uids_E>
                  </PCT-ID-List_uids>
                </PCT-ID-List>
              </PCT-QueryUids_ids>
            </PCT-QueryUids>
          </PCT-Download_uids>
          <PCT-Download_format value="sdf"/>
          <PCT-Download_compression value="gzip"/>
        </PCT-Download>
      </PCT-InputData_download>
    </PCT-InputData>
  </PCT-Data_input>
</PCT-Data>


If the request is small and finishes very quickly, you may get a final URL right away (see further below). But usually PUG will respond initially with a waiting message and a request ID (<PCT-Waiting_reqid>) such as:

<PCT-Data>
  <PCT-Data_output>
    <PCT-OutputData>
      <PCT-OutputData_status>
        <PCT-Status-Message>
          <PCT-Status-Message_status>
            <PCT-Status value="success"/>
          </PCT-Status-Message_status>
        </PCT-Status-Message>
      </PCT-OutputData_status>
      <PCT-OutputData_output>
        <PCT-OutputData_output_waiting>
          <PCT-Waiting>
            <PCT-Waiting_reqid>402936103567975582</PCT-Waiting_reqid>
          </PCT-Waiting>
        </PCT-OutputData_output_waiting>
      </PCT-OutputData_output>
    </PCT-OutputData>
  </PCT-Data_output>
</PCT-Data>


You would then parse out this request id, being “402936103567975582”, in this case, and use this id to “poll” PUG on the status of the request, composing an XML message like:

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

Note that here the request type “status” is used; there is also the request type “cancel” that you may use to cancel a running job. 

If the request is still running, you well get back another waiting message as above, and then you’d poll again after some reasonable interval. If the request is finished, you will get a final result message like:

<PCT-Data>
  <PCT-Data_output>
    <PCT-OutputData>
      <PCT-OutputData_status>
        <PCT-Status-Message>
          <PCT-Status-Message_status>
            <PCT-Status value="success"/>
          </PCT-Status-Message_status>
        </PCT-Status-Message>
      </PCT-OutputData_status>
      <PCT-OutputData_output>
        <PCT-OutputData_output_download-url>
          <PCT-Download-URL>
            <PCT-Download-URL_url>
          ftp://ftp-private.ncbi.nlm.nih.gov/pubchem/.fetch/1064385222466625960.sdf.gz
        </PCT-Download-URL_url>
          </PCT-Download-URL>
        </PCT-OutputData_output_download-url>
      </PCT-OutputData_output>
    </PCT-OutputData>
  </PCT-Data_output>
</PCT-Data>

You would parse out the URL from the <PCT-Download-URL_url> tag, and then use a tool of your choice to connect to that URL to retrieve the actual requested data.

</pre>
     */
    public List<String> process(List<String> target) throws ProcessorException {
        try {
            URL url = new URL("http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi");
            URLConnection connection= url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection hc = ((HttpURLConnection)connection);
                hc.setRequestMethod("POST");
                hc.setDoOutput(true);
                Writer w = new OutputStreamWriter(hc.getOutputStream());
                getInput(target, w);
                w.flush ();
                w.close ();
                
                // getting the response is required to force the request, otherwise it might not even be sent at all
                BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));
                String input;
                StringBuffer response = new StringBuffer(256);
                
                while((input = in.readLine()) != null) {
                    response.append(input + "\r");
                }
                System.out.println(response);
                return null;

            }
            return null;
        } catch (Exception x) {
            throw new ProcessorException(x);
        }
    }
    /**
<pre>
<PCT-Data>
  <PCT-Data_input>
    <PCT-InputData>
      <PCT-InputData_download>
        <PCT-Download>
          <PCT-Download_uids>
            <PCT-QueryUids>
              <PCT-QueryUids_ids>
                <PCT-ID-List>
                  <PCT-ID-List_db>pccompound</PCT-ID-List_db>
                  <PCT-ID-List_uids>
                    <PCT-ID-List_uids_E>1</PCT-ID-List_uids_E>
                    <PCT-ID-List_uids_E>99</PCT-ID-List_uids_E>
                  </PCT-ID-List_uids>
                </PCT-ID-List>
              </PCT-QueryUids_ids>
            </PCT-QueryUids>
          </PCT-Download_uids>
          <PCT-Download_format value="sdf"/>
          <PCT-Download_compression value="gzip"/>
        </PCT-Download>
      </PCT-InputData_download>
    </PCT-InputData>
  </PCT-Data_input>
</PCT-Data>
</pre>
     */
    public void getInput(List<String> sids,Writer out) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        factory.setNamespaceAware(true);      
        factory.setValidating(true);        
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler( new SimpleErrorHandler(builder.getClass().getName()) );
        Document doc = builder.newDocument();
        Element top = doc.createElement("PCT-Data");
        Node node = top.appendChild(doc.createElement("PCT-Data_input"));
        node = node.appendChild(doc.createElement("PCT-InputData"));
        node = node.appendChild(doc.createElement("PCT-InputData_download"));
        node = node.appendChild(doc.createElement("PCT-Download"));
        Node dnode = node.appendChild(doc.createElement("PCT-Download_uids"));
        
        dnode = dnode.appendChild(doc.createElement("PCT-QueryUids"));
        dnode = dnode.appendChild(doc.createElement("PCT-QueryUids_ids"));
        dnode = dnode.appendChild(doc.createElement("PCT-ID-List"));
        Node unode = dnode.appendChild(doc.createElement("PCT-ID-List_db"));
        unode.appendChild(doc.createTextNode("pccompound"));
        
        unode = dnode.appendChild(doc.createElement("PCT-ID-List_uids"));
        
        unode = unode.appendChild(doc.createElement("PCT-ID-List_uids_E"));
        for (String sid : sids)
            unode.appendChild(doc.createTextNode(sid));
        
        Element fnode = doc.createElement("PCT-Download_format");
        fnode.setAttribute("value", "sdf");
        node.appendChild(fnode);
        
        Element cnode = doc.createElement("PCT-Download_compression");
        cnode.setAttribute("value", "gzip");
        node.appendChild(cnode);
        
        doc.appendChild(top);
        
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        //xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, dtdSchema);
        //xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "qmrf.dtd");
        xformer.setOutputProperty(OutputKeys.INDENT,"Yes");
        xformer.setOutputProperty(OutputKeys.STANDALONE,"Yes");
        
        //Writer out = new StringWriter();
        xformer.transform(new DOMSource(doc), new StreamResult(out));
        out.flush();
        //return out.toString();
    }


}
