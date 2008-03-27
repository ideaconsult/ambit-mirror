package ambit2.database.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.smiles.SmilesParserWrapper;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.core.DbSQL;

/**
 * Exact database search. <b>Example</b>: looks for benzene and writes the result in SDF file. 
 * <pre>
 * 	public void testExactSearch() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbExactSearchReader reader = new DbExactSearchReader(
					conn,
					MoleculeFactory.makeBenzene(),
					null,
					0,100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbExactSearchReader.sdf"));
			
			IAmbitProcessor processor = new DefaultAmbitProcessor();

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);
			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertTrue(rr>0);
			assertTrue(rr<100);
			assertEquals(rr,rp);
			assertTrue(rw <= rr);
			assertTrue(rw > 0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}

 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbExactSearchReader extends DbSearchReader {
	protected SmilesParserWrapper parser = null;
	protected SmilesGenerator generator = null;

	/**
	 * 
	 * @param connection
	 * @param mol  Molecule to search for
	 * @param srcDataset {@link SourceDataset} to search within, if null searches the entire database
	 * @param page
	 * @param pagesize
	 * @throws AmbitException
	 */
	public DbExactSearchReader(Connection connection, IAtomContainer mol,
			SourceDataset srcDataset,  int page, int pagesize) throws AmbitException {
		super(connection, mol,  srcDataset,0, page,pagesize);
		parser = SmilesParserWrapper.getInstance();
	}



	public Object next() {
		try {
			String smiles = resultset.getString("smiles");
			IMolecule mol = parser.parseSmiles(smiles);
			if (mol != null) {
				mol.setProperty(AmbitCONSTANTS.FORMULA, getFormula());
				mol.setProperty(AmbitCONSTANTS.SMILES, getSmiles());
                setPropertyStructure(mol);
                setPropertySubstance(mol);
				Object c = getCAS();
				if (c!=null) {
					mol.setProperty(CDKConstants.CASRN,c.toString());
				}	
				c = resultset.getObject("chemname");
				if (c!=null)				
					mol.setProperty(CDKConstants.NAMES, c.toString());
				c = resultset.getObject("dataset");
				if (c!=null)				
					mol.setProperty(AmbitCONSTANTS.DATASET, resultset.getObject("dataset"));
				
				return mol;
			} else return null;
		} catch (SQLException x) {
			logger.error(x);
			return null;
		} catch (Exception x) {
			logger.error(x);
			return null;
		}
	}
	
	
	protected PreparedStatement prepareSQLStatement(Connection conn,
			IAtomContainer mol,int page, int pagesize, double threshold) throws AmbitException {
		
		Object s = mol.getProperty(AmbitCONSTANTS.SMILES);
		if (s == null) { 
			if (generator == null) generator = new SmilesGenerator();
			try {
				
				IMolecule m = (IMolecule )mol.clone();
				MFAnalyser mfa = new MFAnalyser(m);
				m = (IMolecule)mfa.removeHydrogensPreserveMultiplyBonded();
				s = generator.createSMILES(m);
				if ("".equals(s)) {}
				else {
					m = null;
					mol.setProperty(AmbitCONSTANTS.SMILES,s);
				}
			} catch (Exception x) {
			    
			}
		} 
		
		List<String> parameters = new ArrayList<String>();
		String sql = DbSQL.getExactSearchSQL(mol.getProperties(), page, pagesize, threshold, parameters);
		
		if ((sql != null) && !sql.equals("")) {
			PreparedStatement ps;
			try {
				ps = conn.prepareStatement(sql);
				for (int i = 0; i < parameters.size(); i++) {

					ps.setString(i+1,parameters.get(i).toString());
				}	
			} catch (SQLException x) {
				throw new AmbitException(x);
			}
			System.out.println(ps.toString());
			return ps;
		}else return null;
		
	
	}
	/*
	 * 	protected PreparedStatement prepareSQLStatement(Connection conn,
			IAtomContainer mol,int page, int pagesize, double threshold) throws AmbitException {
		String[][] conditions={
				{CDKConstants.CASRN,"casno"},
				{AmbitCONSTANTS.FORMULA,"formula"},
				{CDKConstants.NAMES,"name.name"}
				};
		boolean hasQuery = false;
		CompoundsList results = null;
		StringBuffer condition = new StringBuffer();
		List c = new ArrayList();  
		
		for (int i=0;i<conditions.length;i++) {
			Object s = mol.getProperty(conditions[i][0]);
			if (s != null) {
					if (hasQuery) condition.append(" and ");
					hasQuery = true; 
					c.add(s.toString()); 
					condition.append(conditions[i][1]);
					condition.append("=?");
			};			
		}

		Object s = mol.getProperty(AmbitCONSTANTS.SMILES);
		if (s != null) { 
			if (hasQuery) condition.append(" and ");
			hasQuery = true; c.add(s.toString()); 
			condition.append(" smiles=?"); }
		else {
			if (generator == null) generator = new SmilesGenerator();
			try {
				
				IMolecule m = (IMolecule )mol.clone();
				MFAnalyser mfa = new MFAnalyser(m);
				m = (IMolecule)mfa.removeHydrogensPreserveMultiplyBonded();
				s = generator.createSMILES(m);
				if ("".equals(s)) {}
				else {
					m = null;
					mol.setProperty(AmbitCONSTANTS.SMILES,s);
					
					if (hasQuery) condition.append(" and ");
					hasQuery = true;
					c.add(s);
					condition.append("smiles=?");
				}
			} catch (Exception x) {
			    
			}
		}

		if (hasQuery) {
			
			ResultSet rs = null;
			PreparedStatement ps;
			
	
			StringBuffer pss = new StringBuffer();
			pss.append("SELECT s.idsubstance,c.idstructure,casno,formula,smiles,name.name as chemname,r.name as dataset \n"); 
			pss.append("FROM  substance as s\n");
			pss.append("LEFT join structure as c using (idsubstance) \n");			
			pss.append("LEFT join cas using (idstructure) \n"); 
			pss.append("LEFT JOIN name using (idstructure) \n"); 
			pss.append("LEFT JOIN struc_dataset using (idstructure) \n");
			pss.append("LEFT JOIN src_dataset as r using (id_srcdataset) \n");
			pss.append("WHERE ");
			pss.append(condition.toString());
//			String pss = "select s.idsubstance,casno,formula,smiles,name,id_srcdataset from substance as s left join structure as c using (idsubstance)  left join struc_dataset using(idstructure) left join cas using(idstructure) left join name using(idstructure) where "+
			//"SELECT substance.idsubstance,casno as CAS_RN,formula as Formula,molweight as MolWeight,name.name as ChemName,smiles as SMILES,type_structure,c.idstructure FROM structure as c, substance left join cas on (cas.idstructure=c.idstructure) LEFT JOIN name on (name.idstructure=c.idstructure) WHERE name.name = "phenol" and substance.idsubstance=c.idsubstance group by idsubstance order by type_structure desc limit 500;"			
			pss.append(" group by idsubstance order by type_structure limit ");
			pss.append(page);
			pss.append(",");
			pss.append(pagesize);
			
			
			try {
				ps = conn.prepareStatement(pss.toString());
				for (int i = 0; i < c.size(); i++) {

					ps.setString(i+1,c.get(i).toString());
				}	
			} catch (SQLException x) {
				throw new AmbitException(x);
			}
			System.out.println(ps.toString());
			return ps;
		}else return null;
		
	
	}

	 */
	
/*
 * (non-Javadoc)
 * @see ambit2.database.readers.DbReader#toString()
 */	
	@Override
	public String toString() {
		return "exact structure";
	}
}
