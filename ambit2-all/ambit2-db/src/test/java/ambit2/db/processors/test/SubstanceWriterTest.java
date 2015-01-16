package ambit2.db.processors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ProtocolApplicationAnnotated;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.substance.processor.DBBundleStudyWriter;
import ambit2.db.substance.processor.DBMatrixValueMarker;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.db.update.bundle.matrix.DeleteMatrixValue;

public class SubstanceWriterTest extends DbUnitTest {

    public int write(SubstanceEndpointsBundle bundle, IRawReader<IStructureRecord> reader, Connection connection)
	    throws Exception {
	return write(bundle, reader, connection, new ReferenceSubstanceUUID());
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection) throws Exception {
	return write(null, reader, connection, new ReferenceSubstanceUUID());
    }

    public int write(SubstanceEndpointsBundle bundle, IRawReader<IStructureRecord> reader, Connection connection,
	    PropertyKey key) throws Exception {
	return write(bundle, reader, connection, key, true, true, true);
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key) throws Exception {
	return write(null, reader, connection, key, true, true, true);
    }

    public int write(SubstanceEndpointsBundle bundle, IRawReader<IStructureRecord> reader, Connection connection,
	    PropertyKey key, boolean splitRecord, boolean clearMeasurements, boolean clearComposition) throws Exception {
	DBSubstanceWriter writer;
	if (bundle != null)
	    writer = new DBBundleStudyWriter(bundle, DBSubstanceWriter.datasetMeta(), new SubstanceRecord());
	else
	    writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(), new SubstanceRecord(), clearMeasurements,
		    clearComposition);
	writer.setSplitRecord(splitRecord);
	writer.setConnection(connection);
	writer.open();
	int records = 0;
	while (reader.hasNext()) {
	    Object record = reader.next();
	    if (record == null)
		continue;
	    Assert.assertTrue(record instanceof IStructureRecord);
	    writer.setImportedRecord((SubstanceRecord) record);
	    writer.process((IStructureRecord) record);
	    records++;
	}
	writer.close();
	return records;
    }

    public SubstanceStudyParser getJSONReader(String json) throws Exception {
	InputStream in = null;
	try {
	    in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/data/json/" + json);
	    return new SubstanceStudyParser(new InputStreamReader(in, "UTF-8"));
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (Exception x) {
	    }
	}
    }

    @Test
    public void testWriteJSONStudies() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(0, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("study.json");
	    write(null, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, false, false);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(2, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(7, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	    Assert.assertEquals(21, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testWriteJSONStudies_generateuuid() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(0, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("study_nouuid.json");
	    write(null, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, false, false);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(2, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(7, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	    Assert.assertEquals(21, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testWriteJSONmatrixUpdate() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(4, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(1, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(4, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("matrixupdate.json");
	    SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	    write(bundle, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, false, false);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c
		    .createQueryTable(
			    "EXPECTED",
			    "SELECT topcategory,endpointcategory,guidance,interpretation_criteria,reference,studyResultType FROM bundle_substance_protocolapplication where idbundle=1");
	    // only studies for already existing substances are written
	    Assert.assertEquals(2, substance.getRowCount());

	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM bundle_substance_experiment where idbundle=1");
	    Assert.assertEquals(2, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testWriteJSONStudiesCellViability() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(0, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("cell_viability.json");
	    write(null, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, false, false);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	    Assert.assertEquals(2, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testWriteJSONStudiesBundle() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(4, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(1, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(4, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("study.json");
	    SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	    write(bundle, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, false, false);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED",
		    "SELECT * FROM bundle_substance_protocolapplication where idbundle=1");
	    // only studies for already existing substances are written
	    Assert.assertEquals(5, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM bundle_substance_experiment where idbundle=1");
	    Assert.assertEquals(13, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testWriteJSONSubstanceStudies() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(0, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	Assert.assertEquals(0, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("substance.json");

	    write(null, parser, c.getConnection(), new ReferenceSubstanceUUID(), false, true, true);
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(104, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance_experiment");
	    Assert.assertEquals(110, substance.getRowCount());
	} finally {
	    c.close();
	}
    }

    @Test
    public void testDeleteEffects() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
	IDatabaseConnection c = getConnection();
	ITable substance = c
		.createQueryTable("EXPECTED", "SELECT * FROM bundle_substance_experiment where deleted = 0");
	Assert.assertEquals(1, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	Assert.assertEquals(1, substance.getRowCount());
	substance = c.createQueryTable("EXPECTED",
		"SELECT * FROM bundle_substance_protocolapplication where deleted = 0");
	Assert.assertEquals(1, substance.getRowCount());
	try {

	    IRawReader<IStructureRecord> parser = getJSONReader("effects_delete.json");
	    SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	    delete_by_writer(bundle, parser, c.getConnection());
	    parser.close();

	    c = getConnection();
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM substance");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM bundle_substance_protocolapplication");
	    Assert.assertEquals(1, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED", "SELECT * FROM bundle_substance_experiment where deleted = 0");
	    Assert.assertEquals(0, substance.getRowCount());
	    substance = c.createQueryTable("EXPECTED",
		    "SELECT idbundle,idresult,remarks FROM bundle_substance_experiment where deleted = 1");
	    Assert.assertEquals(1, substance.getRowCount());
	    Assert.assertEquals("the reason to delete", substance.getValue(0, "remarks"));
	} finally {
	    c.close();
	}
    }

    public int delete_by_writer(SubstanceEndpointsBundle bundle, IRawReader<IStructureRecord> reader,
	    Connection connection) throws Exception {
	DBMatrixValueMarker writer;
	writer = new DBMatrixValueMarker(bundle);
	writer.setConnection(connection);
	writer.open();
	int records = 0;
	while (reader.hasNext()) {
	    Object record = reader.next();
	    if (record == null)
		continue;
	    Assert.assertTrue(record instanceof IStructureRecord);
	    writer.process((IStructureRecord) record);
	    records++;
	}
	writer.close();
	return records;
    }

    public int delete(SubstanceEndpointsBundle bundle, IRawReader<IStructureRecord> reader, Connection connection)
	    throws Exception {
	/*
	 * DBSubstanceWriter writer; if (bundle != null) writer = new
	 * DBBundleStudyWriter(bundle, DBSubstanceWriter.datasetMeta(), new
	 * SubstanceRecord()); else writer = new
	 * DBSubstanceWriter(DBSubstanceWriter.datasetMeta(), new
	 * SubstanceRecord(), clearMeasurements, clearComposition);
	 * writer.setSplitRecord(splitRecord); writer.setConnection(connection);
	 * writer.open();
	 */
	UpdateExecutor<IQueryUpdate> writer = new UpdateExecutor<IQueryUpdate>();
	writer.setConnection(connection);
	DeleteMatrixValue q = new DeleteMatrixValue();
	q.setGroup(bundle);
	int records = 0;
	while (reader.hasNext()) {
	    Object record = reader.next();
	    if (record == null)
		continue;
	    Assert.assertTrue(record instanceof SubstanceRecord);
	    Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
		    ((SubstanceRecord) record).getCompanyUUID());
	    for (ProtocolApplication pa : ((SubstanceRecord) record).getMeasurements()) {
		Assert.assertTrue(pa instanceof ProtocolApplicationAnnotated);
		System.out.println(((ProtocolApplicationAnnotated) pa).getRecords_to_delete());

		ProtocolApplicationAnnotated paa = (ProtocolApplicationAnnotated) pa;
		List<ValueAnnotated> vaa = paa.getRecords_to_delete();
		for (ValueAnnotated va : vaa) {
		    q.setObject(va);
		    writer.process(q);
		}
	    }

	    // writer.setImportedRecord((SubstanceRecord) record);
	    // writer.process((IStructureRecord) record);
	    records++;
	}

	writer.close();
	return records;
    }
}
