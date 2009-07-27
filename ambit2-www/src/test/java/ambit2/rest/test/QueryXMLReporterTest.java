package ambit2.rest.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.data.MediaType;
import org.restlet.resource.Variant;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.CompoundResource;

public class QueryXMLReporterTest extends DbUnitTest {
	
	@Before
	public void setUp() throws Exception {
		setUpDatabase("src/test/resources/src-datasets.xml");

		IDatabaseConnection c = getConnection();
		ITable fp = 	c.createQueryTable("EXPECTED_FP","SELECT * FROM fp1024");		
		Assert.assertEquals(4,fp.getRowCount());		
		c.close();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimilarity() throws Exception {
		SimilarityResource resource = new SimilarityResource(null,null,null);
		RepresentationConvertor  convertor = resource.createConvertor(new Variant(MediaType.TEXT_XML));	
		IDatabaseConnection c = getConnection();
		
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		q.setThreshold(0.1);
		q.setCondition(NumberCondition.getInstance(">"));		
		
		FingerprintGenerator gen = new FingerprintGenerator();
		SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		
		try {
			convertor.getReporter().setConnection(c.getConnection());
			q.setValue(gen.process(parser.parseSmiles("C")));			
			System.out.println(convertor.process(q).getText());
			
		} catch (InvalidSmilesException x) {
			throw new AmbitException(x);
		} finally {
			c.close();
		}
	
	}
	
	@Test
	public void testStructure() throws Exception {
		CompoundResource resource = new CompoundResource(null,null,null);
		RepresentationConvertor  convertor = resource.createConvertor(new Variant(MediaType.TEXT_XML));		
		IDatabaseConnection c = getConnection();
		IStructureRecord record = new StructureRecord();
		record.setIdstructure(100215);
		QueryStructureByID q = new QueryStructureByID();
		q.setValue(record);
		convertor.getReporter().setConnection(c.getConnection());
		System.out.println(convertor.process(q).getText());
		c.close();
	}	
	
	@Test
	public void testSDF() throws Exception {
		CompoundResource resource = new CompoundResource(null,null,null);
		RepresentationConvertor  convertor = resource.createConvertor(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));		
		IDatabaseConnection c = getConnection();
		IStructureRecord record = new StructureRecord();
		record.setIdstructure(100215);
		QueryStructureByID q = new QueryStructureByID();
		q.setValue(record);
		convertor.getReporter().setConnection(c.getConnection());
		System.out.println(convertor.process(q).getText());
		c.close();
	}	
	
	@Test
	public void testSmiles() throws Exception {
		CompoundResource resource = new CompoundResource(null,null,null);
		RepresentationConvertor  convertor = resource.createConvertor(new Variant(ChemicalMediaType.CHEMICAL_SMILES));		
		IDatabaseConnection c = getConnection();
		IStructureRecord record = new StructureRecord();
		record.setIdstructure(100215);
		QueryStructureByID q = new QueryStructureByID();
		q.setValue(record);
		convertor.getReporter().setConnection(c.getConnection());
		System.out.println(convertor.process(q).getText());
		c.close();
	}		
	
	@Test
	public void testPDF() throws Exception {
		CompoundResource resource = new CompoundResource(null,null,null);
		RepresentationConvertor  convertor = resource.createConvertor(new Variant(MediaType.APPLICATION_PDF));		
		IDatabaseConnection c = getConnection();
		IStructureRecord record = new StructureRecord();
		record.setIdstructure(100215);
		QueryStructureByID q = new QueryStructureByID();
		q.setValue(record);
		convertor.getReporter().setConnection(c.getConnection());
		convertor.process(q);
		c.close();
	}		

}
