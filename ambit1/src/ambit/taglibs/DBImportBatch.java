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

package ambit.taglibs;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.core.DbSrcDataset;
import ambit.database.exception.DbAmbitException;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.processors.FindUniqueProcessor;
import ambit.database.writers.DbSubstanceWriter;
import ambit.io.FileInputState;
import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.AromaticityProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.SmilesGeneratorProcessor;

public class DBImportBatch extends DefaultBatchProcessing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9029262569765535978L;
	public DBImportBatch(IIteratingChemObjectReader reader, SourceDataset dataset, 
			 ArrayList<String> aliases, DescriptorsHashtable descriptorslookup, IdentifiersProcessor identifiers,
			DbConnection dbConnection) throws BatchProcessingException {
		super(reader,
  			 getWriter(dbConnection,dataset,aliases,descriptorslookup),
				getProcessor(dbConnection.getConn(),identifiers),
				new EmptyBatchConfig()
  			 );
	}	
	public DBImportBatch(IIteratingChemObjectReader reader, SourceDataset dataset, DbConnection dbConnection) throws BatchProcessingException {
		this(reader,dataset,getAliases(),new DescriptorsHashtable(),null,dbConnection);
	}
	protected static IChemObjectWriter getWriter(DbConnection dbConnection,  SourceDataset dataset, ArrayList<String> aliases, DescriptorsHashtable descriptorslookup) {
		return  new DbSubstanceWriter(dbConnection,
				dataset,
				aliases,
				descriptorslookup);
	}
	protected static ArrayList<String> getAliases() {
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("ChemName_IUPAC");
		aliases.add("INChI");
		aliases.add("NSC");
		aliases.add("ID");
		aliases.add("Code");
		aliases.add("KEGG");
		return aliases;
	}
	protected static IAmbitProcessor getProcessor(Connection connection, IdentifiersProcessor identifiers) {
		ProcessorsChain processors = new ProcessorsChain();
		if (identifiers == null) identifiers = new IdentifiersProcessor();
		processors.add(identifiers);
		
		    try {
		        IAmbitProcessor p = new CASSmilesLookup(connection,false);
		        p.setEnabled(false);
		        processors.add(p);
		        processors.add(new AromaticityProcessor());
		        SmilesGeneratorProcessor smigen = new SmilesGeneratorProcessor(5*60*1000);
				processors.add(smigen);
		    	processors.add(new FindUniqueProcessor(connection));
		    } catch (Exception x) {
		    	
		    }
 
		return processors;
	}
	public static void import2DB_xml(String xml,String host,String port, String database,String user,String password) throws Exception  {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		Element e = doc.getDocumentElement();
		String url = e.getAttribute("url");
		String datasetName = e.getAttribute("dataset");
		NodeList n = e.getChildNodes();
		
		SourceDataset dataset = new SourceDataset(datasetName,
				ReferenceFactory.createDatasetReference(url,datasetName));
		/*
		 * 	"Structural Formula,Dependent Variable Value,,Experimental data,other"/>

		 */
		ArrayList<String> aliases = new ArrayList<String>();
		DescriptorsHashtable descriptorslookup = new DescriptorsHashtable();
		IdentifiersProcessor identifiers = new IdentifiersProcessor();
		for (int i=0; i < n.getLength(); i++) {
			Node node = n.item(i);
			if (node instanceof Element) {
				String type = ((Element) node).getAttribute("fieldtype");
				String fieldname = ((Element) node).getAttribute("fieldname");
				String newname = ((Element) node).getAttribute("newname"); 
				if ("ignore".equals(type)) continue;
				if ("Chemical Name (IUPAC)".equals(type)) 
					identifiers.getIdentifiers().put(fieldname,CDKConstants.NAMES);
				else
				if ("Chemical Name (Not IUPAC)".equals(type)) 
					aliases.add(fieldname);
				else 
				if ("CAS Number".equals(type)) 
					identifiers.getIdentifiers().put(fieldname,CDKConstants.CASRN);	
				else
				if ("SMILES".equals(type)) 
					identifiers.getIdentifiers().put(fieldname,AmbitCONSTANTS.SMILES);	
				else
				if ("InChI".equals(type)) 
					aliases.add(fieldname);					
				else
				if ("Descriptor".equals(type)) {
					Descriptor d = new Descriptor(newname,ReferenceFactory.createEmptyReference());					
					descriptorslookup.addDescriptorPair(fieldname,d);
				}
			}
		}
		doc = null;
		import2DB(new File(url), dataset,aliases, descriptorslookup, identifiers,
				host, port, database, user, password);
/*
 * 	public DBImportBatch(IIteratingChemObjectReader reader, SourceDataset dataset, 
			 ArrayList<String> aliases, DescriptorsHashtable descriptorslookup,
			DbConnection dbConnection) throws BatchProcessingException {
 */		
	}	
	public static void import2DB(File file,SourceDataset dataset,
			String host,String port, String database,String user,String password) throws Exception  {
		import2DB(file, dataset, getAliases(), new DescriptorsHashtable(), null, host, port, database, user, password);
	}
	public static void import2DB(File file,SourceDataset dataset,
			ArrayList<String> aliases, DescriptorsHashtable descriptorslookup, IdentifiersProcessor identifiers,
			String host,String port, String database,String user,String password) throws Exception  {
		IIteratingChemObjectReader reader = FileInputState.getReader(new FileInputStream(file), file.getName());
		
		DbConnection dbConnection = new DbConnection(host,port,database,user,password);
		dbConnection.open(false);
		DBImportBatch batch = new DBImportBatch(reader,
				dataset,				
				aliases,
				descriptorslookup,
				identifiers,
				dbConnection);
		
		batch.start();
		reader.close();
		
		housekeeping(0,dataset,dbConnection);
		housekeeping(1,dataset,dbConnection);
		dbConnection.close();
	}

	public static void generateFingerprints(SourceDataset dataset,int selectedAction, DbConnection dbConnection) throws Exception  {

		housekeeping(1,dataset,dbConnection);
	}	
	public static void generateSMILES(SourceDataset dataset,int selectedAction, DbConnection dbConnection) throws Exception  {

		housekeeping(0,dataset,dbConnection);
		
	}	
	public static void housekeeping(int selectedAction,SourceDataset dataset, DbConnection dbConnection) throws Exception  {
		
		DbSrcDataset d = new DbSrcDataset(dbConnection);
		int i = d.getSourceDatasetId(dataset);
		if (i < 0) throw new DbAmbitException(null, dataset + " dataset not found!");
			
		dataset.setId(i);
		
		BatchHousekeeping batch = new BatchHousekeeping(selectedAction,
				dataset,
				dbConnection);
		
		batch.start();

	}	
}


