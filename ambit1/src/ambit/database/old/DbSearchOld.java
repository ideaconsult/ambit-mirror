/**
 * Created on 2004-12-8
 *
 */
package ambit.database.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;

import ambit.benchmark.Benchmark;
import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeConvertor;
import ambit.database.core.DbSQL;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;




/**
 * Search structures and substructures
 * @author Nina Jeliazkova <br>
 * @deprecated Use {@link ambit.database.processors.SubstructureSearchProcessor}
 * <b>Modified</b> 2005-4-7
 */
public class DbSearchOld {
	private Connection conn = null;
	
	private PreparedStatement psSmiles = null; 
	private MoleculeConvertor mc = new MoleculeConvertor();
	/**
	 * @deprecated
	 */
	
	public DbSearchOld(Connection conn) {
		this.conn = conn;
	}
	
	public void prepareExactSearch(boolean updatable)  throws SQLException {
		if (psSmiles == null) 
			if (updatable) {
				psSmiles = conn.prepareStatement(DbSQL.AMBIT_ExactSearchBySmiles,
				ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);				
			} else	
			psSmiles = conn.prepareStatement(DbSQL.AMBIT_ExactSearchBySmiles);
	}
	
	public int exactSearch(IMolecule mol, Integer id, boolean update) throws SQLException {

		ResultSet rs = null;
		String smiles = "";
		mc.setMolecule(mol);
		mc.createGamut();
		if (mc.getCyclicBonds() < 37) {
			smiles = mc.createSMILES();
			psSmiles.setString(1,smiles);
			rs = psSmiles.executeQuery();
		} else {
			
		}
		
		int rows = 0;
		
		
		
		while (rs.next()) {
			id = new Integer(rs.getInt(1));
			rows ++;
			System.out.println("ID "+id.intValue() + "\trows="+rows);			
		}
		rs.close();

		return rows;
	}
	
	public String getSMILESbyCAS(String casno) throws SQLException {
		PreparedStatement ps;
		ps = conn.prepareStatement(
			"SELECT distinct(smiles) FROM `substance` join structure using (idsubstance) join cas using(idstructure) where casno=? order by usmiles desc;");
		ps.setString(1,casno);		
		ResultSet rs = ps.executeQuery();
		String smiles = "NA";
		while (rs.next()) {
			smiles = rs.getString(1);
			break;
		}
		rs.close();
		rs=null;
		return smiles;
	}
	
	public ResultSet lookup(IMolecule mol) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps;
		String cas="", smiles="",name="",formula="";
		boolean hasQuery = false;
		
