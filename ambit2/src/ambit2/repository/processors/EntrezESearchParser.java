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

package ambit2.repository.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openscience.cdk.io.formats.PubChemFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ambit2.repository.IProcessor;
import ambit2.repository.ProcessorException;
import ambit2.repository.StructureRecord;

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
public class EntrezESearchParser implements IProcessor<InputStream,List<StructureRecord>> {
	protected final static String tag_ENTREZ_esearch="eSearchResult";
	protected final static String tag_ENTREZ_IdList="IdList";
	protected final static String tag_ENTREZ_Id="Id";
	protected final static String tag_ENTREZ_ErrorList="ErrorList";
	protected final static String tag_ENTREZ_PhraseNotFound="PhraseNotFound";
	
	
	public List<StructureRecord> process(InputStream target) throws ProcessorException {
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new InputSource(new InputStreamReader(target)));
	        doc.normalize();
	        return parse(doc);
		} catch (IOException x) {
			throw new ProcessorException(x);
		} catch (SAXException x) {
			throw new ProcessorException(x);
		} catch (ParserConfigurationException x) {
			throw new ProcessorException(x);
		}
		
	}

	protected List<StructureRecord> parse(Document doc) throws ProcessorException {
		List<StructureRecord> results = new ArrayList<StructureRecord>();
		NodeList top = doc.getElementsByTagName(tag_ENTREZ_esearch);
		if ((top==null) || (top.getLength()!=1)) throw new ProcessorException(tag_ENTREZ_esearch+ "not found");
		NodeList idlist = ((Element)top.item(0)).getElementsByTagName(tag_ENTREZ_IdList);
		if ((idlist==null) || (idlist.getLength()!=1)) throw new ProcessorException(tag_ENTREZ_IdList+ "not found");
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


