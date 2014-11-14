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

package ambit2.pubchem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.io.formats.PubChemFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorException;

/**
 * Reads Entrez eSearch query result  (DTD available at http://eutils.ncbi.nlm.nih.gov/entrez/query/DTD/eSearch_020511.dtd )
 * and returns List<StructureRecord>, where {@link StructureRecord#getFormat()} is equal to {@link PubChemFormat}
 * 
 * <pre>
<?xml version="1.0"?>
<!DOCTYPE eSearchResult PUBLIC "-//NLM//DTD eSearchResult, 11 May 2002//EN" "http://www.ncbi.nlm.nih.gov/entrez/query/DTD/eSearch_020511.dtd">
<eSearchResult>
	<Count>1</Count>
	<RetMax>1</RetMax>
	<RetStart>0</RetStart>
	<IdList>
		<Id>7474</Id>
	</IdList>
	<TranslationSet>
	</TranslationSet>
	<TranslationStack>
		<TermSet>
			<Term>100-00-5[All Fields]</Term>
			<Field>All Fields</Field>
			<Count>1</Count>
			<Explode>Y</Explode>
		</TermSet>
		<OP>GROUP</OP>
	</TranslationStack>
	<QueryTranslation>100-00-5[All Fields]</QueryTranslation>
</eSearchResult>
 * </pre>
 * @author nina
 *
 */
public class EntrezESearchParser extends DefaultAmbitProcessor<InputStream,List<IStructureRecord>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1147135155708179050L;
	protected final static String tag_ENTREZ_esearch="eSearchResult";
	protected final static String tag_ENTREZ_IdList="IdList";
	protected final static String tag_ENTREZ_Id="Id";
	protected final static String tag_ENTREZ_ErrorList="ErrorList";
	protected final static String tag_ENTREZ_PhraseNotFound="PhraseNotFound";
	
	
	public List<IStructureRecord> process(InputStream target) throws AmbitException {
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new InputSource(new InputStreamReader(target)));
	        doc.normalize();
	        /*
	        try {
				Transformer xformer = TransformerFactory.newInstance().newTransformer();
		        xformer.setOutputProperty(OutputKeys.INDENT,"Yes");
		        xformer.setOutputProperty(OutputKeys.STANDALONE,"Yes");
		        Result result = new StreamResult(System.err);
		        xformer.transform(new DOMSource(doc), result);
	        } catch (Exception x) {
	        	
	        }
			*/
	        
	        return parse(doc);
		} catch (IOException x) {
			throw new ProcessorException(this,x);
		} catch (SAXException x) {
			throw new ProcessorException(this,x);
		} catch (ParserConfigurationException x) {
			throw new ProcessorException(this,x);
		}
		
	}

	protected List<IStructureRecord> parse(Document doc) throws ProcessorException {
		List<IStructureRecord> results = new ArrayList<IStructureRecord>();
		NodeList top = doc.getElementsByTagName(tag_ENTREZ_esearch);
		if ((top==null) || (top.getLength()!=1)) throw new ProcessorException(this,tag_ENTREZ_esearch+ "not found");
		NodeList idlist = ((Element)top.item(0)).getElementsByTagName(tag_ENTREZ_IdList);
		if ((idlist==null) || (idlist.getLength()!=1)) throw new ProcessorException(this,tag_ENTREZ_IdList+ "not found");
		NodeList id = ((Element)idlist.item(0)).getElementsByTagName(tag_ENTREZ_Id);
		if ((id!=null) && (id.getLength()>0)) 
			for (int i=0; i < id.getLength(); i++)
				if (id.item(i) instanceof Element) {
					results.add(new StructureRecord(-1,-1,
							((Element)id.item(i)).getTextContent(),
							PUGProcessor.PUBCHEM_CID)
					);
				}
		NodeList errors = ((Element)top.item(0)).getElementsByTagName(tag_ENTREZ_ErrorList);
		if (errors!=null) 
			for (int i=0; i < errors.getLength(); i++) 
				if (errors.item(i) instanceof Element) {
					NodeList error = ((Element)errors.item(i)).getElementsByTagName(tag_ENTREZ_PhraseNotFound);
					if (error != null)
						for (int j=0; j < error.getLength(); j++) 
							if (errors.item(j) instanceof Element) {							
								results.add(new StructureRecord(-1,-1,
										((Element)error.item(j)).getTextContent(),
										tag_ENTREZ_PhraseNotFound)
								);
							}
				}
		return results;
	}
}


/*
 * 0
143-28-2.xml
 * ambit2.repository.ProcessorException: java.net.ConnectException: Connection timed out: connect
	at ambit2.repository.processors.EntrezESearchParser.process(EntrezESearchParser.java:95)
	at ambit2.test.repository.CAS2SDFbyPubchemTtest.process(CAS2SDFbyPubchemTtest.java:113)
	at ambit2.test.repository.CAS2SDFbyPubchemTtest.test_einecs_noformula(CAS2SDFbyPubchemTtest.java:72)
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
	at ambit2.repository.processors.EntrezESearchParser.process(EntrezESearchParser.java:91)
	... 21 more


 */
