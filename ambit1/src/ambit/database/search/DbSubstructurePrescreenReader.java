/* DbSubstructurePrescreenReader.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-22 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.database.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SourceDataset;
import ambit.exceptions.AmbitException;

/**
 * Performs substructure search prescreening of the database.
 * The prescreening is based on 1024 bit fingerprints ({@link org.openscience.cdk.fingerprint.Fingerprinter})
 * The substructure is given by {@link IMolecule}<br>
 * For actual subsearch pass the structures through <br>
 * {@link ambit.database.processors.ReadSubstanceProcessor} and <br>
 * {@link ambit.database.processors.SubstructureSearchProcessor} as in <br>
 * {@link ambit.ui.actions.search.DbSubstructureSearchAction}<br>
 * <b>Example</b>: substructure search for benzene. Search the database and writes the results into SDF file.<br>
 * <pre>
	public void testSubstructureSearch() {
		try {
			
			DbConnection dbConnection = new DbConnection("localhost","33060","ambit","myuser","mypasword");
			//Open connection to database
			dbConnection.open();
			
			Connection conn = dbConnection.getConn();
			IMolecule benzene = MoleculeFactory.makeBenzene();
			//The reader will extract only structures which probably contain benzene. The prescreening is based on fingerprints.
			DbSubstructurePrescreenReader reader = new DbSubstructurePrescreenReader(
					conn,
					benzene,
					null,
					0,100
					);
					
			//We need more than one processor		
			ProcessorsChain processors = new ProcessorsChain();
			//Note the reader doesn't read the structures, so we need a processor to read them.
			processors.add(new ReadSubstanceProcessor(conn));
			//This is where actual substructure search take place
			processors.add(new SubstructureSearchProcessor(benzene));

			//A writer to write the results in SDF file.
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("benzene_substructure_search_results.sdf"));
						
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);

			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw < rr);
			assertTrue(rw >0);

			dbConnection.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
</pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-22
 */
public class DbSubstructurePrescreenReader extends DbSearchReader {
    protected Fingerprinter fingerprinter;
    /**
     * 
     * @param connection  {@link Connection} database connection
     * @param mol {@link IMolecule} molecule
     * @param srcDataset {@link SourceDataset} dataset. Null if to search entire database
     * @param page - the page number to return 
     * @param pagesize - the page size (numer of records per page)
     * @throws AmbitException
     */
    public DbSubstructurePrescreenReader(Connection connection, IAtomContainer mol,
            SourceDataset srcDataset,  int page, int pagesize) throws AmbitException {
        super(connection, mol, srcDataset, 0, page,pagesize);
        
    }

    /* (non-Javadoc)
     * @see ambit.database.search.DbSearchReader#prepareSQLStatement(java.sql.Connection, org.openscience.cdk.interfaces.Molecule, java.lang.String)
     */
	protected PreparedStatement prepareSQLStatement(Connection conn, IAtomContainer mol, int page, int pagesize, double threshold) throws AmbitException {
		boolean hasQuery = (mol != null) &&	(mol.getAtomCount() > 0)	;
		try {
			if (hasQuery) {
			    fingerprinter = new Fingerprinter(1024);
				BitSet bs = fingerprinter.getFingerprint((IAtomContainer) mol);
				long[] h16 = new long[16];
				MoleculeTools.bitset2Long16(bs,64,h16);
				String[] fp = new String[16];
				for (int i = 0; i < 16; i++) {
					fp[i] = Long.toString(h16[i]);
				}
				//System.out.println(bs.cardinality()); //bit count
				
				String bc = Integer.toString(bs.cardinality());
				StringBuffer b = new StringBuffer();
					b.append("select cbits,bc,formula,L.idsubstance,smiles  from (select fp1024.idsubstance,");
					for (int i = 0; i < 16; i++) {
						if (i > 0) b.append("+");
						b.append("bit_count(");
						b.append(fp[i]);
						b.append("& fp");
						b.append(Integer.toString(i+1).trim());
						b.append(") ");
					}
					b.append(" as cbits,");
					b.append("bc from fp1024 ");
					if ((srcDataset != null) && (srcDataset.getId()>=0)) {
						b.append(" join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=");
						b.append(srcDataset.getId());
					}
					b.append(") as L, substance ");
					b.append("where L.cbits=");
					b.append(bs.cardinality());
					b.append(" and L.idsubstance=substance.idsubstance limit ");
					b.append(page);   
					b.append(",");
					b.append(pagesize);
					
					//System.out.println(b.toString());
				

				return  conn.prepareStatement(b.toString());
			}	
		} catch (Exception ex) {
			throw new AmbitException(ex);
		}
		return null;
	}

}
