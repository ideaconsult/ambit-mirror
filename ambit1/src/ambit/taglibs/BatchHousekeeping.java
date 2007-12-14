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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.processors.ReadSubstanceProcessor;
import ambit.database.readers.DbStructureReader;
import ambit.database.writers.AtomEnvironmentWriter;
import ambit.database.writers.DbStructureWriter;
import ambit.database.writers.FingerprintWriter;
import ambit.database.writers.SmilesWriter;
import ambit.exceptions.AmbitException;
import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.processors.AromaticityProcessor;
import ambit.processors.Builder3DProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.AtomEnvironmentGenerator;
import ambit.processors.structure.FingerprintGenerator;
import ambit.processors.structure.SmilesGeneratorProcessor;
import ambit.ui.actions.dbadmin.DBGenerator;

public class BatchHousekeeping extends DefaultBatchProcessing {
	public BatchHousekeeping(int selectedAction, SourceDataset dataset, DbConnection dbConnection) throws BatchProcessingException {
		super(getReader(dbConnection,dataset),
  			 getWriter(selectedAction,dbConnection,dataset),
				getProcessor(selectedAction,dbConnection),
				new EmptyBatchConfig()
  			 );
						
	}
	public static IAmbitProcessor getProcessor(int selectedAction,DbConnection dbConnection) throws BatchProcessingException {
		try {
				ProcessorsChain processors = new ProcessorsChain();
				processors.addProcessor(new ReadSubstanceProcessor(dbConnection.getConn()));
				switch (selectedAction) {
				case 0: {processors.add(new SmilesGeneratorProcessor()); break;}
				case 1: {processors.add(new FingerprintGenerator());break;}
				case 2: {processors.add(new AtomEnvironmentGenerator());break;}
				case 3: {processors.add(new AromaticityProcessor());break;}
				case 4: {processors.add(new Builder3DProcessor());break;}
				}
				return processors;
		} catch (AmbitException x) {
			throw new BatchProcessingException(x);
		}
	}	
	protected static IChemObjectWriter getWriter(int selectedAction,DbConnection dbConnection,  SourceDataset dataset) {
			switch (selectedAction) {
				case 0: return new SmilesWriter(dbConnection.getConn(),dbConnection.getUser());
				case 1: return new FingerprintWriter(dbConnection.getConn(),dbConnection.getUser());
				case 2: return new AtomEnvironmentWriter(dbConnection.getConn(),dbConnection.getUser());
				case 3: return new DbStructureWriter(dbConnection.getConn(),dbConnection.getUser());
				case 4: return new DbStructureWriter(dbConnection.getConn(),dbConnection.getUser());
			}
			return null;		
	}
	protected static IIteratingChemObjectReader getReader(DbConnection dbConnection,  final SourceDataset dataset) throws BatchProcessingException {
		try {
			
		return new DbStructureReader(dbConnection.getConn(),DBGenerator.SQL_DATASET) {
			public String toString() {
				return "Reads structures from "+dataset.getName();
			}
			@Override
			public void setParameters(PreparedStatement ps) throws SQLException {
				ps.setInt(1,dataset.getId());
			}							
		};
		} catch (AmbitException x) {
			throw new BatchProcessingException(x);
		}
	}		
}



