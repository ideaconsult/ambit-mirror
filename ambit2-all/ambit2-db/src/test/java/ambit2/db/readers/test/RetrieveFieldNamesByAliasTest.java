package ambit2.db.readers.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;

public class RetrieveFieldNamesByAliasTest extends RetrieveTest<Property> {

	@Override
	protected IQueryRetrieval<Property> createQuery() {
		RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias();
		return q;
	}

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Test
	public void testGetObject() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");		
		Assert.assertEquals(4,names.getRowCount());

		QueryExecutor<RetrieveFieldNamesByAlias> qe = new QueryExecutor<RetrieveFieldNamesByAlias>();		
		qe.setConnection(c.getConnection());

		ResultSet rs = qe.process((RetrieveFieldNamesByAlias)query);
		
		int count = 0; 
		while (rs.next()) {
			names = 	c.createQueryTable("EXPECTED_NAME","SELECT * FROM properties where name='"+query.getObject(rs)+"'");		
			Assert.assertEquals(1,names.getRowCount());
			count++;
		}
		Assert.assertEquals(2,count);
		rs.close();
		qe.close();
		c.close();
	}	
	
	@Test
	public void testGetObjectByName() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");		
		Assert.assertEquals(4,names.getRowCount());

		QueryExecutor<RetrieveFieldNamesByAlias> qe = new QueryExecutor<RetrieveFieldNamesByAlias>();		
		qe.setConnection(c.getConnection());

		RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias();
		q.setValue(Property.opentox_Name);
		ResultSet rs = qe.process(q);
		
		int count = 0; 
		while (rs.next()) {
			names = 	c.createQueryTable("EXPECTED_NAME","SELECT * FROM properties where idproperty = "+ query.getObject(rs).getId());		
			Assert.assertEquals(1,names.getRowCount());
			count++;
		}
		Assert.assertEquals(2,count);
		rs.close();
		qe.close();
		c.close();
	}		

	@Test
	public void testGetFieldID() {
		Assert.assertEquals("idproperty", ((RetrieveFieldNamesByAlias)query).getFieldID());
	}

	@Test
	public void testGetValueID() {
		Assert.assertEquals("name", ((RetrieveFieldNamesByAlias)query).getValueID());
	}

	@Test
	public void testGetFieldType() throws Exception {
		Assert.assertEquals(String.class,((RetrieveFieldNamesByAlias)query).getFieldType());
	}

	@Test
	public void testGetValueType() {
		Assert.assertEquals(String.class,((RetrieveFieldNamesByAlias)query).getValueType());
	}
	@Override
	protected AmbitRows<Property> createRows() throws Exception {
		return new AmbitRows<Property>();
	}
	@Override
	protected void verifyRows(AmbitRows<Property> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(2,rows.size());
		while (rows.next()) {
			Property p = rows.getObject();
			ITable table = 	c.createQueryTable("EXPECTED",
					"select idproperty,name,units,title,url,idreference,comments,null,islocal,type,rdf_type,predicate,object from properties join catalog_references using(idreference) left join property_annotation using(idproperty) where name='"+p.getName()+"' and title='"+p.getReference().getTitle()+"'");		
			Assert.assertEquals(1,table.getRowCount());			
			for (int i=1; i <= rows.getMetaData().getColumnCount();i++) {
				Object expected = table.getValue(0,rows.getMetaData().getColumnName(i));
				Object actual = rows.getObject(i);
				if ((expected == null) && (actual == null)) continue;
				else
					Assert.assertEquals(expected.toString(),actual.toString());

				
			}
			
		}
	}
}