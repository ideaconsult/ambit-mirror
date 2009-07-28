package ambit2.rest.test;
import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.rest.dataset.DatasetReporter;
import ambit2.rest.query.XMLTags;

/**
 * ambit.acad.bg/query/scope/dataset/all/similarity/method/fingerprints/distance/tanimoto/threshold/0.5/smiles/CCC
 * @author nina
 *
 */
public class DatasetReporterTest extends DbUnitTest {
	DatasetReporter reporter;
	@Before
	public void setUp() throws Exception {
		setUpDatabase("src/test/resources/src-datasets.xml");

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset");		
		Assert.assertEquals(3,names.getRowCount());		
		c.close();
		reporter = new DatasetReporter(new Reference());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToXML() throws Exception {
		IDatabaseConnection c = getConnection();
		RetrieveDatasets q = new RetrieveDatasets();
		reporter.setConnection(c.getConnection());
		String xml = XMLTags.save(reporter.process(q));
		System.out.println(xml);
		c.close();
		
	}

	@Test
	public void testToXMLDataset() throws Exception {

		IDatabaseConnection c = getConnection();
		RetrieveDatasets q = new RetrieveDatasets();
		q.setValue(new SourceDataset("Dataset 2"));
		reporter.setConnection(c.getConnection());
		String xml = XMLTags.save(reporter.process(q));
		System.out.println(xml);
		c.close();
		
	}	

}
