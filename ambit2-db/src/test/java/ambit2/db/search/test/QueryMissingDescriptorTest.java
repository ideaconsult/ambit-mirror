package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.search.IQueryObject;

public class QueryMissingDescriptorTest extends QueryTest<IQueryObject<IStructureRecord>> {
	@Test
	public void test() throws Exception {
		Assert.assertEquals(
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric,null as text from structure where (type_structure != 'NA') and idstructure not in (select idstructure from property_values join properties using(idproperty) join catalog_references using(idreference) where   title=?)",
		query.getSQL());
	}
	@Override
	protected IQueryObject<IStructureRecord> createQuery() throws Exception {
		ProcessorMissingDescriptorsQuery processor = new ProcessorMissingDescriptorsQuery();
		Profile<Property> profile = new Profile<Property>();
		Property property = Property.getInstance("","");
		property.setClazz(XLogPDescriptor.class);
		property.setEnabled(true);
		profile.add(property);
		return processor.process(profile);
	}

	@Override
	protected void verify(IQueryObject<IStructureRecord> query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		Assert.assertEquals(4,count);
		
	}
}


