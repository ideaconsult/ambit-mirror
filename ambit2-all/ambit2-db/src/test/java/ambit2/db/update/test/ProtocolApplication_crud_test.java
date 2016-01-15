package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.test.ProtocolApplicationTestFactory;
import ambit2.db.substance.study.UpdateSubstanceStudy;

public class ProtocolApplication_crud_test extends CRUDTest<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> {

	static ProtocolApplication<Protocol, IParams, String, IParams, String> papp = ProtocolApplicationTestFactory.initpa();
	
	

	@Override
	protected IQueryUpdate<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> updateQuery()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp);
	}

	@Override
	protected void updateVerify(IQueryUpdate<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid) su,params,reference from substance_protocolapplication where document_prefix='IUC4' and document_UUID=unhex('7ADB0D03F69B32A99EFE86B4A8577893')");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(papp.getDocumentUUID(),
				I5Utils.getPrefixedUUID(
						table.getValue(0,"document_prefix"),table.getValue(0,"u")));
		Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
				I5Utils.getPrefixedUUID(
						table.getValue(0,"substance_prefix"),table.getValue(0,"su")));
		Assert.assertEquals(papp.getProtocol().getEndpoint(),table.getValue(0,"endpoint"));
		Assert.assertEquals(papp.getProtocol().getCategory(),table.getValue(0,"endpointcategory"));
		Assert.assertEquals(papp.getProtocol().getGuideline().get(0),table.getValue(0,"guidance"));
		Assert.assertEquals(papp.getReference(),table.getValue(0,"reference"));
		Assert.assertEquals(papp.getParameters().toString(),table.getValue(0,"params"));
		c.close();

	}
/*
	


	 {
"uuid":	"IUC4-ae64fc3b-22a4-3173-9362-9cce1ff622ae/0",
"protocol":	{"category":"TO_ACUTE_ORAL_SECTION","endpoint":"Acute toxicity: oral, IUC4#2/Ch.5.1.1","guidance": ["Method: other: no data"]},
"parameters":	{"Test Organism":"guinea pig","Reference":"Smyth, H. F. Seaton J., and Fischer L. (1941).|The single dose toxicity of some glycols and derivatives.|J. Ind. Hyg. Toxicol. 23, 259-268","Sex":"male/female"},
"effects":	[{
"endpoint":	"LD50",
"conditions":	{"Sex":"male/female"},
"result":	{
	"unit":	"mg/kg bw",
	"loValue":	260.0
	}
}]
}
	 
 */

	@Override
	public void testDelete() throws Exception {
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> createQuery()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",ProtocolApplicationTestFactory.initpc());
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> createQueryNew()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",ProtocolApplicationTestFactory.initacutetox());
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
