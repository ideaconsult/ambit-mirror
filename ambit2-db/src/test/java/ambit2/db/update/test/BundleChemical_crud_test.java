package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.bundle.chemicals.AddChemicalToBundle;
import ambit2.db.update.bundle.chemicals.DeleteChemicalsFromBundle;

public class BundleChemical_crud_test extends CRUDTest<SubstanceEndpointsBundle, IStructureRecord> {
    @Override
    public void setUp() throws Exception {
	super.setUp();
	dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> createQuery() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	AddChemicalToBundle query = new AddChemicalToBundle();
	query.setGroup(bundle);

	StructureRecord record = new StructureRecord(10, -1, null, null);
	record.setProperty(new Property("tag", LiteratureEntry.getBundleReference(bundle)), "target");
	record.setProperty(new Property("remarks", LiteratureEntry.getBundleReference(bundle)), "ABCDEF");
	query.setObject(record);
	return query;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> createQueryNew() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	AddChemicalToBundle query = new AddChemicalToBundle();
	query.setGroup(bundle);

	StructureRecord record = new StructureRecord(7, -1, null, null);
	record.setProperty(new Property("tag", LiteratureEntry.getBundleReference(bundle)), "target");
	record.setProperty(new Property("remarks", LiteratureEntry.getBundleReference(bundle)), "ABCDEF");
	query.setObject(record);
	return query;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> updateQuery() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	AddChemicalToBundle query = new AddChemicalToBundle();
	query.setGroup(bundle);

	StructureRecord record = new StructureRecord(29141, -1, null, null);
	record.setProperty(new Property("tag", LiteratureEntry.getBundleReference(bundle)), "target");
	record.setProperty(new Property("remarks", LiteratureEntry.getBundleReference(bundle)), "ABCDEF");
	query.setObject(record);
	return query;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> deleteQuery() throws Exception {
	DeleteChemicalsFromBundle query = new DeleteChemicalsFromBundle();
	query.setGroup(new SubstanceEndpointsBundle(1));
	query.setObject(new StructureRecord(29141, -1, null, null));
	return query;
    }

    @Override
    protected void createVerify(IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED_BUNDLE",
		"SELECT idbundle,idchemical,tag,remarks from bundle_chemicals where idbundle=1 and idchemical=10");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("target", table.getValue(0, "tag"));
	Assert.assertEquals("ABCDEF", table.getValue(0, "remarks"));
	c.close();
    }

    @Override
    protected void createVerifyNew(IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED_BUNDLE",
		"SELECT idbundle,idchemical,tag,remarks from bundle_chemicals where idbundle=1 and idchemical=7");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("target", table.getValue(0, "tag"));
	Assert.assertEquals("ABCDEF", table.getValue(0, "remarks"));
	c.close();
    }

    @Override
    protected void updateVerify(IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED_BUNDLE",
		"SELECT idbundle,idchemical,tag,remarks from bundle_chemicals where idbundle=1 and idchemical=29141");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("target", table.getValue(0, "tag"));
	Assert.assertEquals("ABCDEF", table.getValue(0, "remarks"));
	c.close();
    }

    @Override
    protected void deleteVerify(IQueryUpdate<SubstanceEndpointsBundle, IStructureRecord> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED_BUNDLE",
		"SELECT idbundle,idchemical from bundle_chemicals where idbundle=1 and idchemical=29141");
	Assert.assertEquals(0, table.getRowCount());
	c.close();
    }

}