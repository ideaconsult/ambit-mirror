package ambit2.test.io;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import ambit2.database.ConnectionPool;
import ambit2.io.RawIteratingSDFReader;
import ambit2.repository.PropertyWriter;
import ambit2.repository.RepositoryReader;
import ambit2.repository.RepositoryWriter;
import ambit2.repository.StructureRecord;

public class TestRawIteratingSDFReader extends TestCase {
	public void xtest() throws Exception  {
		
		ConnectionPool pool = new ConnectionPool("localhost","3306","ambit_repository","root","sinanica",1,1);
        Connection connection = pool.getConnection();
        
		String filename = "D:/nina/Chemical Databases/EINECS/einecs_structures_V13Apr07.sdf";
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
		RepositoryWriter writer = new RepositoryWriter(connection);
		int records = 0;
		long now = System.currentTimeMillis();
		while (reader.hasNext()) {
			Object o = reader.next();
			writer.write(o.toString());
			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		writer.close();
		now = System.currentTimeMillis() - now;
		System.out.println("Records "+records + " " + now + " ms.");
	}
	public void testPropertyWriter() throws Exception {
		
		ConnectionPool pool = new ConnectionPool("localhost","3306","ambit_repository","root","sinanica",1,1);
        Connection connection = pool.getConnection();
        RepositoryReader reader = new RepositoryReader(connection);
        PropertyWriter propertyWriter = new PropertyWriter(connection);
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		StructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure()).trim();
			
			IteratingMDLReader mReader = new IteratingMDLReader(new StringReader(content),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IMolecule) {
					o.setProperties(((IMolecule)mol).getProperties());
					propertyWriter.write(o);
				}
				
			}
			
			o.clear();
			mReader.close();
			mReader = null;
			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		propertyWriter.close();
		now = System.currentTimeMillis() - now;
		System.out.println("msec/records "+(double)now/(double)records);
	}
}
