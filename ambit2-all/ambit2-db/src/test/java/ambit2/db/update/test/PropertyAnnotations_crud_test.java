package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.db.update.propertyannotations.CreatePropertyAnnotations;

public class PropertyAnnotations_crud_test  extends CRUDTest<Property,PropertyAnnotations> {

	@Override
	protected IQueryUpdate<Property, PropertyAnnotations> createQuery()
			throws Exception {
		CreatePropertyAnnotations q = new CreatePropertyAnnotations();
		Property p = Property.getNameInstance();
		p.setId(1);
		PropertyAnnotations pas = new PropertyAnnotations();
		PropertyAnnotation pa = new PropertyAnnotation();
		pa.setIdproperty(1);
		pa.setObject("testA");
		pa.setPredicate("testB");
		pa.setType("Confidence");
		pas.add(pa);
		pa = new PropertyAnnotation();
		pa.setIdproperty(1);
		pa.setObject("testC");
		pa.setPredicate("testB");
		pa.setType("Confidence");
		pas.add(pa);
		q.setObject(pas);
		q.setGroup(p);
		return q;
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotations> createQueryNew()
			throws Exception {
		return null;
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotations> updateQuery()
			throws Exception {

		return null;
	}

	@Override
	protected IQueryUpdate<Property, PropertyAnnotations> deleteQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<Property, PropertyAnnotations> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM property_annotation where idproperty=1");
		
		Assert.assertEquals(3,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<Property, PropertyAnnotations> query) throws Exception {
	
		
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<Property, PropertyAnnotations> query) throws Exception {
		
		
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<Property, PropertyAnnotations> query) throws Exception {

		
	}
	@Override
	public void testCreateNew() throws Exception {
	
	}
	@Override
	public void testDelete() throws Exception {
	}
	@Override
	public void testUpdate() throws Exception {

	}
	
}