		Object obj = mol.getProperty(AmbitCONSTANTS.CASRN);
		if (obj == null) { hasQuery = true; cas = (String) obj; }  
		if (hasQuery) {
			ps = conn.prepareStatement(
			"SELECT distinct(smiles) FROM `substance` join structure using (idsubstance) join cas using(idstructure) where casno=? order by usmiles desc;"
				);
			ps.setString(1,cas);		
			rs = ps.executeQuery();
			if (!rs.next()) { 
				rs.close(); rs = null;	
			}
		}	
		return rs;
	}
	
	public ISetOfMolecules searchBy(IMolecule mol) throws SQLException {
		boolean hasQuery = false;
		ISetOfMolecules sm = null;
		StringBuffer condition = new StringBuffer();
		List c = new ArrayList();  
		
		Object obj = mol.getProperty(AmbitCONSTANTS.CASRN);
		if ((obj != null) && (!obj.equals(""))) { hasQuery = true; c.add(obj); 
				condition.append(" and casno=?"); };

		obj = mol.getProperty(AmbitCONSTANTS.FORMULA);
		if ((obj != null) && (!obj.equals(""))) { hasQuery = true; c.add(obj);
			condition.append(" and formula=?"); };
		
		obj = mol.getProperty(AmbitCONSTANTS.SMILES);
		if ((obj != null) && (!obj.equals(""))) { hasQuery = true; c.add(obj);
			condition.append(" and smiles=?"); };
		
		obj = mol.getProperty(AmbitCONSTANTS.NAMES);
		if ((obj != null) && (!obj.equals(""))) { hasQuery = true; c.add(obj);
			condition.append(" and name.name=?"); };

		
		
		if (hasQuery) {
			
			ResultSet rs = null;
			PreparedStatement ps;
			
			StringBuffer pss = new StringBuffer();
			pss.append("SELECT substance.idsubstance,casno,formula,smiles,name.name,r.name "); 
			pss.append("FROM  structure as c, substance left join cas on (cas.idstructure=c.idstructure) "); 
			pss.append("LEFT JOIN name on (name.idstructure=c.idstructure) "); 
			pss.append("LEFT JOIN struc_dataset on (struc_dataset.idstructure=c.idstructure) ");			
			pss.append("LEFT JOIN src_dataset as r on (struc_dataset.id_srcdataset=r.id_srcdataset) ");			
			pss.append("WHERE  substance.idsubstance=c.idsubstance ");
			pss.append(condition.toString());
//			String pss = "select s.idsubstance,casno,formula,smiles,name,id_srcdataset from substance as s left join structure as c using (idsubstance)  left join struc_dataset using(idstructure) left join cas using(idstructure) left join name using(idstructure) where "+
			pss.append("order by idsubstance limit 300;");
			
			ps = conn.prepareStatement(pss.toString());
			//logger.debug(r.println(ps);
			for (int i = 0; i < c.size(); i++) {
				System.err.println((String) c.get(i));				
				ps.setString(i+1,(String) c.get(i));
			}	
				/*
			if ((formula == null)  || (formula.equals(""))) 
				ps.setNull(2,java.sql.Types.CHAR); else ps.setString(2,formula);
			if ((smiles == null)   || (smiles.equals("")))
				ps.setNull(3,java.sql.Types.CHAR); else ps.setString(3,smiles);
			if ((name == null)  || (name.equals("")))	
				ps.setNull(4,java.sql.Types.CHAR); else ps.setString(4,name);
					*/	
			try {
				rs = ps.executeQuery();
				sm = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
				String s;
				while( rs.next() ) {
					IMolecule m = DefaultChemObjectBuilder.getInstance().newMolecule();
					s = rs.getString(2);
					if (s != null)	m.setProperty(AmbitCONSTANTS.CASRN,s);
					s = rs.getString(3);
					if (s != null)	m.setProperty(AmbitCONSTANTS.FORMULA,s);
					s = rs.getString(4);
					if (s != null)	m.setProperty(AmbitCONSTANTS.SMILES,s);
					s = rs.getString(5);					
					if (s != null)	m.setProperty(AmbitCONSTANTS.NAMES,s);					
					s = rs.getString(6);					
					if (s != null)	m.setProperty(AmbitCONSTANTS.DATASET,s);					
					

					sm.addMolecule(m);
				}
				rs.close();
			} catch (SQLException e) {
				rs.close();
			}
			rs = null;
		}	
		return sm;
	
	}
	
	public static void main(String[] args) {
		
		DbExport dbe = new DbExport("ambit");
		
		Benchmark b = new Benchmark();
		b.mark();
		try {
			dbe.open();
			DbSearchOld dbs = new DbSearchOld(dbe.getConn() );	
			Statement stmt = dbe.createBidirectional();
			ResultSet rs = dbe.getStructures(stmt,10);
			//dbs.prepareExactSearchByFP();			
			
			IMolecule mol;
			int idsubstance;
			Integer id = new Integer(0);
			int rows = 0;
			int found = 0;
			int n = 0;
			MoleculeConvertor mc = new MoleculeConvertor();
			while (rs.next()) {
				idsubstance = rs.getInt(1);
				try {
					mol = Compound.readMolecule(rs.getString(3));
					
					mc.setMolecule(mol);
					mc.createGamut();
					if (mc.getCyclicBonds() < 37) {
					
						//n = dbs.exactSearchByFP(mol,id);
						if ((n == 1) & (id.intValue() == idsubstance)) {
							found ++;
						} else {
							System.out.println("ID="+idsubstance+"\tnot found "+n);
						}
					}	
					rows++;
				} catch (CDKException ce) {
					ce.printStackTrace();
				}
			}
			dbe.closeStatement(stmt);
			dbs = null;
			dbe.close();
			dbe= null;
		} catch (AmbitException x) {
			x.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		b.record();
		b.report();
	}
}
