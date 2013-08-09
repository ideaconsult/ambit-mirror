package ambit2.db.processors.test;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.i5.io.I5ZReader;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.relation.UpdateSubstanceRelation;

public class SubstanceWriterTest extends DbUnitTest {

	
	@Test
	public void testWriteMultipleFiles_i5d() throws Exception {
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(0,chemicals.getRowCount());
		ITable strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(0,strucs.getRowCount());
		ITable srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
		Assert.assertEquals(0,srcdataset.getRowCount());
		ITable struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(0,struc_src.getRowCount());
		ITable property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(0,property.getRowCount());
		ITable property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(0,property_values.getRowCount());

		/**
		 * Now reading only substances and reference substances
    Document types:
    EndpointStudyRecord: 877
    AttachmentDocument: 5
    LegalEntity: 1
    ReferenceSubstance: 6
    Substance: 1
    EndpointRecord: 14
		 */
		URL url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i5z");
		//URL url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/IUC4-21159b7a-0479-342b-be33-4aa1f079ec8e.i5z");
		
		//URL url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/formaldehyde.i5z");
		//URL url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/Benzoicacid.i5z");
		
		Assert.assertNotNull(url);
		File i5z = new File(url.getFile());
		Assert.assertTrue(i5z.exists());

	   FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith(".");
	        }
	    };

	    I5ZReader reader = new I5ZReader(i5z);
	    reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
			}
			
			@Override
			public void handleError(String message, Exception exception) {
			}
			
			@Override
			public void handleError(String message) {
			}
		});
		int records = write(reader,c.getConnection(),new ReferenceSubstanceUUID());
		
		Assert.assertEquals(7,records);
		reader.close();
        c.close();
        
        c = getConnection();
        ITable substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
        Assert.assertEquals(1,substance.getRowCount());

		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(6,chemicals.getRowCount());
		//there are two empty file without $$$$ sign, which are skipped
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='I5Z INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(6,struc_src.getRowCount());
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where comments='http://www.opentox.org/api/1.1#IUCLID5_UUID'");
		Assert.assertEquals(1,property.getRowCount());
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) order by name");
		//Assert.assertEquals(34,property.getRowCount());
		Assert.assertEquals(6,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(34,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(13,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(41,p_tuples.getRowCount());				
		ITable p_cas = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='CasRN'");
		Assert.assertEquals(6,p_cas.getRowCount());
		ITable p_ec = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='EC'");
		Assert.assertEquals(6,p_ec.getRowCount());		
		ITable p_uuid = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='I5UUID'");
		Assert.assertEquals(6,p_uuid.getRowCount());
		c.close();
	}	
	
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(reader, connection,new ReferenceSubstanceUUID());
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		
		RepositoryWriter writer = new RepositoryWriter();
		writer.setUsePreferredStructure(true);
		if (key != null) writer.setPropertyKey(key);
		
		writer.setDataset(new SourceDataset("I5Z INPUT",LiteratureEntry.getInstance("File","file:input.i5z")));
		writer.setConnection(connection);
        writer.open();
        CreateSubstance q = new CreateSubstance();
        UpdateSubstanceRelation qr = new UpdateSubstanceRelation();
        UpdateExecutor x = new UpdateExecutor();
        x.setCloseConnection(false);
        x.setConnection(connection);
		int records = 0;
		while (reader.hasNext()) {
            IStructureRecord record = reader.nextRecord();
            if (record instanceof SubstanceRecord) {
            	SubstanceRecord substance = (SubstanceRecord) record;
            	q.setObject(substance);
            	x.process(q);
            	Assert.assertTrue(substance.getIdsubstance()>0);
            	Assert.assertTrue(substance.getRelatedStructures().size()>0);
            	for (CompositionRelation rel : substance.getRelatedStructures()) {
            		if (rel.getSecondStructure().getIdchemical()<=0) {
            			writer.write(rel.getSecondStructure());		
            		}
            		System.out.println(rel.getSecondStructure().getContent());
            		System.out.println(rel.getSecondStructure().getProperty(Property.getI5UUIDInstance()));
            		qr.setCompositionRelation(rel);
            		x.process(qr);
            	}
            	System.out.println(record.getClass().getName());
    			records ++;
            } else if (record instanceof IStructureRecord) {
            	writer.write(record);
    			records ++;
            }
		}
		x.close();
		reader.close();
		writer.close();
		return records;
	}	
}
