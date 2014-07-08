package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.db.update.propertyannotations.CreatePropertyAnnotation;
import ambit2.db.update.propertyannotations.DeletePropertyAnnotation;
import ambit2.db.update.propertyannotations.UpdatePropertyAnnotation;

public class PropertyAnnotation_crud_test  extends CRUDTest<Property,PropertyAnnotation>  {

	@Override
	protected IQueryUpdate<Property, PropertyAnnotation> createQuery()
			throws Exception {
		CreatePropertyAnnotation q = new CreatePropertyAnnotation();
		Property p = Property.getNameInstance();
		p.setId(1);
		PropertyAnnotation pa = new PropertyAnnotation();
		pa.setIdproperty(1);
		pa.setObject("testA");
		pa.setPredicate("testB");
		pa.setType("Confidence");
		q.setObject(pa);
		q.setGroup(p);
		return q;
	}
	
	@Test
	@Override
	public void testCreate() throws Exception {
		super.testCreate();
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotation> createQueryNew()
			throws Exception {
		CreatePropertyAnnotation q = new CreatePropertyAnnotation();
		PropertyAnnotation pa = new PropertyAnnotation();
		pa.setIdproperty(1);
		pa.setObject("testA");
		pa.setPredicate("testB");
		pa.setType("Confidence");
		q.setObject(pa);
		return q;
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotation> updateQuery()
			throws Exception {
		UpdatePropertyAnnotation q = new UpdatePropertyAnnotation();
		PropertyAnnotation pa = new PropertyAnnotation();
		pa.setIdproperty(1);
		pa.setObject("test1");
		pa.setPredicate("test2");
		pa.setType("Name");
		q.setObject(pa);
		return q;
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotation> deleteQuery()
			throws Exception {
		DeletePropertyAnnotation q = new DeletePropertyAnnotation();
		Property p = Property.getNameInstance();
		p.setId(1);
		q.setGroup(p);
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<Property, PropertyAnnotation> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM property_annotation where idproperty=1");
		
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<Property, PropertyAnnotation> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM property_annotation where idproperty=1");
		
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void updateVerify(IQueryUpdate<Property, PropertyAnnotation> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM property_annotation where idproperty=1");
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Property, PropertyAnnotation> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM property_annotation where idproperty=1");
		
		Assert.assertEquals(0,table.getRowCount());
		c.close();

		
	}


}
