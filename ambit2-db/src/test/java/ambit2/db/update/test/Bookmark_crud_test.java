package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Bookmark;
import ambit2.db.update.bookmark.CreateBookmark;
import ambit2.db.update.bookmark.DeleteBookmark;
import ambit2.db.update.bookmark.UpdateBookmark;

public class Bookmark_crud_test extends CRUDTest<Object,Bookmark>  {

	@Override
	protected IQueryUpdate<Object,Bookmark> createQuery() throws Exception {
		Bookmark ref = new Bookmark();
		ref.setCreator("test");
		ref.setRecalls("http://example.com");
		ref.setHasTopic(Bookmark.ResourceType.Dataset.toString());
		ref.setTitle("my dataset");
		return new CreateBookmark(ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,Bookmark> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM bookmark where recalls='http://example.com' and title='my dataset' and hasTopic='%s'",
						Bookmark.ResourceType.Dataset.toString()));
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,Bookmark> deleteQuery() throws Exception {
		Bookmark ref = new Bookmark();
		ref.setId(1);
		return new DeleteBookmark(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,Bookmark> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM bookmark where idbookmark=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object,Bookmark> updateQuery() throws Exception {
		Bookmark ref = new Bookmark();
		ref.setId(1);
		ref.setCreator("test");
		ref.setRecalls("http://example.com");
		ref.setHasTopic(Bookmark.ResourceType.Algorithm.toString());
		ref.setTitle("my algorithm");
		return new UpdateBookmark(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,Bookmark> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM bookmark where hasTopic='Model'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM bookmark where hasTopic='Algorithm'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object, Bookmark> createQueryNew()
			throws Exception {
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, Bookmark> query)
			throws Exception {
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}
