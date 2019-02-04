package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.study.SubstanceStudyFlatQuery;
import ambit2.db.substance.study.SubstanceStudyFlatQuery._QUERY_TYPE;
import junit.framework.Assert;
import net.idea.modbcum.i.bucket.Bucket;

public class SubstanceStudyFlatQueryTest extends QueryTest<SubstanceStudyFlatQuery> {
	private static final String test_investigation = "AE64FC3B22A4317393629CCE1FF622AE";

	@Override
	protected SubstanceStudyFlatQuery createQuery() throws Exception {
		// String json = "{\"params\" : {\":all\" : { \"type\" : \"boolean\",
		// \"value\": false}, \":s_prefix\" : { \"type\" : \"String\",
		// \"value\": \"IUC4\"},\":s_uuid\" : { \"type\" : \"String\",
		// \"value\":\"EFDB21BBE79F3286A988B6F6944D3734\"} }}";
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceUUID("IUC4-EFDB21BBE79F3286A988B6F6944D3734");
		return new SubstanceStudyFlatQuery(record);
	}

	@Override
	protected void verify(SubstanceStudyFlatQuery query, ResultSet rs) throws Exception {
		browse(query, rs, 4);
	}

	protected void browse(SubstanceStudyFlatQuery query, ResultSet rs, int countexpected) throws Exception {
		int count = 0;
		while (rs.next()) {
			Bucket record = query.getObject(rs);
			// System.out.println(record.asJSON());
			for (String header : record.getHeader()) {

				if ("owner_name".equals(header))
					continue;
				if ("params".equals(header))
					continue;
				if ("interpretation_result".equals(header))
					continue;
				if ("interpretation_criteria".equals(header))
					continue;
				if (EffectRecord._fields.conditions.name().equals(header))
					continue;
				if (EffectRecord._fields.loQualifier.name().equals(header))
					continue;
				if (EffectRecord._fields.upQualifier.name().equals(header))
					continue;
				if (EffectRecord._fields.upValue.name().equals(header))
					continue;
				if (EffectRecord._fields.textValue.name().equals(header))
					continue;
				if ("document_prefix".equals(header))
					continue;
				if ("s_prefix".equals(header))
					continue;
				if ("errQualifier".equals(header))
					continue;
				if ("reference_year".equals(header))
					continue;
				if ("err".equals(header))
					continue;
				if ("hash".equals(header))
					continue;
				if ("content".equals(header))
					continue;
				if ("isRobustStudy".equals(header))
					continue;
				if ("isUsedforClassification".equals(header))
					continue;
				if ("isUsedforMSDS".equals(header))
					continue;
				if ("resulttype".equals(header))
					continue;
				if ("auuid".equals(header))
					continue;
				if ("resultgroup".equals(header))
					continue;								
				if ("iuuid".equals(header)) {
					if (record.get(header) != null)
						Assert.assertEquals(test_investigation, record.get(header));
					continue;
				}

				if (EffectRecord._fields.unit.name().equals(header))
					continue;
				Assert.assertNotNull(header, record.get(header));
			}
			count++;
		}
		Assert.assertEquals(countexpected, count);
	}

	@Test
	public void testSelectByInvestigation() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			// String json = "{\"params\" : {\":all\" : { \"type\" :
			// \"boolean\", \"value\": false}, \":s_prefix\" : { \"type\" :
			// \"String\", \"value\": \"IUC4\"},\":s_uuid\" : { \"type\" :
			// \"String\", \"value\":\"EFDB21BBE79F3286A988B6F6944D3734\"} }}";

			ProtocolApplication papp = new ProtocolApplication(null);
			String uuid = I5Utils.addDashes(test_investigation).toLowerCase();
			papp.setInvestigationUUID(test_investigation);

			Assert.assertEquals(uuid, papp.getInvestigationUUID().toString());
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(papp);

			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 2);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}

	@Test
	public void testSelectByEndpointcategory() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(p);

			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}
	
	@Test
	public void testSelectBySubstanceName_and_Study() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bysubstance_name,p,new String[] {
					"testname"
					});
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}		
	@Test
	public void testSelectBySubstanceType_and_Study() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bysubstance_type,p,new String[] {
					"Monoconstituent"
					});
			executor.setConnection(c.getConnection());
			executor.open();
			System.out.println(q.getSQL());
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}		
	
	@Test
	public void testSelectByStructure_names() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bystructure_name,p,new String[] {
					"123-45-6"
					});
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}	
	
	
	public void testSelectByStructure_smiles() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bystructure_smiles,p,new String[] {
					"O=C([O-])C(CC)CCCC.O=C([O-])C(CC)CCCC.[SnH4+2]","[F-].[Na+].F","[Br-].C=1C=CC(=CC1)[P+](C=2C=CC=CC2)(C=3C=CC=CC3)CC","[Fe]"
					});
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}	
	
	@Test
	public void testSelectByStructure_inchikey() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bystructure_inchikey,p,new String[] {
					"BFXAWOHHDUIALU-UHFFFAOYSA-M",
					"IKNSDPVLAJQDTH-UHFFFAOYSA-L",
					"JHYNXXDQQHTCHJ-UHFFFAOYSA-M",
					"XEEYBQQBJWHFJM-UHFFFAOYSA-N"
					});
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}	
	

	@Test
	public void testSelectByStructure_idchemical() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {

			Protocol p = new Protocol(null);
			p.setTopCategory("TOX");
			p.setCategory("TO_ACUTE_ORAL_SECTION");
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(p,new int[]{11});
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}
		
	

	@Test
	public void testSelectByReferenceOwner() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.byprovider,"Test Company 3");
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 1);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}
	
	@Test
	public void testSelectByReference() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			SubstanceStudyFlatQuery q = new SubstanceStudyFlatQuery(_QUERY_TYPE.bycitation,"reference");
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q);
			Assert.assertNotNull(rs);
			browse(q, rs, 3);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}
}
