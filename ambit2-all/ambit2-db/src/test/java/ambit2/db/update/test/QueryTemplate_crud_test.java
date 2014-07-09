package ambit2.db.update.test;

import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Template;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.update.storedquery.QueryAddTemplate;
import ambit2.db.update.storedquery.QueryDeleteTemplate;

public class QueryTemplate_crud_test  extends  CRUDTest<IStoredQuery,Template> {

	@Override
	protected IQueryUpdate<IStoredQuery, Template> createQuery()
			throws Exception {
		QueryAddTemplate qt = new QueryAddTemplate();
		StoredQuery q = new StoredQuery();
		q.setId(1);
		Template t = new Template("Endpoints");
		qt.setGroup(q);
		qt.setObject(t);
		return qt;
	}

	@Override
	protected IQueryUpdate<IStoredQuery, Template> createQueryNew()
			throws Exception {
		QueryAddTemplate qt = new QueryAddTemplate();
		StoredQuery q = new StoredQuery();
		q.setId(1);
		Template t = new Template();
		t.setId(44);
		qt.setGroup(q);
		qt.setObject(t);
		return qt;
	}

	@Override
	protected void createVerify(IQueryUpdate<IStoredQuery, Template> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery,idtemplate from query join template using(idtemplate) where idquery=1 and template.name='Endpoints'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(new BigInteger("85"),table.getValue(0,"idtemplate"));
		c.close();
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<IStoredQuery, Template> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery,idtemplate,template.name as name from query join template using(idtemplate) where idquery=1 and idtemplate=44");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("Octanol-water partition coefficient (Kow)",table.getValue(0,"name"));
		c.close();
		
	}

	@Override
	protected IQueryUpdate<IStoredQuery, Template> deleteQuery()
			throws Exception {
		QueryDeleteTemplate qt = new QueryDeleteTemplate();
		StoredQuery q = new StoredQuery();
		q.setId(2);
		qt.setGroup(q);
		return qt;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IStoredQuery, Template> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery,idtemplate from query  where idquery=2");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertNull(table.getValue(0,"idtemplate"));
		c.close();
		
	}
	@Override
	public void testUpdate() throws Exception {

	}

	@Override
	protected IQueryUpdate<IStoredQuery, Template> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<IStoredQuery, Template> query)
			throws Exception {
	
	}

}
