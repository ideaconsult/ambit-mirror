/**
 * Created on 2004-12-7
 *
 */
package ambit2.database.core;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.molecule.CompoundsList;
import ambit2.data.molecule.MoleculeTools;


/**
 * SQL statements
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbSQL {
	public static final String AMBIT_stopForeignKeyCheck = "SET FOREIGN_KEY_CHECKS=0;";
	public static final String AMBIT_startForeignKeyCheck = "SET FOREIGN_KEY_CHECKS=1;";
	public static final String AMBIT_stopUniqueKeyCheck = "SET UNIQUE_CHECKS=0;";
	public static final String AMBIT_startUniqueKeyCheck = "SET UNIQUE_CHECKS=1";

	
	
	//
	public static final String AMBIT_Structures = "select idsubstance,idstructure,uncompress(structure) from structure group by idsubstance order by idsubstance;";
	public static final String AMBIT_StructureBySubstance = "select idsubstance,idstructure,uncompress(structure) from structure where idsubstance=?;";	
	public static final String AMBIT_StructuresLimit(long limit) {
		return "select idsubstance,idstructure,uncompress(structure) from structure group by idsubstance order by idsubstance LIMIT "+limit+";";
	}
	public static final String AMBIT_Substances = "select idsubstance,formula,molweight,smiles order by idsubstance;";
	public static final String AMBIT_SubstancesID = "select idsubstance,formula from substance order by idsubstance;";
	public static final String AMBIT_SubstancesIDLimit = "select idsubstance,formula from substance order by idsubstance limit ";
	public static final String AMBIT_SubstancesIDJoinGamut = "select substance.idsubstance,formula from substance join gamut on substance.idsubstance=gamut.idsubstance";
	public static final String AMBIT_SubstancesIDNullSmiles(long limit) {
		return "select idsubstance,formula from substance where smiles is null limit "+limit +";";
	}
	public static final String AMBIT_SubstancesIDNullGamut(long limit) {
		return "select idsubstance,formula from substance where idsubstance not in (select idsubstance from gamut) limit "+limit +";";
	}
	public static final String AMBIT_SubstancesIDNullGamutNullSmiles(long limit) {
		return "select idsubstance,formula from substance where smiles is null and (idsubstance not in (select idsubstance from gamut)) limit "+limit +";";
	}
	
	public static final String AMBIT_SubstancesSimpleSmilesNull(long limit, int cyclicBonds) {
		return "select substance.idsubstance,formula from substance,gamut where smiles is null and (substance.idsubstance=gamut.idsubstance) and (oo=0) and (cb<=" +
		cyclicBonds + ") order by cb limit "+limit+";";
	}

	public static final String AMBIT_SubstancesSimpleSmilesNotcanonical(long limit, int cyclicBonds) {
		//return "select substance.idsubstance,formula from substance,gamut where usmiles=0 and (substance.idsubstance=gamut.idsubstance) and (oo=0) and (cb<=" +
	    return "select substance.idsubstance,formula from substance,gamut where usmiles=0 and (substance.idsubstance=gamut.idsubstance) and (cb<=" +
		cyclicBonds + ") order by cb limit "+limit+";";
	}
	
	public static final String AMBIT_SubstancesStrucSimpleFPNull(long limit, int cyclicBonds) {
		return 
		"select s.idsubstance,smiles from  substance  as s, gamut left join fp1024 on (fp1024.idsubstance = s.idsubstance) where smiles is not null and gamut.idsubstance = s.idsubstance and oo=0 and cb<=" + 
		cyclicBonds + 
		//" and fp1024.status = 0 " +
		" and fp1024.status is null " +
		" LIMIT " +
		//"select s.idsubstance,uncompress(structure),formula,cb,oo from gamut as s join substance using(idsubstance)  join structure using(idsubstance) where (cb<37) and (oo=0) and s.idsubstance not in (select idsubstance from fp1024)  group by idsubstance limit " 
		+ limit + ";";
	}


	public static final String AMBIT_SubstancesStrucSimple(long limit) {
		return 
		"select s.idsubstance,idstructure,uncompress(structure),formula,cb,oo from substance as b join structure as s on b.idsubstance=s.idsubstance join gamut as g on s.idsubstance=g.idsubstance where (cb<37) and (oo=0) group by idsubstance limit " + limit + ";";
	}
	
	public static final String AMBIT_UpdateSmilesByID = "UPDATE substance set smiles=?,usmiles=?,time_elapsed=? where idsubstance=?";
	
	public static final String AMBIT_SubstancesIDByCB(int lowCB,int highCB) {
		return AMBIT_SubstancesIDJoinGamut + " where cb between " + lowCB + " and " + highCB + " order by cb;";
	}
	public static final String AMBIT_SubstancesIDByCBLimit(int lowCB,int highCB, long limit) {
		return AMBIT_SubstancesIDJoinGamut + " where cb between " + lowCB + " and " + highCB + " order by cb limit "+limit + ";";
	}
	
	public static final String AMBIT_ExactSearchBySmiles = "select idsubstance,formula,molweight,stype_id from substance where smiles=? order by idsubstance;";  
	public static final String AMBIT_ExactStrucSearchBySmiles = "select substance.idsubstance,idstructure,type_structure from substance,structure where smiles=? and substance.idsubstance=structure.idsubstance order by type_structure desc;";	
	public static final String AMBIT_ExactStrucSearchBySmilesCAS = "select substance.idsubstance,structure.idstructure,type_structure from substance,structure,cas where smiles=? and casno=? and substance.idsubstance=structure.idsubstance and structure.idstructure=cas.idstructure order by type_structure desc;";	
		
	
	/*
		//select * from fp1024 as f join tmpfp1024 as t on f.fp1=t.fp1 and f.fp2 = t.fp2 and f.fp3=t.fp3 and f.fp4=t.fp4 and f.fp5=t.fp5 and f.fp6=t.fp6 and f.fp7=t.fp7 and f.fp8=t.fp8 and f.fp9=t.fp9 and f.fp10 = t.fp10 and f.fp11=t.fp11 and f.fp12=t.fp12 and f.fp13=t.fp13 and f.fp14=t.fp14 and f.fp15=t.fp15 and f.fp16=t.fp16;
		/*
		 *select idsubstance,concat(
lpad(hex(fp1),16,0),
lpad(hex(fp2),16,0),
lpad(hex(fp3),16,0),
lpad(hex(fp4),16,0),
lpad(hex(fp5),16,0),
lpad(hex(fp6),16,0),
lpad(hex(fp7),16,0),
lpad(hex(fp8),16,0),
lpad(hex(fp9),16,0),
lpad(hex(fp10),16,0),
lpad(hex(fp11),16,0),
lpad(hex(fp12),16,0),
lpad(hex(fp13),16,0),
lpad(hex(fp14),16,0),
lpad(hex(fp15),16,0),
lpad(hex(fp16),16,0)
) as FP  from fp1024 order by idsubstance limit 10\G 
		StringBuffer b = new StringBuffer();
		b.append("select idsubstance from fp1024 where ");
		for (int i = 0; i < h16.length ; i++) {
			if (i>0) b.append (" and ");
			b.append("fp");
			b.append((i+1));
			b.append("=");
			//b.append(Long.toHexString(h16[i]));
			
			b.append(BigInteger.valueOf(h16[i]).toString());
		}		
		b.append(";");
		return b.toString();
	}
			 * 
		 */

	public static final String AMBIT_createTmpImportTable = 
		"CREATE TABLE IF NOT EXISTS import (id INTEGER(10) UNSIGNED NOT NULL AUTO_INCREMENT, cas VARCHAR(11) NULL, name Text NULL, formula VARCHAR(255) NULL, molweight FLOAT(8,3) NULL, smiles TEXT NULL, time_elapsed INTEGER(11) NOT NULL, remark TEXT NULL, t_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  structure blob not null,idsource_dataset INTEGER(10) UNSIGNED NOT NULL,PRIMARY KEY(id)) ENGINE=MyISAM;";
	
	public static final String AMBIT_insertIntoTmpImportTable = "insert into import set cas=?, name=?, smiles=?, remark=?, structure=?, idsource_dataset=?;";
	
	public static final String AMBIT_getDuplicateSmiles = "select count(smiles) as cs, smiles,min(idsubstance) as idsubstance from substance where smiles is not null group by smiles order by cs desc;";
	
	public static final String AMBIT_packStructures = "update structure as a,substance set a.idsubstance=? where smiles=? and substance.idsubstance=a.idsubstance;";
	
	public static final String AMBIT_deleteDuplicateSubstances = "delete substance where smiles=? and idsubstance !=?;";
	public static final String AMBIT_deleteOrphanSubstances = "delete from  substance where substance.idsubstance not in (select idsubstance from structure order by idsubstance);";
	public static final String AMBIT_deleteOrphanFingerprints = "delete from  fp1024 where fp1024.idsubstance not in (select idsubstance from structure order by idsubstance);";
	public static final String AMBIT_deleteOrphanGamut = "delete from  gamut where gamut.idsubstance not in (select idsubstance from structure order by idsubstance);";
	
	/*
	public static String getExactSearchSQL(IAtomContainer mol,int page, int pagesize, double threshold) {
			String 
			Object s = mol.getProperty(AmbitCONSTANTS.SMILES);
			if (s != null) { 
				if (hasQuery) condition.append(" and ");
				hasQuery = true; c.add(s.toString()); 
				condition.append(" smiles=?"); }
			else {
				SmilesGenerator generator = new SmilesGenerator();
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
	}
	*/
	public static String getExactSearchSQL(Map properties,int page, int pagesize, double threshold, List parameters) {
			String[][] conditions={
					{CDKConstants.CASRN,"casno"},
					{AmbitCONSTANTS.FORMULA,"formula"},
					{CDKConstants.NAMES,"name.name"},
					{AmbitCONSTANTS.SMILES,"smiles"}
					};
			boolean hasQuery = false;
			CompoundsList results = null;
			StringBuffer condition = new StringBuffer();
			
			
			for (int i=0;i<conditions.length;i++) {
				Object s = properties.get(conditions[i][0]);
				if ((s != null) && (!s.equals(""))) {
						if (hasQuery) condition.append(" and ");
						hasQuery = true; 
						parameters.add(s.toString()); 
						condition.append(conditions[i][1]);
						condition.append("=?");
				};			
			}

			if (hasQuery) {
				
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
			
				pss.append(" group by idsubstance order by type_structure limit ");
				pss.append(page);
				pss.append(",");
				pss.append(pagesize);
				
				
				return pss.toString();
			} else return null;
			
		
		}

		public static  String getSimilaritySearchSQL(IAtomContainer query,int page, int pagesize, double threshold, int srcDataset, List parameters) throws Exception {

		boolean hasQuery = (query != null) &&
					(query.getAtomCount() > 0)	;
			if (hasQuery) {
				IAtomContainer mol = query;
    			
				Fingerprinter fingerprinter = new Fingerprinter(1024);
				BitSet bs = fingerprinter.getFingerprint(mol);
				long[] h16 = new long[16];
				MoleculeTools.bitset2Long16(bs,64,h16);
				String[] fp = new String[16];
				for (int i = 0; i < 16; i++) {
					fp[i] = Long.toString(h16[i]);
				}
				int bc = bs.cardinality();
				StringBuffer b = new StringBuffer();
					b.append("select cbits,bc,?\n");
					//b.append(bc);
					b.append(" as NA,round(cbits/(bc+?");
					//b.append(bc);
					b.append("-cbits),2) as similarity,smiles,formula,molweight,L.idsubstance  from (\n");
					b.append("select fp1024.idsubstance,(\n");
					for (int h=0; h < 16; h++) {
						b.append("bit_count("); 
						b.append(fp[h]); 
						b.append("& fp");
						b.append(Integer.toString(h+1));
						b.append(")");
						if (h<15) b.append(" + \n"); else b.append(") \n");
					}
					b.append(" as cbits,bc from fp1024 \n");
					if (srcDataset>=0) {
						b.append(" join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=\n");
						b.append(srcDataset);
					}
					b.append (") as L, substance \n");
					b.append("where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idsubstance=substance.idsubstance order by similarity desc limit ");
					b.append(page*pagesize);
					b.append(',');
					b.append(pagesize);
					b.append('\n');
//select fp1024.idsubstance,bc,fp1 from fp1024 
//join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=?;					
			
	
				parameters.add(bc);
				parameters.add(bc);
				parameters.add(bc);
				parameters.add(threshold);
				/*
				ps.setInt(1,bc);
				ps.setInt(2,bc);
				ps.setInt(3,bc);
				ps.setDouble(4,threshold);
				ps.setInt(5,page*pagesize);
				ps.setInt(6,pagesize);
				System.out.println(ps);
				return ps;
				*/
				return b.toString();
			} else return null;

	}	
}
