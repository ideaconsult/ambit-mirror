package ambit2.ambitxt.test;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.BitSet;
import java.util.Hashtable;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.DatasourceFactory;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.RepositoryWriter;


public class TestRawIteratingSDFReader extends RepositoryTest {
    /**
     * Records 100204 5182341 ms.
     * @throws Exception
     */
	public void xtest() throws Exception  {
		
        Connection connection = datasource.getConnection();
        
		String filename = "D:/nina/IdeaConsult/Projects/Ambit2/EINECS/einecs_structures_V13Apr07.sdf";
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
		RepositoryWriter writer = new RepositoryWriter();
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		long now = System.currentTimeMillis();

		while (reader.hasNext()) {
            IStructureRecord record = reader.nextRecord();
			writer.write(record);
			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		writer.close();
		now = System.currentTimeMillis() - now;
		System.out.println("Records "+records + " " + now + " ms.");
	}

	public void testFingerprintWriter() throws Exception {
		datasource = DatasourceFactory.getDataSource(
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "ambit2", "guest","guest" ));		
        Connection connection = datasource.getConnection();
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(connection);
        FingerprintGenerator gen = new FingerprintGenerator();
        FP1024Writer fpWriter = new FP1024Writer();
        fpWriter.setConnection(connection);
        fpWriter.open();
        reader.open();
        int records = 0;
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
					long mark = System.currentTimeMillis();
					BitSet bitset = gen.process((IMolecule)mol);
	      			o.getProperties().put(AmbitCONSTANTS.FingerprintTIME,System.currentTimeMillis()-mark);
					o.getProperties().put(AmbitCONSTANTS.Fingerprint,bitset);
					fpWriter.write(o);
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
		fpWriter.close();
		now = System.currentTimeMillis() - now;
		System.out.println("msec/records "+(double)now/(double)records);
	}	
	/*
	public void xtestPropertyWriter() throws Exception {
		datasource = DatasourceFactory.getDataSource(
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "ambit2", "guest","guest" ));		
        Connection connection = datasource.getConnection();
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(connection);
        PropertyWriter propertyWriter = new PropertyWriter();
        propertyWriter.setConnection(connection);
        propertyWriter.open();
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			IIteratingChemObjectReader mReader = new MyIteratingMDLReader(new StringReader(content),b);
			
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
	*/

 
    /**
select SUBSTRING_INDEX(user(),'@',1)

insert into sessions (user_name) values (SUBSTRING_INDEX(user(),'@',1))
insert into query (idsessions,name) values (1,"test")


insert ignore into query_results (idquery,idstructure,selected)
select 1,idstructure,1 from structure limit 100,200

     */
}
