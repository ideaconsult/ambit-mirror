/* SearchFactory.java
 * Author: Nina Jeliazkova
 * Date: Apr 1, 2007 
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

package ambit.database.readers;

import java.sql.Connection;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectWriter;

import ambit.data.molecule.SourceDataset;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSubstanceProcessor;
import ambit.database.processors.SubstructureSearchProcessor;
import ambit.database.search.DbSearchReader;
import ambit.database.search.DbSimilarityByAtomenvironmentsReader;
import ambit.database.search.DbSimilarityByFingerprintsReader;
import ambit.database.search.DbSubstructureSearchReader;
import ambit.exceptions.AmbitException;
import ambit.io.batch.DefaultBatchConfig;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatch;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;

public class SearchFactory {
    public static final String[] title = {"Fingerprints/Tanimoto distance",  "Atom Environments", "Substructure"}; 
    public static final String[] methods = {"fingerprint",  "atomenvironment", "substructure"};
    public static final int MODE_FINGERPRINT=0;
    public static final int MODE_ATOMENVIRONMENT=1;
    public static final int MODE_SUBSTRUCTURE=2;
    
    protected SearchFactory() {
        
    }
    public static int getSimilarityByTitle(String name) {
        for (int i=0; i < title.length; i++) 
            if (title[i].equals(name)) return i;
        return -1;
    }
    public static DbSearchReader createReader(String similarity,
            Connection connection,  IAtomContainer mol, 
            SourceDataset srcDataset,double threshold, int page, int pagesize) throws Exception {
        for (int i=0; i < methods.length; i++) 
            if (methods[i].equals(similarity)) 
                return createReader(i, connection, mol, srcDataset, threshold, page, pagesize);
       return null; 
    }
    public static DbSearchReader createReader(int similarity,
            Connection connection,  IAtomContainer mol, 
            SourceDataset srcDataset,double threshold, int page, int pagesize) throws Exception {    
        DbSearchReader reader = null;

        switch (similarity) {
        
        case MODE_FINGERPRINT: 
                return new DbSimilarityByFingerprintsReader(
                        connection,
                        mol,
                        srcDataset,
                        threshold,
                        page,pagesize); 
                
        case MODE_ATOMENVIRONMENT: 
                   return new DbSimilarityByAtomenvironmentsReader(
                            connection,
                            mol,
                            srcDataset,
                            threshold,
                            page,pagesize);                    
        case MODE_SUBSTRUCTURE:
                    return new DbSubstructureSearchReader(connection,
                            mol,
                            srcDataset,
                            threshold,
                            page,pagesize);     

        default:
                return new DbSimilarityByFingerprintsReader(
                        connection,
                        mol,
                        srcDataset,
                        threshold,
                        page,pagesize); 
        }
    }
    public static IAmbitProcessor getProcessor(Connection connection, IAtomContainer mol, int similarity, boolean readSubstance) {
        
            try {
                ProcessorsChain processors = new ProcessorsChain();
                
                if (readSubstance || (similarity == MODE_SUBSTRUCTURE)) 
                    processors.add(new ReadSubstanceProcessor(connection));
                if (similarity == MODE_SUBSTRUCTURE)
                    processors.add(new SubstructureSearchProcessor(mol));
                processors.add(new ReadAliasProcessor(connection));
                processors.add(new ReadNameProcessor(connection));
                processors.add(new ReadCASProcessor(connection));
                return processors;
            } catch (AmbitException x) {
                return null;
            }
    }
    
    public static IBatch createSearchBatch(int similarity,
            Connection connection,  IAtomContainer mol, 
            SourceDataset srcDataset,double threshold, int page, int pagesize,
            IChemObjectWriter writer) throws Exception {
        DbSearchReader reader = createReader(similarity, connection, mol, srcDataset, threshold, page, pagesize);
        IAmbitProcessor processor = getProcessor(connection, mol,similarity, similarity == MODE_SUBSTRUCTURE);
        return new DefaultBatchProcessing(reader,writer,processor,
                new DefaultBatchConfig(),new DefaultBatchStatistics());
    }
}
