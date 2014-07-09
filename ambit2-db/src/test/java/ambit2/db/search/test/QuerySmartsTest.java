package ambit2.db.search.test;

import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.db.SessionID;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.ProcessorCreateSession;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FunctionalGroup;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;

public class QuerySmartsTest extends QueryTest<QuerySMARTS> {

	@Override
	protected QuerySMARTS createQuery() throws Exception {
		QuerySMARTS q = new QuerySMARTS();
		q.setValue(new FunctionalGroup("aromatic aldehyde - example","c1ccccc1[$(C(C)C(=O)),$(CC(C)C(=O))]",""));
		q.setId(999);
		q.setChemicalsOnly(false);
		return q;
	}

	@Test
	public void testSelectChemicals() throws Exception {
		Preferences.setProperty(Preferences.FASTSMARTS,"true");		
		setDbFile("src/test/resources/ambit2/db/processors/test/smarts-dataset.xml");	
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		
		ITable fp = c.createQueryTable("EXPECTED","select * from structure");
		Assert.assertEquals(4, fp.getRowCount());
		
		ProcessorCreateSession p = new ProcessorCreateSession();
		p.setConnection(c.getConnection());
		SessionID id = p.process(null);
		c = getConnection();
		ProcessorCreateQuery q = new ProcessorCreateQuery();
		q.setConnection(c.getConnection());
		q.open();
		q.setSession(id);

		query.setChemicalsOnly(true);
		IStoredQuery sq = q.process(query);
		p.close();
		q.close();
		c.close();

		c = getConnection();
		fp = c.createQueryTable("EXPECTED_query","select * from query where idquery="+sq.getId());
		Assert.assertEquals(1, fp.getRowCount());
		fp = c.createQueryTable("EXPECTED_query","select idchemical,idstructure from query_results where idquery="+sq.getId());
		Assert.assertEquals(1, fp.getRowCount());
		Assert.assertEquals(new BigInteger("2050"),fp.getValue(0,"idchemical"));
		
		c.close();
	}
	
	@Test
	public void testSelectStructures() throws Exception {
		Preferences.setProperty(Preferences.FASTSMARTS,"true");		
		setDbFile("src/test/resources/ambit2/db/processors/test/smarts-dataset.xml");	
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		
		ITable fp = c.createQueryTable("EXPECTED","select * from structure");
		Assert.assertEquals(4, fp.getRowCount());
		
		ProcessorCreateSession p = new ProcessorCreateSession();
		p.setConnection(c.getConnection());
		SessionID id = p.process(null);
		c = getConnection();
		ProcessorCreateQuery q = new ProcessorCreateQuery();
		q.setConnection(c.getConnection());
		q.open();
		q.setSession(id);

		query.setChemicalsOnly(false);
		IStoredQuery sq = q.process(query);
		p.close();
		q.close();
		c.close();

		c = getConnection();
		fp = c.createQueryTable("EXPECTED_query","select * from query where idquery="+sq.getId());
		Assert.assertEquals(1, fp.getRowCount());
		fp = c.createQueryTable("EXPECTED_query","select idchemical,idstructure from query_results where idquery="+sq.getId());
		//Assert.assertEquals(4, fp.getRowCount());
		//Assert.assertEquals(2050,fp.getValue(0,"idchemical"));
		
		c.close();
	}	
	@Override
	protected void verify(QuerySMARTS query, ResultSet rs) throws Exception {
		while (rs.next()) {
			/*
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(10,rs.getInt(2));
			Assert.assertEquals(100214,rs.getInt(3));
			Assert.assertEquals(1,rs.getInt(4));
			Assert.assertEquals(0.25,rs.getFloat(5),1E-4);	
			Assert.assertEquals(0.25,rs.getFloat("metric"),1E-4);
			*/
			IStructureRecord r = query.getObject(rs);
		}
		
	}

	@Test
	public void testMatchNoAccelerator() throws Exception {
		Preferences.setProperty(Preferences.FASTSMARTS,"false"); 
		QuerySMARTS q = new QuerySMARTS();
		q.setValue(new FunctionalGroup("aromatic aldehyde example","c1ccccc1[$(C(C)C(=O)),$(CC(C)C(=O))]",""));
				//"c1ccccc1C(C)C(=O)",""));
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(
			getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/smartssearch.sdf")	
			));
		int count = 0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			Assert.assertEquals(1.0,q.calculateMetric(record));
			count++;
		}
		Assert.assertEquals(2,count);
	}
	@Test
	public void testMatchWithAccelerator() throws Exception {
		Preferences.setProperty(Preferences.FASTSMARTS,"true");
		QuerySMARTS q = new QuerySMARTS();
		q.setValue(new FunctionalGroup("aromatic aldehyde example","c1ccccc1[$(C(C)C(=O)),$(CC(C)C(=O))]",""));
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(
			getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/smartssearch.sdf")	
			));
		int count = 0;
		SMARTSPropertiesGenerator gen = new SMARTSPropertiesGenerator();
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			try {
				gen.process(record);
			} catch (Exception x) {
				x.printStackTrace();
			}
			Assert.assertNotNull(record.getProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp)));
			
			/*
			record.setProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp),
			"0210,1430,1440,04310600,14310600,14310600,14310600,14310600,14310600,3440,0110,0110,0110,0110,0110,0110,0110,0110,0110,0110,\n0,0,0,0,0,0,1,1,1,0,1,0,1,0,1,0,0,0,0,0,");
			*/
			Assert.assertEquals(1.0,q.calculateMetric(record));
			count++;
		}
		Assert.assertEquals(2,count);
	}	
}
