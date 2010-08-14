package ambit2.db.search.test;

import javax.swing.DefaultListModel;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.data.Range;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.results.CrossViewPropertyMetric;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.StoredQuery;

public class CrossViewPropertyMetricTest extends DbUnitTest{
	protected CrossViewPropertyMetric view;
	protected QueryExecutor executor;
	protected String dbFile = "src/test/resources/ambit2/db/processors/test/query-datasets-string.xml";
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		executor = new QueryExecutor();
		view = new CrossViewPropertyMetric();
	}
	
	@Test
	public void testSelect() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		
		DefaultListModel propertyValues = new DefaultListModel();
		propertyValues.addElement("Extreme");
		propertyValues.addElement("Strong");
		propertyValues.addElement("Moderate");
		propertyValues.addElement("Weak");
		propertyValues.addElement("NC");
		view.setRows(propertyValues);
		
		DefaultListModel metricValues = new DefaultListModel();
		metricValues.addElement(new Range<Double>(0.0,0.25));
		metricValues.addElement(new Range<Double>(0.25,0.5));
		metricValues.addElement(new Range<Double>(0.5,0.6));
		metricValues.addElement(new Range<Double>(0.6,0.7));		
		metricValues.addElement(new Range<Double>(0.7,0.8));
		metricValues.addElement(new Range<Double>(0.8,0.9));
		metricValues.addElement(new Range<Double>(0.9,1.0));		
		view.setColumns(metricValues);
		
		view.setQuery(new StoredQuery(1));
		
		view.setProperty(Property.getInstance("LLNA Class","Skinsens_dataset.sdf"));
		view.setExec(executor);
					
		String expected = 
		"Metric-->	(0,0.25]	(0.25,0.5]	(0.5,0.6]	(0.6,0.7]	(0.7,0.8]	(0.8,0.9]	(0.9,1]	\n"+	
		"Extreme								\n"+	
		"Strong							1	\n"+		
		"Moderate							6	\n"+		
		"Weak							3	\n"+		
		"NC								";

		long now = System.currentTimeMillis();
		Assert.assertEquals(expected, view.toString());
		//System.out.println("\n"+(System.currentTimeMillis()-now)+"ms");
		executor.close();
		
		c.close();
		
		
	}	
}
