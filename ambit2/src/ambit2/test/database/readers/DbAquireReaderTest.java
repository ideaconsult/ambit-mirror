package ambit2.test.database.readers;

import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

import toxTree.query.MolFlags;
import ambit2.config.AmbitCONSTANTS;
import ambit2.database.ConnectionPool;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.aquire.AquireRecords;
import ambit2.database.aquire.DbAquireProcessor;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.readers.DbStructureReader;
import ambit2.database.search.DbDatasetReader;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.toxtree.MetaboliteProcessor;
import ambit2.test.ITestDB;

public class DbAquireReaderTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbAquireReaderTest.class);
	}
	public void test() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,"ambit","root","",1,1);
			Connection conn = pool.getConnection();
			
			IAtomContainer ac = new Molecule();
			ac.setProperty(CDKConstants.CASRN, "50-00-0");
			DbAquireProcessor processor = new DbAquireProcessor(conn,ITestDB.host,ITestDB.port,ITestDB.user,"",null,null,false);
			
			processor.process(ac);
			Object o = ac.getProperty(AmbitCONSTANTS.AQUIRE);
			assertTrue(o instanceof AquireRecords);
			assertEquals(666, ((AquireRecords)o).size());

			ac = new Molecule();
			ac.setProperty(CDKConstants.CASRN, "60571");
			processor = new DbAquireProcessor(conn,ITestDB.host,ITestDB.port,ITestDB.user,"","LC50",null,false);
			processor.process(ac);
			o = ac.getProperty(AmbitCONSTANTS.AQUIRE);
			assertTrue(o instanceof AquireRecords);
			
			assertEquals(715, ((AquireRecords)o).size());
			
			
			processor.close();
			
			
			pool.returnConnection(conn);
			pool = null;			
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

	public void testAquireCompounds() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,"ambit","root","",1,1);
			Connection conn = pool.getConnection();
		
		    SourceDataset d = new SourceDataset();
			d.setName("AQUIRE");
			DbDatasetReader reader = new DbDatasetReader(conn,d,1,10000);
			IAmbitProcessor p = new ReadStructureProcessor(conn);
			IAmbitProcessor m = new MetaboliteProcessor();
			int n = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				o = p.process(o);
				m.process(o);
				if (((IChemObject)o).getProperty(MolFlags.MOLFLAGS) != null) { 
				        
					System.out.print(((IChemObject)o).getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE));
					if ((n % 20) == 0)
					    System.out.println(",");
					else System.out.print(",");
					n++;
				}
			}
			System.out.println(n);
			reader.close();
			pool.returnConnection(conn);
			pool = null;			
			conn.close();						
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	public void xtestMetabolizableCompounds() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,"ambit","root","",1,1);
			Connection conn = pool.getConnection();
		
		    
			DbStructureReader reader = new DbStructureReader(conn,"select idstructure from structure");
			IAmbitProcessor p = new ReadStructureProcessor(conn);
			IAmbitProcessor m = new MetaboliteProcessor();
			int n = 0;
			int all = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				o = p.process(o);
				m.process(o);
				all++;
				if (((IChemObject)o).getProperty(MolFlags.MOLFLAGS) != null) { 
				        
					n++;
					System.out.println(n + " of " + all + " = " + 100*n/all + " %");
				}
			}
			reader.close();
			pool.returnConnection(conn);
			pool = null;			
			conn.close();						
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

}
