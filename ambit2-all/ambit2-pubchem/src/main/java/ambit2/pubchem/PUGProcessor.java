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

package ambit2.pubchem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.exception.CDKException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.SimpleErrorHandler;
import ambit2.base.processors.ProcessorException;
import ambit2.core.io.RawIteratingSDFReader;

/**
 * 
 * Queries Pubchem Power User Gateway. 
 * All communication to PUG is via XML sent to the CGI at the URL:
 * http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 20, 2008
 */
public class PUGProcessor extends DefaultAmbitProcessor<List<IStructureRecord>,List<IStructureRecord>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4410271093960202855L;
	public final static String PUBCHEM_CID = "CID";
	protected static String pugURL = "http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi?tool="+PUGProcessor.class.getName();
	protected QuerySupport<List<IStructureRecord>,List<IStructureRecord>> support = new QuerySupport<List<IStructureRecord>,List<IStructureRecord>>();
	protected final static String PCT_attribute_value="value";
	protected final static String[] PCT_status = {"unknown","success","server-error","hit-limit","time-limit","input-error","data-error","stopped","running","queued"};
	
	protected final static int PCT_download_compression_none=0;
	protected final static int PCT_download_compression_gzip=1;
	protected final static int PCT_download_compression_bzip2=2;	
	protected final static String[] PCT_download_compression={"none","gzip","bzip2"};

	protected final static int PCT_download_format_text_asn=0;
	protected final static int PCT_download_format_binary_asn=1;
	protected final static int PCT_download_format_xml=2;
	protected final static int PCT_download_format_sdf=3;
	protected final static String[] PCT_download_format={"text-asn","binary-asn","xml","sdf"};
	
	protected final static String tag_PCT_Data="PCT-Data";
	protected final static String tag_PCT_Data_input="PCT-Data_input";
	protected final static String tag_PCT_Data_output="PCT-Data_output";
	
	protected final static String tag_PCT_InputData="PCT-InputData";
	protected final static String tag_PCT_OutputData="PCT-OutputData";	
	
	protected final static String tag_PCT_OutputData_status="PCT-OutputData_status";
	protected final static String tag_PCT_OutputData_output="PCT-OutputData_output";
	protected final static String tag_PCT_OutputData_output_download_url="PCT-OutputData_output_download-url";
	protected final static String tag_PCT_Download_URL="PCT-Download-URL";
	protected final static String tag_PCT_Download_URL_url="PCT-Download-URL_url";
	
	protected final static String tag_PCT_OutputData_output_waiting="PCT-OutputData_output_waiting";
	protected final static String tag_PCT_Waiting="PCT-Waiting";
	protected final static String tag_PCT_Waiting_reqid="PCT-Waiting_reqid";
	
	protected final static String tag_PCT_InputData_download="PCT-InputData_download";
	protected final static String tag_PCT_Download="PCT-Download";
	protected final static String tag_PCT_Download_uids="PCT-Download_uids";
	protected final static String tag_PCT_QueryUids="PCT-QueryUids";
	protected final static String tag_PCT_QueryUids_ids="PCT-QueryUids_ids";
	protected final static String tag_PCT_ID_List="PCT-ID-List";
	
	protected final static String tag_PCT_ID_List_uids="PCT-ID-List_uids";
	protected final static String tag_PCT_ID_List_uids_E="PCT-ID-List_uids_E";
	protected final static String tag_PCT_ID_List_db="PCT-ID-List_db";
	protected final static String tag_PCT_Download_format="PCT-Download_format";
	protected final static String tag_PCT_Download_compression="PCT-Download_compression";
	protected final static String tag_PCT_download_3D ="PCT-Download_use-3d";	
	
	protected boolean retrieveConformers = false;
    public boolean isRetrieveConformers() {
		return retrieveConformers;
	}
	public void setRetrieveConformers(boolean retrieveConformers) {
		this.retrieveConformers = retrieveConformers;
	}
	/**
<pre>
Example:   You want to download CID 1 and CID 99  being uids 1 and 99 in the 'pccompound' Entrez database  in SDF format with gzip compression.

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


You would then parse out this request id, being '402936103567975582', in this case, and use this id to 'poll' PUG on the status of the request, composing an XML message like:

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

Note that here the request type 'status' is used; there is also the request type 'cancel' that you may use to cancel a running job. 

If the request is still running, you well get back another waiting message as above, and then you would poll again after some reasonable interval. If the request is finished, you will get a final result message like:

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

    public List<IStructureRecord> process(List<IStructureRecord> target) throws AmbitException {

    	final ResultListener<List<IStructureRecord>> listener = new ResultListener<List<IStructureRecord>>() {
    		public void exception(Exception e) {
    			e.printStackTrace();
    			
    		}
    		public void result(List<IStructureRecord> data) {

    		}
    	};
    	HTTPRequest<List<IStructureRecord>, List<IStructureRecord>> downloadRequest = createDownloadHTTPRequest(target);    	
    	Future<List<IStructureRecord>> fresult = support.lookup(target, downloadRequest, listener);
    	
    	try {
    		List<IStructureRecord> data = fresult.get();    
    		if (data == null) return null;
    		
    		boolean wait = true;
        	HTTPRequest<List<IStructureRecord>, List<IStructureRecord>> pollRequest = null;
    		int trycount = 0;
    		while (wait) {
    			trycount++;
    			if (trycount>32)
    				throw new ProcessorException(this,String.format("Retrial limit %d exceeded",trycount));
    			long now = System.currentTimeMillis();
	    		for (IStructureRecord datum : data) {
	    			wait = tag_PCT_Waiting_reqid.equals(datum.getFormat());
	    			break;
	    		}
	    		if (wait) {
	    			if (trycount>1) Thread.sleep(10000);
	    			if (pollRequest == null) pollRequest = createPollRequest(data);
	    			Future<List<IStructureRecord>> fpollresult = support.lookup(data, pollRequest, listener);
	    			//System.out.println("Poll");
	    			data = fpollresult.get();
	    			if (data==null)
	    				throw new ProcessorException(this,"Poll request returns no data!");
	    		}
	    		now = System.currentTimeMillis()-now;
	    		
	    		//System.out.println(now);
    		}
    		return data;
    	} catch (ProcessorException x) {
    		throw x;
    	} catch (Exception x) {
    		x.printStackTrace();
    		throw new ProcessorException(this,x);
    	}
    	/*
                // getting the response is required to force the request, otherwise it might not even be sent at all
                BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));
                String input;
                StringBuffer response = new StringBuffer(256);
                
                while((input = in.readLine()) != null) {
                    response.append(input + "\r");
                }
                System.out.println(response);


            }
            return null;
        } catch (Exception x) {
            throw new ProcessorException(x);
        }
        */
    }
	protected HTTPRequest<List<IStructureRecord>, List<IStructureRecord>> createDownloadHTTPRequest(List<IStructureRecord> record) {
    	HTTPRequest<List<IStructureRecord>, List<IStructureRecord>> request = new HTTPRequest<List<IStructureRecord>, List<IStructureRecord>>() {
    		@Override
    		protected List<IStructureRecord> parseInput(List<IStructureRecord> target, InputStream in) throws ProcessorException {
    			try {
	    	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	        DocumentBuilder builder = factory.newDocumentBuilder();
	    	        Document doc = builder.parse(new InputSource(new InputStreamReader(in)));
	    	        doc.normalize();
	    	        return parseOutput(doc);
    			} catch (IOException x) {
    				throw new ProcessorException(this,x);
    			} catch (SAXException x) {
    				throw new ProcessorException(this,x);
    			} catch (ParserConfigurationException x) {
    				throw new ProcessorException(this,x);
    			}
    		}
    		@Override
    		protected void prepareOutput(List<IStructureRecord> target, OutputStream out) throws ProcessorException {
    			try {
    	            Writer w = new OutputStreamWriter(out);
    	            createDownloadRequest(target, w);
    	            w.flush ();
    	            w.close ();
    			} catch (ParserConfigurationException x) {
    				throw new ProcessorException(this,x);
    			} catch (TransformerException x) {
    				throw new ProcessorException(this,x);
    			} catch (IOException x) {
    				throw new ProcessorException(this,x);
    			}
    		}
    	};	
    	request.setUrl(pugURL);		
    	return request;
	}
	
	protected HTTPRequest<List<IStructureRecord>, List<IStructureRecord>> createPollRequest(List<IStructureRecord> waitingregids) {
    	HTTPRequest<List<IStructureRecord>, List<IStructureRecord>>  request = new HTTPRequest<List<IStructureRecord>, List<IStructureRecord>>() {
    		@Override
    		protected List<IStructureRecord> parseInput(List<IStructureRecord> target, InputStream in) throws ProcessorException {
    			try {
	    	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	        DocumentBuilder builder = factory.newDocumentBuilder();
	    	        Document doc = builder.parse(new InputSource(new InputStreamReader(in)));
	    	        doc.normalize();
	    	        return parseOutput(doc);
    			} catch (IOException x) {
    				throw new ProcessorException(this,x);
    			} catch (SAXException x) {
    				throw new ProcessorException(this,x);
    			} catch (ParserConfigurationException x) {
    				throw new ProcessorException(this,x);
    			}
    		}
    		@Override
    		protected void prepareOutput(List<IStructureRecord> target, OutputStream out) throws ProcessorException {
    			try {
    	            Writer w = new OutputStreamWriter(out);
    	            createPollRequest(target.get(0), w);
    	            w.flush ();
    	            w.close ();
    			} catch (IOException x) {
    				throw new ProcessorException(this,x);
    			}
    		}
    	};	
    	request.setUrl(pugURL);		
    	return request;
	}
    public void createDownloadRequest(List<IStructureRecord> sids,Writer out) throws 
    						ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        factory.setNamespaceAware(true);      
        factory.setValidating(true);        
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler( new SimpleErrorHandler(builder.getClass().getName()) );
        Document doc = builder.newDocument();
        Element top = doc.createElement(tag_PCT_Data);
        Node node = top.appendChild(doc.createElement(tag_PCT_Data_input));
        node = node.appendChild(doc.createElement(tag_PCT_InputData));
        node = node.appendChild(doc.createElement(tag_PCT_InputData_download));
        node = node.appendChild(doc.createElement(tag_PCT_Download));
        Node dnode = node.appendChild(doc.createElement(tag_PCT_Download_uids));
        
        dnode = dnode.appendChild(doc.createElement(tag_PCT_QueryUids));
        dnode = dnode.appendChild(doc.createElement(tag_PCT_QueryUids_ids));
        dnode = dnode.appendChild(doc.createElement(tag_PCT_ID_List));
        Node unode = dnode.appendChild(doc.createElement(tag_PCT_ID_List_db));
        unode.appendChild(doc.createTextNode("pccompound"));
        
        unode = dnode.appendChild(doc.createElement(tag_PCT_ID_List_uids));
        
    	
    	
        for (IStructureRecord sid : sids) {
        	Node idnode = unode.appendChild(doc.createElement(tag_PCT_ID_List_uids_E));	
        	if (PUBCHEM_CID.equals(sid.getFormat()))
        		idnode.appendChild(doc.createTextNode(sid.getContent()));
        }    
        
        Element fnode = doc.createElement(tag_PCT_Download_format);
        fnode.setAttribute(PCT_attribute_value, PCT_download_format[PCT_download_format_sdf]);
        node.appendChild(fnode);
        
        Element cnode = doc.createElement(tag_PCT_Download_compression);
        cnode.setAttribute(PCT_attribute_value, PCT_download_compression[PCT_download_compression_gzip]);
        node.appendChild(cnode);

        if (isRetrieveConformers()) {
	        Element tnode = doc.createElement(tag_PCT_download_3D);
	        tnode.setAttribute(PCT_attribute_value, "true");
	        node.appendChild(tnode);
        }
        
        doc.appendChild(top);
        
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        //xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, dtdSchema);
        //xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "qmrf.dtd");
        xformer.setOutputProperty(OutputKeys.INDENT,"yes");
        xformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        
        //Writer out = new StringWriter();
        xformer.transform(new DOMSource(doc), new StreamResult(out));
        out.flush();
        //return out.toString();
    }
    public static void createPollRequest(IStructureRecord regid, Writer out) throws ProcessorException {
    	if (!tag_PCT_Waiting_reqid.equals(regid.getFormat()))
    		throw new ProcessorException(null,tag_PCT_Waiting_reqid + " expected instead of "+regid.getFormat());
    	try {
	    	out.write("<?xml version=\"1.0\"?>");
	    	out.write("<PCT-Data>");
	    	out.write("<PCT-Data_input>");
	    	out.write("<PCT-InputData>");
	    	out.write("<PCT-InputData_request>");
	    	out.write("<PCT-Request>");
	    	out.write("<PCT-Request_reqid>");
	    	out.write(regid.getContent());
	    	out.write("</PCT-Request_reqid>");
	    	out.write("<PCT-Request_type value=\"status\"/>");
	    	out.write("</PCT-Request>");
	    	out.write("</PCT-InputData_request>");
	    	out.write("</PCT-InputData>");
	    	out.write("</PCT-Data_input>");
	    	out.write("</PCT-Data>");
    	} catch (IOException x) {
    		throw new ProcessorException(null,x);
    	}
    }
		/**
		<!ELEMENT PCT-OutputData_output (
		        PCT-OutputData_output_waiting | 
		        PCT-OutputData_output_download-url | 
		        PCT-OutputData_output_ids | 
		        PCT-OutputData_output_entrez)>

		 * @param doc
		 */
	public static List<IStructureRecord> parseOutput(Document doc)  throws ProcessorException  {
		
		
		Element top = getNodes(doc, null, tag_PCT_Data);
		Element next = getNodes(doc, top, tag_PCT_Data_output);
		Element outputData = getNodes(doc, next, tag_PCT_OutputData);
		Element status = getNodes(doc, outputData, tag_PCT_OutputData_status);
		//TODO process status
		NodeList nodes = outputData.getElementsByTagName(tag_PCT_OutputData_output);
		if ((nodes != null) && (nodes.getLength()==1)) { 
			//Element output = getNodes(doc, outputData, tag_PCT_OutputData_output);
			nodes = ((Element)nodes.item(0)).getChildNodes();
			for (int i=0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				
				String tag = node.getNodeName();
				if (tag_PCT_OutputData_output_download_url.equals(tag))
					return processDownloadURL(doc, (Element) node);
				if (tag_PCT_OutputData_output_waiting.equals(tag))
					return processWaiting(doc, (Element) node);
			}
		}
		
		return null;

	}
	protected static List<IStructureRecord> processWaiting(Document doc, Element waiting) throws ProcessorException {
		List<IStructureRecord> result = new ArrayList<IStructureRecord>();
		Element waitingNode = getNodes(doc, waiting, tag_PCT_Waiting);
		NodeList regid = waitingNode.getElementsByTagName(tag_PCT_Waiting_reqid);
		if ((regid == null) || (regid.getLength() == 0)) 
			throw new ProcessorException(null,"Error - expected element "+tag_PCT_Waiting_reqid);
		for (int i=0; i < regid.getLength();i++)
			result.add(new StructureRecord(-1,-1,((Element)regid.item(i)).getTextContent(),tag_PCT_Waiting_reqid));
		return result;		
	}	
	protected static List<IStructureRecord> processDownloadURL(Document doc, Element download) throws ProcessorException {
		List<IStructureRecord> result = new ArrayList<IStructureRecord>();
		Element urlNode = getNodes(doc, download, tag_PCT_Download_URL);
		Element url = getNodes(doc, urlNode, tag_PCT_Download_URL_url);
		
		try {
	        URL theUrl = new URL(url.getTextContent());
	        URLConnection connection= theUrl.openConnection();
	        RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
	        while (reader.hasNext()) {
	        	Object o = reader.next();
	        	if (o instanceof IStructureRecord) {
	        		result.add((IStructureRecord)o);
	        	} else
	        		result.add(new StructureRecord(-1,-1,o.toString(),PCT_download_format[PCT_download_format_sdf]));
	        }
	        /*
	        StringBuilder b = new StringBuilder();
	        while((input = in.readLine()) != null) {
	            b.append(input);
	            b.append("\r");  
	        }
	        result.add(new StructureRecord(-1,-1,b.toString(),PCT_download_format[PCT_download_format_sdf]));
	        */
		} catch (CDKException x) {
			throw new ProcessorException(null,x);
		} catch (IOException x) {
			throw new ProcessorException(null,x);
		}
		return result;		
	}
	protected static Element getNodes(Document doc, Element parent, String tag) throws ProcessorException {
		NodeList nodes = null;
		if (parent == null)
			nodes = doc.getElementsByTagName(tag);
		else
			nodes = parent.getElementsByTagName(tag);
		if ((nodes == null) || (nodes.getLength() != 1)) 
			throw new ProcessorException(null,"Error - expected element "+tag);
		return (Element) nodes.item(0);
	}
	
}

