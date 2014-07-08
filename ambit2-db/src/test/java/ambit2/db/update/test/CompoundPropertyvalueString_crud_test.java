package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.db.update.value.UpdateCompoundPropertyValueString;

public class CompoundPropertyvalueString_crud_test extends CRUDTest<IStructureRecord,PropertyValue<String>> {

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<String>> createQuery()
			throws Exception {
		UpdateCompoundPropertyValueString q = new UpdateCompoundPropertyValueString();
		StructureRecord record = new StructureRecord(7,-1,null,null);
		q.setGroup(record);
		Property p =new Property("");
		p.setId(2);
		
		q.setObject(new PropertyValue<String>(p,"AABBCC"));
		return q;
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<String>> createQueryNew()
			throws Exception {
		UpdateCompoundPropertyValueString q = new UpdateCompoundPropertyValueString();
		StructureRecord record = new StructureRecord(7,-1,null,null);
		q.setGroup(record);
		Property p =new Property("");
		p.setId(2);
		StringBuilder b = new StringBuilder();
		for (int i=0; i < 500; i++) b.append("A");
		q.setObject(new PropertyValue<String>(p,b.toString()));
		return q;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<String>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT value FROM property_values join property_string using(idvalue_string) where idstructure=100211 and idproperty=2");
		Assert.assertEquals(1,table.getRowCount());
		StringBuilder b = new StringBuilder();
		Assert.assertEquals("AABBCC",table.getValue(0,"value"));
		/**
		 * Test trigger update_string_ci
		 */
		table = 	c.createQueryTable("EXPECTED","SELECT value_ci from property_ci where value_ci='aabbcc'");
		Assert.assertEquals(1,table.getRowCount());
		/**
		 * Test trigger summary_chemical_prop_update
		 */
		table = 	c.createQueryTable("EXPECTED",
				"SELECT idchemical,idstructure,idproperty,value_ci from summary_property_chemicals join structure using(idchemical) join property_ci using(id_ci) where idstructure=100211 and idproperty=2 and value_ci='aabbcc'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();	
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<IStructureRecord, PropertyValue<String>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT value,text FROM property_values join property_string using(idvalue_string) where idstructure=100211 and idproperty=2");
		Assert.assertEquals(1,table.getRowCount());
		StringBuilder b = new StringBuilder();
		for (int i=0; i < 500; i++) b.append("A");
		Assert.assertEquals(b.toString(),table.getValue(0,"text"));
		Assert.assertEquals(b.toString().substring(0,255),table.getValue(0,"value"));
		/**
		 * Test trigger insert_string_ci
		 */
		table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT value_ci from property_ci where value_ci='%s'",b.toString().substring(0,255).toLowerCase()));
		Assert.assertEquals(1,table.getRowCount());	
		
		table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idchemical,idstructure,idproperty,value_ci from summary_property_chemicals join structure using(idchemical) join property_ci using(id_ci) where idstructure=100211 and idproperty=2 and value_ci='%s'",
						b.toString().substring(0,255).toLowerCase()));
		Assert.assertEquals(1,table.getRowCount());		
		c.close();	
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<String>> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void testDelete() throws Exception {
	
	}
	@Override
	protected void deleteVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, PropertyValue<String>> updateQuery()
			throws Exception {
		return createQuery();
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<IStructureRecord, PropertyValue<String>> query)
			throws Exception {
		createVerify(query);
		
	}


}
