package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.db.update.value.UpdateStructurePropertyIDNumber;

public class StructurePropertyValueSNumber_crud_test extends CRUDTest<IStructureRecord,PropertyValue<Number>> {

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<Number>> createQuery()
			throws Exception {
		UpdateStructurePropertyIDNumber q = new UpdateStructurePropertyIDNumber();
		StructureRecord record = new StructureRecord(7,100211,null,null);
		q.setGroup(record);
		Property p =new Property("");
		p.setId(2);
		
		q.setObject(new PropertyValue(p,100.1));
		return q;
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<Number>> createQueryNew()
			throws Exception {
		return createQuery();
	}

	@Override
	protected void createVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<Number>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT value_num FROM property_values where idstructure=100211 and idproperty=2");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(100.1,table.getValue(0,"value_num"));
		c.close();	
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<IStructureRecord, PropertyValue<Number>> query)
			throws Exception {
		createVerify(query);
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<Number>> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void testDelete() throws Exception {
	
	}
	@Override
	protected void deleteVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<Number>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<Number>> updateQuery()
			throws Exception {
		return createQuery();
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<Number>> query)
			throws Exception {
		createVerify(query);
		
	}

}