/**
ambit2.repository.ProcessorException: java.lang.NullPointerException
at ambit2.repository.processors.PUGProcessor.process(PUGProcessor.java:268)
at ambit2.test.repository.CAS2SDFbyPubchemTtest.process(CAS2SDFbyPubchemTtest.java:106)
at ambit2.test.repository.CAS2SDFbyPubchemTtest.test_einecs_noformula(CAS2SDFbyPubchemTtest.java:65)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
at java.lang.reflect.Method.invoke(Method.java:585)
at junit.framework.TestCase.runTest(TestCase.java:154)
at junit.framework.TestCase.runBare(TestCase.java:127)
at junit.framework.TestResult$1.protect(TestResult.java:106)
at junit.framework.TestResult.runProtected(TestResult.java:124)
at junit.framework.TestResult.run(TestResult.java:109)
at junit.framework.TestCase.run(TestCase.java:118)
at junit.framework.TestSuite.runTest(TestSuite.java:208)
at junit.framework.TestSuite.run(TestSuite.java:203)
at org.junit.internal.runners.OldTestClassRunner.run(OldTestClassRunner.java:35)
at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:38)
at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
Caused by: java.lang.NullPointerException
at ambit2.repository.processors.PUGProcessor.process(PUGProcessor.java:252)
... 21 more


12025-19-3.xml
ambit2.repository.ProcessorException: ambit2.repository.ProcessorException: java.net.ConnectException: Connection timed out: connect
at ambit2.repository.processors.HTTPRequest.process(HTTPRequest.java:61)
at ambit2.repository.processors.QuerySupport$1.call(QuerySupport.java:53)
at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:269)
at java.util.concurrent.FutureTask.run(FutureTask.java:123)
at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:650)
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:675)
at java.lang.Thread.run(Thread.java:595)
Caused by: ambit2.repository.ProcessorException: java.net.ConnectException: Connection timed out: connect
at ambit2.repository.processors.PUGProcessor$2.parseInput(PUGProcessor.java:300)
at ambit2.repository.processors.PUGProcessor$2.parseInput(PUGProcessor.java:1)
at ambit2.repository.processors.HTTPRequest.process(HTTPRequest.java:56)
... 6 more
Caused by: java.net.ConnectException: Connection timed out: connect
at java.net.PlainSocketImpl.socketConnect(Native Method)
at java.net.PlainSocketImpl.doConnect(PlainSocketImpl.java:333)
at java.net.PlainSocketImpl.connectToAddress(PlainSocketImpl.java:195)
at java.net.PlainSocketImpl.connect(PlainSocketImpl.java:182)
at java.net.Socket.connect(Socket.java:520)
at java.net.Socket.connect(Socket.java:470)
at sun.net.NetworkClient.doConnect(NetworkClient.java:157)
at sun.net.www.http.HttpClient.openServer(HttpClient.java:388)
at sun.net.www.http.HttpClient.openServer(HttpClient.java:523)
at sun.net.www.http.HttpClient.<init>(HttpClient.java:231)
at sun.net.www.http.HttpClient.New(HttpClient.java:304)
at sun.net.www.http.HttpClient.New(HttpClient.java:321)
at sun.net.www.protocol.http.HttpURLConnection.getNewHttpClient(HttpURLConnection.java:813)
at sun.net.www.protocol.http.HttpURLConnection.plainConnect(HttpURLConnection.java:765)
at sun.net.www.protocol.http.HttpURLConnection.connect(HttpURLConnection.java:690)
at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:934)
at com.sun.org.apache.xerces.internal.impl.XMLEntityManager.setupCurrentEntity(XMLEntityManager.java:973)
at com.sun.org.apache.xerces.internal.impl.XMLEntityManager.startEntity(XMLEntityManager.java:905)
at com.sun.org.apache.xerces.internal.impl.XMLEntityManager.startDTDEntity(XMLEntityManager.java:872)
at com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl.setInputSource(XMLDTDScannerImpl.java:282)
at com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl$DTDDispatcher.dispatch(XMLDocumentScannerImpl.java:1021)
at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.scanDocument(XMLDocumentFragmentScannerImpl.java:368)
at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:834)
at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:764)
at com.sun.org.apache.xerces.internal.parsers.XMLParser.parse(XMLParser.java:148)
at com.sun.org.apache.xerces.internal.parsers.DOMParser.parse(DOMParser.java:250)
at com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderImpl.parse(DocumentBuilderImpl.java:292)
at ambit2.repository.processors.PUGProcessor$2.parseInput(PUGProcessor.java:296)
... 8 more
java.lang.NullPointerException
at ambit2.repository.processors.PUGProcessor.process(PUGProcessor.java:252)
at ambit2.test.repository.CAS2SDFbyPubchemTtest.process(CAS2SDFbyPubchemTtest.java:106)
at ambit2.test.repository.CAS2SDFbyPubchemTtest.test_einecs_noformula(CAS2SDFbyPubchemTtest.java:65)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
at java.lang.reflect.Method.invoke(Method.java:585)
at junit.framework.TestCase.runTest(TestCase.java:154)
at junit.framework.TestCase.runBare(TestCase.java:127)
at junit.framework.TestResult$1.protect(TestResult.java:106)
at junit.framework.TestResult.runProtected(TestResult.java:124)
at junit.framework.TestResult.run(TestResult.java:109)
at junit.framework.TestCase.run(TestCase.java:118)
at junit.framework.TestSuite.runTest(TestSuite.java:208)
at junit.framework.TestSuite.run(TestSuite.java:203)
at org.junit.internal.runners.OldTestClassRunner.run(OldTestClassRunner.java:35)
at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:38)
at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386)
at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
*/