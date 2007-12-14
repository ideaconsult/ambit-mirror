/**
 * Created on 2004-12-8
 *
 */
package ambit.database.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.benchmark.Benchmark;
import ambit.data.descriptors.AtomEnvironment;
import ambit.data.descriptors.AtomEnvironmentDescriptor;
import ambit.data.molecule.Compound;
import ambit.data.molecule.CompoundsList;
import ambit.data.molecule.MoleculeConvertor;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.old.DbExport;
import ambit.exceptions.AmbitException;

/**
 * @deprecated Use {@link ambit.database.search} instead.
 * Looks for {@link ambit.data.molecule.Compound}s in the database  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbSearchCompound extends DbCore {
	HydrogenAdder hAdder = null;
	protected AtomEnvironmentDescriptor aeDescriptor = null;
	protected Fingerprinter fingerprinter = null;
	protected Object[] aeParams = null;
	protected int[] aeResult = null;
	
	private PreparedStatement psSmiles = null; 
	private MoleculeConvertor mc = new MoleculeConvertor();
	private int pageSize = 200;
	private int page =1;
	
	protected SourceDataset srcDataset = null;
	/**
	 * 
	 */
	
	public DbSearchCompound(DbConnection conn) {
		super(conn);
		fingerprinter = new Fingerprinter(1024);
	}
	
	protected void initializeExactSearch(boolean updatable)  throws SQLException {
		if (psSmiles == null) 
			if (updatable) {
				psSmiles = conn.prepareStatement(DbSQL.AMBIT_ExactSearchBySmiles,
				ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);				
			} else	
			psSmiles = conn.prepareStatement(DbSQL.AMBIT_ExactSearchBySmiles);
	}
	public void close() throws SQLException {
		if (psSmiles != null) psSmiles.close(); 
	}	
	public int exactSearch(Compound mol, boolean update) throws SQLException {
		initializeExactSearch(false);
		ResultSet rs = null;
		mc.setMolecule(mol.getMolecule());
		mc.createGamut();
		if (mc.getCyclicBonds() < 37) {
			
			mol.updateMolecule();
			psSmiles.setString(1,mol.getSMILES());
			rs = psSmiles.executeQuery();
		} else {
			//T
		}
		
		int rows = 0;
		
		while (rs.next()) {
/**
 * idsubstance,formula,molweight,stype_id 
 */			
			mol.writeIdsubstance(rs.getInt(1));
			mol.setFormula(rs.getString(2));
			mol.getSubstanceType().setId(rs.getInt(4));
			rows ++;
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
	/*
	public ResultSet lookup(Molecule mol) throws SQLException {
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
	*/
	public PreparedStatement prepareAtomEnvironmentsPS(IMolecule mol,int page, int pagesize) throws SQLException {
		boolean hasQuery = (mol != null) &&
					(mol.getAtomCount() > 0)	;
		if (!hasQuery) return null; 		
		IAtomContainer newMol =null;
	    try {
			newMol = (AtomContainer)mol.clone();
			
		    if (hAdder == null) hAdder = new HydrogenAdder();	        
	        hAdder.addExplicitHydrogensToSatisfyValency(mol);
	    } catch (Exception x) {
	        return null;        
	    }
	    
		
	    MFAnalyser mfa = new MFAnalyser(newMol);
	    mfa.removeHydrogensPreserveMultiplyBonded();
	    
		try {
		    ArrayList aes = new ArrayList();

				String atomS, ae;
				if (aeDescriptor == null) {
					aeDescriptor = new AtomEnvironmentDescriptor();
					aeParams = new Object[1];
				}				
				for (int a = 0; a < newMol.getAtomCount(); a++) {
					aeParams[0] = new Integer(a);
					aeDescriptor.setParameters(aeParams);
					
					if (aeResult == null)
					    aeResult = new int[aeDescriptor.getAtomFingerprintSize()];
					
					aeDescriptor.doCalculation(newMol,aeResult);
					ae = AtomEnvironment.atomFingerprintToString(aeResult,'\t');
					atomS = aeDescriptor.atomTypeToString(aeResult[1]);
					if (aes.indexOf(ae) == -1)
					    aes.add(ae);
				}	

				StringBuffer b = new StringBuffer();
				b.append("select substance.idsubstance,formula,smiles,round(100*count(fpaeid.idsubstance)/");
				b.append(aes.size());
				b.append(",2) as similarity,molweight from substance join fpaeid using(idsubstance) join fpae using(idfpae) where ae in \n");
				b.append("");
				b.append("(\n");
				
				for (int i=0; i < aes.size();i++) {
					if (i>0) b.append(",");
					b.append("'");
					b.append(aes.get(i));
					b.append("'");
					b.append('\n');				    
				}
				    
				b.append(")");
				b.append(" group by idsubstance order by similarity desc limit ");
				b.append(page);
				b.append(",");
				b.append(pagesize);
				System.out.println(b.toString()); 


				
				return conn.prepareStatement(b.toString());

//			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public CompoundsList searchByAtomEnvironments(Compound mol) throws SQLException {
		CompoundsList results = null;
		ResultSet rs = null;
		if ((mol.getMolecule() != null) && (mol.getMolecule().getAtomCount()==0))
			mol.updateMolecule();
		PreparedStatement ps = prepareAtomEnvironmentsPS(mol.getMolecule(),page,pageSize);

		try {
				rs = ps.executeQuery();
				int aeCount ;
				while( rs.next() ) {
					if (results == null) {
						results = new CompoundsList();
						results.setEditable(false);
					}
									
					Compound molecule = new Compound();
					molecule.writeIdsubstance(rs.getInt(1));
					molecule.setId(rs.getInt(1));
						
					String s = rs.getString(2);
					if (s != null)	molecule.setFormula(s);
					s = rs.getString(3);
					if (s != null)	molecule.setSMILES(s);
						
					//s = rs.getString(6);
					aeCount = (int)rs.getDouble(4);
					molecule.setOrderInModel(aeCount);
				
					molecule.setNote(Integer.toString(aeCount));
						
					results.addItem(molecule);
				}
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			rs.close();
		}
			rs = null;
		return results;
	}
	
	public CompoundsList searchByTanimoto(Compound mol) throws SQLException {
		CompoundsList results = null;
		ResultSet rs = null;
		PreparedStatement ps = prepareTanimoto(mol.getMolecule(),page,pageSize);

		try {
			rs = ps.executeQuery();
			double tanimoto ;
			while( rs.next() ) {
				if (results == null) results = new CompoundsList();
				results.setEditable(false);			
				Compound molecule = new Compound();
				molecule.writeIdsubstance(rs.getInt(7));
				molecule.setId(rs.getInt(7));
				
				String s = rs.getString(6);
				if (s != null)	molecule.setFormula(s);
				s = rs.getString(5);
				if (s != null)	molecule.setSMILES(s);
				//s = rs.getString(6);
				tanimoto = rs.getDouble(4);
				molecule.setOrderInModel((int)Math.round(tanimoto*100));
						
					
				results.addItem(molecule);
			}
			rs.close();
		} catch (Exception x) {
			x.printStackTrace();
			rs.close();
		} finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}	
		return results;
		

	}
	
	public PreparedStatement prepareTanimoto(IAtomContainer mol,int page, int pagesize) throws SQLException {
		boolean hasQuery = (mol != null) &&
					(mol.getAtomCount() > 0)	;
		try {
			if (hasQuery) {
				BitSet bs = fingerprinter.getFingerprint(mol);
				long[] h16 = new long[16];
				MoleculeTools.bitset2Long16(bs,64,h16);
				String[] fp = new String[16];
				for (int i = 0; i < 16; i++) {
					fp[i] = Long.toString(h16[i]);
				}
				//System.out.println(bs.cardinality()); //bit count
				String bc = Integer.toString(bs.cardinality());
				StringBuffer b = new StringBuffer();
					b.append("select cbits,bc,");
					b.append(bc);
					b.append(" as NA,round(cbits/(bc+");
					b.append(bc);
					b.append("-cbits),2) as similarity,smiles,formula,molweight,L.idsubstance  from (");
					b.append("select fp1024.idsubstance,(");
					for (int h=0; h < 16; h++) {
						b.append("bit_count("); 
						b.append(fp[h]); 
						b.append("& fp");
						b.append(Integer.toString(h+1));
						b.append(")");
						if (h<15) b.append(" + "); else b.append(") ");
					}
					b.append(" as cbits,bc from fp1024 ");
					if ((srcDataset != null) && (srcDataset.getId()>=0)) {
						b.append(" join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=");
						b.append(srcDataset.getId());
					}
					b.append (") as L, substance ");
					b.append("where bc > 0 and cbits > 0 and L.idsubstance=substance.idsubstance order by similarity desc limit ");
					b.append(page);
					b.append(",");
					b.append(pagesize);   
//select fp1024.idsubstance,bc,fp1 from fp1024 
//join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=?;					
				System.out.println(b.toString());

				return conn.prepareStatement(b.toString());

			} else return null;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public PreparedStatement prepareSubstructureSearchPS(IMolecule mol,int page, int pagesize) throws SQLException {
		boolean hasQuery = (mol != null) &&	(mol.getAtomCount() > 0)	;
		try {
			if (hasQuery) {
				BitSet bs = fingerprinter.getFingerprint((IAtomContainer) mol);
				long[] h16 = new long[16];
				MoleculeTools.bitset2Long16(bs,64,h16);
				String[] fp = new String[16];
				for (int i = 0; i < 16; i++) {
					fp[i] = Long.toString(h16[i]);
				}
				//System.out.println(bs.cardinality()); //bit count
				
				String bc = Integer.toString(bs.cardinality());
				StringBuffer b = new StringBuffer();
					b.append("select cbits,bc,formula,molweight,L.idsubstance,smiles  from (select fp1024.idsubstance,");
					for (int i = 0; i < 16; i++) {
						if (i > 0) b.append("+");
						b.append("bit_count(");
						b.append(fp[i]);
						b.append("& fp");
						b.append(Integer.toString(i+1).trim());
						b.append(") ");
					}
					b.append(" as cbits,");
					b.append("bc from fp1024 ");
					if ((srcDataset != null) && (srcDataset.getId()>=0)) {
						b.append(" join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=");
						b.append(srcDataset.getId());
					}
					b.append(") as L, substance ");
					b.append("where L.cbits=");
					b.append(bs.cardinality());
					b.append(" and L.idsubstance=substance.idsubstance limit ");
					b.append(page);
					b.append(",");
					b.append(pagesize);   
					
				

				return  conn.prepareStatement(b.toString());
			}	
		} catch (Exception ex) {
			//logger.error(ex);
		}
		return null;
	}

	public CompoundsList substructureSearch(Compound mol) throws SQLException {
		CompoundsList results = null;
		try {
				ResultSet rs = null;
				if ((mol.getMolecule() == null) || (mol.getMolecule().getAtomCount()==0)) return null;
				Molecule newMol = (Molecule) mol.getMolecule().clone();
				MFAnalyser mfa = new MFAnalyser(newMol);
			    mfa.removeHydrogensPreserveMultiplyBonded();				
				PreparedStatement ps = prepareSubstructureSearchPS(mol.getMolecule(),page,pageSize);
				String smiles = "";
				SmilesParser p = new SmilesParser();
				Molecule m = null;
				try {
					rs = ps.executeQuery();
					//double tanimoto ;
					//prescreened compounds
					while( rs.next() ) {
						
						smiles = rs.getString(6);
						m = null;
						try {
							m = p.parseSmiles(smiles);
						} catch (Exception x) {
							//logger.error("Error when parsing smiles\t",smiles);
							continue;
						}
						if (UniversalIsomorphismTester.isSubgraph(m,mol.getMolecule())) {
						
							if (results == null) results = new CompoundsList();
							results.setEditable(false);			
							Compound molecule = new Compound(m);
							molecule.writeIdsubstance(rs.getInt(5));
							molecule.setId(rs.getInt(5));
							
							String s = rs.getString(3);
							if (s != null)	molecule.setFormula(s);
							s = rs.getString(6);
							if (s != null)	molecule.setSMILES(s);
							molecule.setOrderInModel(results.size()+1);
							//s = rs.getString(6);
							results.addItem(molecule);
						}	
					}
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					rs.close();
				}
				rs = null;
			return results;
		
			
			

		} catch (Exception ex) {
			//logger.error(ex);
		}
		return results;
	}
	
	public CompoundsList searchBy(Compound mol) throws SQLException {
		boolean hasQuery = false;
		CompoundsList results = null;
		StringBuffer condition = new StringBuffer();
		List c = new ArrayList();  
		
		String s = mol.getCAS_RN();
		if (!s.equals("")) { hasQuery = true; c.add(s); 
				condition.append(" and casno=?"); };

		s = mol.getFormula();
		if (!s.equals("")) { hasQuery = true; c.add(s); 
			condition.append(" and formula=?"); };
		
		s = mol.getSMILES();
		if (!s.equals("")) { hasQuery = true; c.add(s); 
			condition.append(" and smiles=?"); };
		
		s = mol.getName();
		if (!s.equals("")) { hasQuery = true; c.add(s);
			condition.append(" and name.name=?"); };

		if (hasQuery) {
			
			ResultSet rs = null;
			PreparedStatement ps;
			
			StringBuffer pss = new StringBuffer();
			pss.append("SELECT substance.idsubstance,c.idstructure,casno,formula,smiles,name.name,r.name "); 
			pss.append("FROM  structure as c, substance left join cas on (cas.idstructure=c.idstructure) "); 
			pss.append("LEFT JOIN name on (name.idstructure=c.idstructure) "); 
			pss.append("LEFT JOIN struc_dataset on (struc_dataset.idstructure=c.idstructure) ");			
			pss.append("LEFT JOIN src_dataset as r on (struc_dataset.id_srcdataset=r.id_srcdataset) ");			
			pss.append("WHERE  substance.idsubstance=c.idsubstance ");
			pss.append(condition.toString());
//			String pss = "select s.idsubstance,casno,formula,smiles,name,id_srcdataset from substance as s left join structure as c using (idsubstance)  left join struc_dataset using(idstructure) left join cas using(idstructure) left join name using(idstructure) where "+
			//"SELECT substance.idsubstance,casno as CAS_RN,formula as Formula,molweight as MolWeight,name.name as ChemName,smiles as SMILES,type_structure,c.idstructure FROM structure as c, substance left join cas on (cas.idstructure=c.idstructure) LEFT JOIN name on (name.idstructure=c.idstructure) WHERE name.name = "phenol" and substance.idsubstance=c.idsubstance group by idsubstance order by type_structure desc limit 500;"			
			pss.append(" group by idsubstance order by type_structure limit 300;");
			
			ps = conn.prepareStatement(pss.toString());
			System.err.println(pss);
			for (int i = 0; i < c.size(); i++) {
				System.err.println((String) c.get(i));				
				ps.setString(i+1,(String) c.get(i));
			}	

			try {
				rs = ps.executeQuery();
				
				while( rs.next() ) {
					if (results == null) results = new CompoundsList();
					results.setEditable(false);			
					Compound molecule = new Compound();
					molecule.writeIdsubstance(rs.getInt(1));
					molecule.setId(rs.getInt(2));
					
					s = rs.getString(3);
					if (s != null)	molecule.setCAS_RN(s);
					s = rs.getString(4);
					if (s != null)	molecule.setFormula(s);
					s = rs.getString(5);
					if (s != null)	molecule.setSMILES(s);
					s = rs.getString(6);					
					if (s != null)	molecule.setName(s);					
					//s = rs.getString(6);					
					//if (s != null)	molecule.getS					
					
					results.addItem(molecule);
				}
				rs.close();
				if (results != null)
				    System.out.println("Found\t"+results.size());
			} catch (SQLException e) {
				e.printStackTrace();
				rs.close();
			}
			rs = null;
		}	
		return results;
	
	}
	
	public static void main(String[] args) {
		
		DbExport dbe = new DbExport("ambit");
		
		Benchmark b = new Benchmark();
		b.mark();
		try {
			dbe.open();
			DbSearchCompound dbs = new DbSearchCompound(dbe);	
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
				if (mol != null) {
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
				}	
				} catch (CDKException x) {
					x.printStackTrace();
				}
			}
			dbe.closeStatement(stmt);
			dbs = null;
			dbe.close();
			dbe= null;
		} catch (AmbitException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		b.record();
		b.report();
	}

	public SourceDataset getSrcDataset() {
		return srcDataset;
	}

	public void setSrcDataset(SourceDataset srcDataset) {
		this.srcDataset = srcDataset;
	}
}
