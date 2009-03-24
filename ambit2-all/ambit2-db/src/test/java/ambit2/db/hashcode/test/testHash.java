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

package ambit2.db.hashcode.test;

import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.sql.DataSource;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.db.DatasourceFactory;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.test.DbUnitTest;

/**
 * Hashcodes tests. 
 * @author nina
 *
 */
public class testHash extends DbUnitTest{
	//TODO make use of DBUnit test!
	public void testGetHashForDoubles() throws Exception {
		DataSource datasource;
	    Class driverClass = Class.forName("com.mysql.jdbc.Driver");
	    datasource = DatasourceFactory.getDataSource(
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "ambit2", "guest","guest" ));
        Connection connection = datasource.getConnection();
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(connection);        
        reader.open();
        ambit2.hashcode.MoleculeAndAtomsHashing molHash = new ambit2.hashcode.MoleculeAndAtomsHashing();
        Hashtable<Long, List<String>> histogram = new Hashtable<Long, List<String>>();
       
     
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		int[] idchemicals ={3109,25703};			
		for(int i=0;i<idchemicals.length;i++){
		String content = reader.getStructure(idchemicals[i]);	
			
		IIteratingChemObjectReader mReader = new MyIteratingMDLReader(new StringReader(content),b);			
		if (mReader.hasNext()) {
			Object mol = mReader.next();
			if (mol instanceof IMolecule) {
				try{
					Long hash = molHash.getMoleculeHash((IMolecule)mol);
						
						
					} catch (Exception e) {
						
						
						//e.printStackTrace();
					}
					
				}
				
			}
		}
		reader.close();
}

	
public static void testGetHashFomDB() throws Exception {
		DataSource datasource;
	    Class driverClass = Class.forName("com.mysql.jdbc.Driver");
	    datasource = DatasourceFactory.getDataSource(
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "ambit2", "guest","guest" ));
        Connection connection = datasource.getConnection();
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(connection);        
        reader.open();
        ambit2.hashcode.MoleculeAndAtomsHashing molHash = new ambit2.hashcode.MoleculeAndAtomsHashing();
        Hashtable<Long, List<String>> histogram = new Hashtable<Long, List<String>>();
       
        int records = 0;
        int record=0;
        int errors = 0;
        int doubles = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			if (o.getProperties() == null) o.setProperties(new Hashtable());
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			IIteratingChemObjectReader mReader = new MyIteratingMDLReader(new StringReader(content),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IMolecule) {
					try{
						Long hash = molHash.getMoleculeHash((IMolecule)mol);
						//IAtomContainer mol = p.parseSmiles(((IAtomContainer)o).getProperty("GENERATED_SMILES").toString());
						//hash = molHash.getMoleculeHash((IAtomContainer)mol);
						List<String> count = histogram.get(hash);
						
						if (count == null) {
							count = new ArrayList<String>();
							histogram.put(hash, count);
						} 
						//smile = generator.createSMILES((IMolecule)mol);
						//count.add(smile);
						count.add(((IAtomContainer)mol).getProperty("GENERATED_SMILES").toString());
						//count.add(((IAtomContainer)o).getProperty("CAS").toString());
						
					} catch (Exception e) {
						errors++;
						/*System.out.print(record);
	                    System.out.print('\t');                    
	                    System.out.print(((IAtomContainer)mol).getProperty("MF"));                    
						System.out.print('\t');
						System.out.print(((IAtomContainer)mol).getProperty("GENERATED_SMILES"));
						System.out.println('\t');*/
						
						//e.printStackTrace();
					}
					record++;
				}
				
			}
			
			o.clear();
			mReader.close();
			mReader = null;
			records ++;
			//if ((records % 50) == 0)
				//System.out.println(records);
		}
		reader.close();
		//fpWriter.close();
		now = System.currentTimeMillis() - now;
		System.out.println("msec/records "+(double)now/(double)records);
		
		
		Enumeration<Long> hash = histogram.keys();
		IAtomContainer mol;
		Long hash_test;
		int cis_trns = 0;
		while (hash.hasMoreElements()) {
			Long key = hash.nextElement();
			List<String> values = histogram.get(key);
			HashSet set = new HashSet(values);
			String[] array2 = (String[])(set.toArray(new String[set.size()]));
			int cis_trns_loc = 0;
			for (String value: values) {
				if((value.contains("/") || value.contains("\\") )&& cis_trns_loc == 0){
					cis_trns++;
					cis_trns_loc++;
				
				}
			}
			if (values.size()>1 && array2.length > 1 ) {
				System.out.print(key);
				System.out.print('\t');
				System.out.print(values.size());
				System.out.print('\t');
				
				System.out.print(array2.length);
				System.out.print('\t');
				//
				
				for (String value: values) {
					System.out.print(value);
					System.out.print('\t');	
					
					
					
				}
				
				System.out.println();
				doubles++;
			}
		}
		System.out.print("Records\t");
		System.out.print(record);
		System.out.print("Hashes\t");
		System.out.println(histogram.size());
		System.out.print("Errors\t");
		System.out.println(errors);	
		System.out.print("Doubles\t");
		System.out.println(doubles);
		System.out.print("Cis_trns\t");
		System.out.println(cis_trns);
	}	
	
	public static void main(String[] args) {
		try {
			testGetHashFomDB();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
