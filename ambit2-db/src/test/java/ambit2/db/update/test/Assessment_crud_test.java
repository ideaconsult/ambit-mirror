package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.AmbitUser;
import ambit2.db.SessionID;
import ambit2.db.update.assessment.CreateAssessment;
import ambit2.db.update.assessment.DeleteAssessment;
import ambit2.db.update.assessment.UpdateAssessment;

public class Assessment_crud_test extends CRUDTest<AmbitUser,SessionID> {

	@Override
	protected IQueryUpdate<AmbitUser, SessionID> createQuery() throws Exception {
		return new CreateAssessment();
	}

	@Override
	protected IQueryUpdate<AmbitUser, SessionID> createQueryNew()
			throws Exception {
		SessionID id = new SessionID();
		id.setName("xxx");
		return new CreateAssessment(new AmbitUser("admin"),id);
	}

	@Override
	protected void createVerify(IQueryUpdate<AmbitUser, SessionID> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idsessions, user_name, title from sessions where user_name='guest' and idsessions =1");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("temp",table.getValue(0,"title"));
		c.close();			
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<AmbitUser, SessionID> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idsessions, user_name, title from sessions where user_name='admin' and title='xxx'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("xxx",table.getValue(0,"title"));
		c.close();	
		
	}

	@Override
	protected IQueryUpdate<AmbitUser, SessionID> deleteQuery() throws Exception {
		return new DeleteAssessment(new AmbitUser("guest"),new SessionID(1));
	}

	@Override
	protected void deleteVerify(IQueryUpdate<AmbitUser, SessionID> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idsessions, user_name, title from sessions where user_name='guest' and idsessions=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();			
		
	}

	@Override
	protected IQueryUpdate<AmbitUser, SessionID> updateQuery() throws Exception {
		SessionID s = new SessionID(1);
		s.setName("new name");
		return new UpdateAssessment(null,s);
	}

	@Override
	protected void updateVerify(IQueryUpdate<AmbitUser, SessionID> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idsessions, user_name, title from sessions where user_name='guest' and idsessions=1 and title='new name'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();		
		
	}

}
