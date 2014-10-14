package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.rest.AbstractResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;

public class SmartsResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query/smarts?search=%s", port,
				Reference.encode("c1ccccc1"));
	}
	
	public String getTestURIKekuleSMILES() {
		return String.format("http://localhost:%d/query/smarts?search=%s", port,
				Reference.encode("C1=CC=CC=C1"));
	}	
	
	@Test
	public void testSearchByMol() throws Exception {
		
		IAtomContainer ac = MoleculeFactory.makeBenzene();
		StringWriter w = new StringWriter();
		MDLV2000Writer writer = new MDLV2000Writer(w);
		writer.write(ac);
		writer.close();

		String query = String.format("http://localhost:%d/query/smarts?type=mol&%s=%s", port,
				AbstractResource.b64search_param,
				Base64.encode(w.getBuffer().toString().getBytes("UTF-8"),false));
		testGet(query,ChemicalMediaType.CHEMICAL_MDLSDF);
	}	
	
	

	public void testDepictByMol() throws Exception {
		
		IAtomContainer ac = MoleculeFactory.makeBenzene();
		StringWriter w = new StringWriter();
		MDLV2000Writer writer = new MDLV2000Writer(w);
		writer.write(ac);
		writer.close();
		System.out.println(Base64.encode(w.getBuffer().toString().getBytes("UTF-8"),false));
		//DQogIENESyAgICAgMDEwNjEzMjA0Mg0KDQogIDYgIDYgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDA5OTkgVjIwMDANCiAgICAwLjAwMDAgICAgMC4wMDAwICAgIDAuMDAwMCBDICAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMA0KICAgIDAuMDAwMCAgICAwLjAwMDAgICAgMC4wMDAwIEMgICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwDQogICAgMC4wMDAwICAgIDAuMDAwMCAgICAwLjAwMDAgQyAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDANCiAgICAwLjAwMDAgICAgMC4wMDAwICAgIDAuMDAwMCBDICAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMA0KICAgIDAuMDAwMCAgICAwLjAwMDAgICAgMC4wMDAwIEMgICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwDQogICAgMC4wMDAwICAgIDAuMDAwMCAgICAwLjAwMDAgQyAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDANCiAgMSAgMiAgMSAgMCAgMCAgMCAgMCANCiAgMiAgMyAgMiAgMCAgMCAgMCAgMCANCiAgMyAgNCAgMSAgMCAgMCAgMCAgMCANCiAgNCAgNSAgMiAgMCAgMCAgMCAgMCANCiAgNSAgNiAgMSAgMCAgMCAgMCAgMCANCiAgNiAgMSAgMiAgMCAgMCAgMCAgMCANCk0gIEVORA0K
		String query = String.format("http://localhost:%d/depict/cdk?type=mol&%s=%s", port,
				AbstractResource.b64search_param,
				Base64.encode(w.getBuffer().toString().getBytes("UTF-8"),false));
		testGet(query,MediaType.IMAGE_PNG);
	}	
	
	@Test
	public void testSDFKekuleSmiles() throws Exception {
		testGet(getTestURIKekuleSMILES(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}	
	
	@Test
	public void testSDF() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}	
	@Override
	public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
			throws Exception {
			IteratingMDLReader reader = new IteratingMDLReader(in, SilentChemObjectBuilder.getInstance());
			int count = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				Assert.assertTrue(o instanceof IAtomContainer);
				IAtomContainer mol = (IAtomContainer)o;
				Assert.assertEquals(22,mol.getAtomCount());
				Assert.assertEquals(23,mol.getBondCount());
				count++;
			}
			return count==1;
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(String.format("http://localhost:%d/compound/11/conformer/100215",port),line);
			count++;
		}
		return count ==1;
	}
	
	
	
	public static void main(String[] args) {
		
		String uris[] = {
				//"P(=O)([!$([OH1,SH1])])(O([$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$([CH2]c1ccccc1)]))O([$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$(C([#1,Cl,Br,I,F])(C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F]))C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])C([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])([#1,Cl,Br,I,F])),$([CH2]c1ccccc1)])",
				//113 -200
				//"[c;!$(c[N+](=O)[O-]);!$(c[N]([#1,C])([#1,C]));!$(cN([OX2H])([#1,C]));!$(cN([#1,C])OC=O);!$(c[NX3v3]([#1,CH3])([#1,CH3]));!$(c[NX3v3]([#1,CH3])([CH2][CH3]));!$(c[NX3v3]([CH2][CH3])([CH2][CH3]));!$(cNC(=O)[#1,CH3]);!$(cN=[N]a);!$(c!@[cR1r6]1ccccc1);!$(c!@*!@c1ccccc1);!$([R2])]1[c;!$(c[N+](=O)[O-]);!$(c[N]([#1,C])([#1,C]));!$(cN([OX2H])([#1,C]));!$(cN([#1,C])OC=O);!$(c[NX3v3]([#1,CH3])([#1,CH3]));!$(c[NX3v3]([#1,CH3])([CH2][CH3]));!$(c[NX3v3]([CH2][CH3])([CH2][CH3]));!$(cNC(=O)[#1,CH3]);!$(cN=[N]a);!$(c!@[cR1r6]1ccccc1);!$(c!@*!@c1ccccc1);!$([R2])][c;!$(c[N+](=O)[O-]);!$(c[N]([#1,C])([#1,C]));!$(cN([OX2H])([#1,C]));!$(cN([#1,C])OC=O);!$(c[NX3v3]([#1,CH3])([#1,CH3]));!$(c[NX3v3]([#1,CH3])([CH2][CH3]));!$(c[NX3v3]([CH2][CH3])([CH2][CH3]));!$(cNC(=O)[#1,CH3]);!$(cN=[N]a);!$(c!@[cR1r6]1ccccc1);!$(c!@*!@c1ccccc1);!$([R2])][c;!$(c[N+](=O)[O-]);!$(c[N]([#1,C])([#1,C]));!$(cN([OX2H])([#1,C]));!$(cN([#1,C])OC=O);!$(c[NX3v3]([#1,CH3])([#1,CH3]));!$(c[NX3v3]([#1,CH3])([CH2][CH3]));!$(c[NX3v3]([CH2][CH3])([CH2][CH3]));!$(cNC(=O)[#1,CH3]);!$(cN=[N]a);!$(c!@[cR1r6]1ccccc1);!$(c!@*!@c1ccccc1);!$([R2])][c;!$(c[N+](=O)[O-]);!$(c[N]([#1,C])([#1,C]));!$(cN([OX2H])([#1,C]));!$(cN([#1,C])OC=O);!$(c[NX3v3]([#1,CH3])([#1,CH3]));!$(c[NX3v3]([#1,CH3])([CH2][CH3]));!$(c[NX3v3]([CH2][CH3])([CH2][CH3]));!$(cNC(=O)[#1,CH3]);!$(cN=[N]a);!$(c!@[cR1r6]1ccccc1);!$(c!@*!@c1ccccc1);!$([R2])]c1([Cl,Br,F,I;!$([Cl,Br,I,F]cc[Cl,Br,I,F]);!$([Cl,Br,I,F]ccc[Cl,Br,I,F]);!$([Cl,Br,I,F]c1c([OX2H])c([OX2H])c([OX2H])cc1);!$([Cl,Br,I,F]c1c([OX2H])c([OX2H])cc([OX2H])c1);!$([Cl,Br,I,F]c1c([OX2H])c([OX2H])ccc1([OX2H]));!$([Cl,Br,I,F]c1c([OX2H])cc([OX2H])c([OX2H])c1);!$([Cl,Br,I,F]c1c([OX2H])cc([OX2H])cc1([OX2H]));!$([Cl,Br,I,F]c1cc([OX2H])c([OX2H])c([OX2H])c1)])",
				// 120 - 600
				//"[!a,#1;!$(C1(=O)C=CC(=O)C=C1)][#6]([!a,#1;!$(C1(=O)C=CC(=O)C=C1)])!:;=[#6][#6](=O)[!O;!$([#6]1:,=[#6][#6](=O)[#6]:,=[#6][#6](=O)1)]",
				//112 - 8100
				//"[#7X3][#6](=[SX1])[!$([O,S][CX4])!$([OH,SH])!$([O-,S-])]", //900
				//"[CX4H2](N)([OX2H1])", //62 - 17603
				//"C!@C!@C!@C!@C!@C", //2800
				//"ccccn", //1020
				"c1ccccc1",
				//"a[N]=[N]a"
		};
		
		int psize [] = { 
				//1,
				//542,
				//7450,
				//2200,
				//34500,
				//2733,
				//1001,
				100//103,//110,
				//100000
				
		};
		
		/*
		int psize1000 [] = {
				//1650,
				//3200,
				//29300,
				//13500,
				//??10500,
				//22100,
				//9200,
				1000,
				//100000
				
		};
		*/
		long counts[] = {
				100,
				100,
				100,
				100,
				100,
				100,
				100,
				100
		};		
		
		long times[] = {
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0
		};				
		int count = 0;
		long time = 0;
		int max=1000;
		//String screening ="&fp1024=false&sk1024=false";
		String screening = "";
		for (int i=0; i < uris.length;i++) {
			int pagesize=psize[i];
			count = 0;
			while (count < max)  {
				
				ClientResource r = new ClientResource(String.format(
						//"http://192.168.1.2:8080/ambit2-emolecules/query/smarts?search=%s&max=%d%s",
						"http://localhost:8080/ambit2/query/smarts?search=%s&max=%d%s",
						Reference.encode(uris[i]),pagesize,screening));
				Representation p = null;
				try {
					count = 0;
					time = System.currentTimeMillis();
					p = r.get(MediaType.TEXT_URI_LIST);
					time = System.currentTimeMillis()-time;
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getStream()));
					String line;
					while ((line = reader.readLine())!= null) {
						count++;
					}
					try {reader.close(); } catch (Exception x) {}
					
				} catch (Exception x) {
					System.err.println(x.getMessage());
				} finally {
					
					if (p != null) p.release();
					if (r != null) r.release();
				}
				
				if (count < max) {
					System.out.println(count);
					pagesize = pagesize + 100;
				}
				else {
				
					counts[i]=pagesize;
					System.out.println(String.format("%d.\t%d\t%d ms\t%d",(i+1),count,time,pagesize));
					break;
				}
				
			
				counts[i]=pagesize;
				System.out.println(String.format("%d.\t%d\t%d ms\t%d",(i+1),count,time,pagesize));
				break;
				
				

			}
		}
	}
}
