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
import java.sql.Connection;
import java.util.ArrayList;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.descriptors.DescriptorsHashtable;
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
import ambit.processors.AromaticityProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.SmilesGeneratorProcessor;

public class DBImportBatch extends DefaultBatchProcessing {
	public DBImportBatch(IIteratingChemObjectReader reader, SourceDataset dataset, DbConnection dbConnection) throws BatchProcessingException {
		super(reader,
  			 getWriter(dbConnection,dataset),
				getProcessor(dbConnection.getConn()),
				new EmptyBatchConfig()
  			 );
						
	}
	protected static IChemObjectWriter getWriter(DbConnection dbConnection,  SourceDataset dataset) {
		return  new DbSubstanceWriter(dbConnection,
				dataset,
				getAliases(),
				new DescriptorsHashtable());
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
	protected static IAmbitProcessor getProcessor(Connection connection) {
		ProcessorsChain processors = new ProcessorsChain();
		IdentifiersProcessor identifiersProcessor = new IdentifiersProcessor();
		processors.add(identifiersProcessor);
		
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
	public static void import2DB(File file,SourceDataset dataset,String host,String port, String database,String user,String password) throws Exception  {
		IIteratingChemObjectReader reader = FileInputState.getReader(new FileInputStream(file), file.getName());
		
		DbConnection dbConnection = new DbConnection(host,port,database,user,password);
		dbConnection.open(false);
		DBImportBatch batch = new DBImportBatch(reader,
				dataset,
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


