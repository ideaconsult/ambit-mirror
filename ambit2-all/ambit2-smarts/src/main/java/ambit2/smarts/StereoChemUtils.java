package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;


public class StereoChemUtils 
{
	public static final String STEREO_ELEMENTS_PROPERTY = "AMBIT_STEREO_PROPERTY";
	
	/**
	 * 
	 * @param originalDBS - the stereo element from the original molecule that will be cloned
	 * @param originalMol - the original molecule
	 * @param cloneMol - the clone molecule. Its atoms and bond must have already be cloned
	 * @param newAtoms - the cloned/new atoms 
	 * @param newBonds - the cloned/new bonds
	 * @return
	 */
	
	public static DoubleBondStereochemistry cloneDoubleBondStereochemistry(
							DoubleBondStereochemistry originalDBS, 
							IAtomContainer originalMol,
							IAtomContainer cloneMol,
							IAtom newAtoms[], 
							IBond newBonds[])
	{
		if (originalDBS.getStereoBond() == null)
			return null;
		
		int steroBondNum = originalMol.getBondNumber(originalDBS.getStereoBond());
		IBond bonds0[] = originalDBS.getBonds();
		
		if (bonds0 == null)
			return null;
		
		IBond bonds[] = new IBond[bonds0.length];
		for (int i = 0; i < bonds0.length; i++)
		{
			int boNum = originalMol.getBondNumber(bonds0[i]);
			bonds[i] = newBonds[boNum];
		}
		
		DoubleBondStereochemistry dbs = new DoubleBondStereochemistry(newBonds[steroBondNum], bonds, originalDBS.getStereo());
		
		return dbs;
	}
	
	
	/*
	 * Cloning based on presumption that atom/bond indices define 
	 * the atom mapping between original and clone molecule 
	 */
	public static DoubleBondStereochemistry cloneDoubleBondStereochemistry(
			DoubleBondStereochemistry originalDBS, 
			IAtomContainer originalMol,
			IAtomContainer cloneMol
			)
	{
		if (originalDBS.getStereoBond() == null)
			return null;

		int steroBondNum = originalMol.getBondNumber(originalDBS.getStereoBond());
		
		IBond bonds0[] = originalDBS.getBonds();

		if (bonds0 == null)
			return null;

		IBond bonds[] = new IBond[bonds0.length];
		for (int i = 0; i < bonds0.length; i++)
		{
			int boNum = originalMol.getBondNumber(bonds0[i]);
			bonds[i] = cloneMol.getBond(boNum);
		}

		DoubleBondStereochemistry dbs 
			= new DoubleBondStereochemistry(cloneMol.getBond(steroBondNum), bonds, originalDBS.getStereo());

		return dbs;
	}

	public static TetrahedralChirality cloneTetrahedralChirality(
			TetrahedralChirality originalTHC, 
			IAtomContainer originalMol,
			IAtomContainer cloneMol,
			IAtom newAtoms[])
	{	
		IAtom chiralAtom = originalTHC.getChiralAtom();
		if (chiralAtom == null)
			return null;
		
		int chiralAtomNum = originalMol.getAtomNumber(chiralAtom);
		IAtom ligands0[] = originalTHC.getLigands();
		if (ligands0 == null)
			return null;
		
		IAtom ligands[] = new IAtom[ligands0.length];
		for (int i = 0; i < ligands0.length; i++)
		{
			int atNum = originalMol.getAtomNumber(ligands0[i]);
			ligands[i] = newAtoms[atNum];
		}
		
		TetrahedralChirality thc = new TetrahedralChirality(newAtoms[chiralAtomNum], ligands, originalTHC.getStereo());
		
		return thc;
	}
	
	
	/*
	 * Cloning based on presumption that atom/bond indices define 
	 * the atom mapping between original and clone molecule 
	 */
	public static TetrahedralChirality cloneTetrahedralChirality(
			TetrahedralChirality originalTHC, 
			IAtomContainer originalMol,
			IAtomContainer cloneMol	)
	{	
		IAtom chiralAtom = originalTHC.getChiralAtom();
		if (chiralAtom == null)
			return null;
		
		int chiralAtomNum = originalMol.getAtomNumber(chiralAtom);
		IAtom ligands0[] = originalTHC.getLigands();
		if (ligands0 == null)
			return null;
		
		IAtom ligands[] = new IAtom[ligands0.length];
		for (int i = 0; i < ligands0.length; i++)
		{
			int atNum = originalMol.getAtomNumber(ligands0[i]);
			ligands[i] = cloneMol.getAtom(atNum);
		}
		
		TetrahedralChirality thc = new TetrahedralChirality(cloneMol.getAtom(chiralAtomNum), ligands, originalTHC.getStereo());
		
		return thc;
	}
	
	/*
	 * The function works with the immutable IAtomContainer list of stereo elements
	 */
	public static void checkStereoElements(IAtomContainer mol)
	{	
		List<IStereoElement> okElements = new ArrayList<IStereoElement>();
		for (IStereoElement element : mol.stereoElements())
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("DBStereo status = " + status);
				continue;
			}
			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("Chiral atom status = " + status);
				continue;
			}
			
			//System.out.println(element.getClass().getName());
			//TODO handle ExtendedTetrahedral stereo elements
			
			okElements.add(element);
		}
		
		mol.setStereoElements(okElements);
	}
	
	/*
	 * The function check the stereo elements stored as molecule property 
	 */
	public static void checkStereoElementsStoredAsProperty(IAtomContainer mol)
	{	
		List<IStereoElement> okElements = new ArrayList<IStereoElement>();
		List<IStereoElement> molStereoElements = mol.getProperty(STEREO_ELEMENTS_PROPERTY);
		
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("DBStereo status = " + status);
				continue;
			}			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("Chiral atom status = " + status);
				continue;
			}
			
			//System.out.println(element.getClass().getName());
			//TODO handle ExtendedTetrahedral stereo elements
			
			okElements.add(element);
		}
				
		setStereoElementsListAsProperty(mol, okElements);
	}
	
	/**
	 *  
	 * @param dbs - DoubleBondStereochemistry object to checked
	 * @param mol - target molecule (IAtomContainer) 
	 * @return check result:
	 * 	0 - OK
	 * 	1 - stereo bond is null 
	 * 	2 - stereo bond is not present in the molecule
	 * 	3 - stereo bond is not double bond
	 *  4 - stereo bond does not contain 2 atoms
	 * 	5 - ligand bonds array is null
	 *  6 - one of the ligand bonds is not present in the molecule 
	 *  7 - one of the ligand bonds is not adjacent to the stereo bond
	 *  8 - one of the ligand bonds is not single bond 
	 *  9 - stereo Conformation is null
	 */
	public static int checkDoubleBondStereochemistry(DoubleBondStereochemistry dbs, IAtomContainer mol)
	{
		IBond stereoBond = dbs.getStereoBond();
		if (stereoBond == null)
			return 1;
		
		if (!mol.contains(stereoBond))
			return 2;
		
		if (stereoBond.getOrder() != IBond.Order.DOUBLE)
			return 3;
		
		if (stereoBond.getAtomCount() != 2)
			return 4;
		
		IBond ligandBonds[] = dbs.getBonds();
		
		if (ligandBonds == null)
			return 5;
		
		//System.out.println("ligandBonds.length = " + ligandBonds.length);			
		
		for (int i = 0; i < ligandBonds.length; i++)
		{
			if (!mol.contains(ligandBonds[i]))
				return 6;
			
			if ( (!ligandBonds[i].contains(stereoBond.getAtom(0))) &&
					(!ligandBonds[i].contains(stereoBond.getAtom(1))) )
				return 7;
			
			if (ligandBonds[i].getOrder() != IBond.Order.SINGLE)
				return 8;
		}
		
		if (dbs.getStereo() == null)
			return 9;
		
		return 0;
	}
	
	/**
	 * 
	 * @param thc - TetrahedralChirality object to be checked
	 * @param mol - target molecule (IAtomContainer)
	 * @return check result:
	 * 	0 - OK
	 * 	1 - chiralAtom (chiral center) is null
	 *  2 - chiralAtom is not present in the molecule
	 *  3 - ligand atoms array is null
	 *  4 - ligand atoms array size is not 4
	 *  5 - one of the ligand atoms is null
	 *  6 - one of the ligand atoms is not present in the molecule
	 *  7 - one of the ligand atoms is not connected to the chiralAtom
	 *  8 - one of the ligand atoms is connected with non-single bond
	 *  9 - stereo is null
	 *  10 - chiralAtom with missing implicit H atom
	 */
	
	public static int checkTetrahedralChirality(TetrahedralChirality thc, IAtomContainer mol)
	{
		IAtom chiralAtom = thc.getChiralAtom();
		
		if (chiralAtom == null)
			return 1;
		
		if (!mol.contains(chiralAtom))
			return 2;
		
		IAtom ligands[] = thc.getLigands();
		
		if (ligands == null)
			return 3;
		
		if (ligands.length != 4)
			return 4;
		
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == null)
			{	
				//TODO Handle sulfoxides: the lone pair is a ligand represented as null 
				return 5;
			}	
			
			if (!mol.contains(ligands[i]))
				return 6;
			
			IBond b = mol.getBond(chiralAtom, ligands[i]);
			
			if (b == null)
			{	
				if (ligands[i] == chiralAtom)
				{
					//ligands[i] is supposed to be the imlicit H atom connected to the ligand
					//that is why the bond is null
					if (chiralAtom.getImplicitHydrogenCount() == 1)
						continue; //the ligand is OK
					else
						return 10;
				}
				else
					return 7;
			}	
			
			if (b.getOrder() != IBond.Order.SINGLE)
				return 8;
		}
		
		if (thc.getStereo() == null)
			return 9;
		
		return 0;
	}
	
	public static String getTetrahedralChiralityStatusString(int status)
	{
		switch (status)
		{
		case 0:
			return "OK";
			
		case 1:
			return "chiralAtom (chiral center) is null";
		
		case 2:
			return "chiralAtom is not present in the molecule";
			
		case 3:
			return "ligand atoms array is null";
			
		case 4:
			return "ligand atoms array size is not 4";
			
		case 5:
			return "one of the ligand atoms is null";
			
		case 6:
			return "one of the ligand atoms is not present in the molecule";
		
		case 7:
			return "one of the ligand atoms is not connected to the chiralAtom";
		
		case 8:
			return "one of the ligand atoms is connected with non-single bond";
			
		case 9:
			return "stereo is null";
			
		case 10:
			return "Chiral atom with missing implicit H atom";
			
		default:
			return "Unknown status";
		}
	}
	
	public static String doubleBondStereochemistry2String(DoubleBondStereochemistry dbs, IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		IBond stereoBond = dbs.getStereoBond();
		
		if (stereoBond == null)
			sb.append("stereo bond = null ");
		else
		{
			sb.append("stereo bond = ");
			sb.append(stereoBond.getAtom(0).getSymbol());
			sb.append(mol.getAtomNumber(stereoBond.getAtom(0)));
			sb.append(" ");
			sb.append(stereoBond.getAtom(1).getSymbol());
			sb.append(mol.getAtomNumber(stereoBond.getAtom(1)));
			sb.append(" ");
		}
		
		sb.append(dbs.getStereo() + "  ligands: ");
		IBond bo[] = dbs.getBonds();
		for (int i = 0; i < bo.length; i++)
		{	
			IBond b = bo[i];
			if (b == null)
				sb.append("null ");
			else
			{	
				if (stereoBond.contains(b.getAtom(0)))	
					sb.append(b.getAtom(1).getSymbol() + mol.getAtomNumber(b.getAtom(1)));
				else
					sb.append(b.getAtom(0).getSymbol() + mol.getAtomNumber(b.getAtom(0)));
				sb.append(" ");
			}	
		}	
		
		return sb.toString();
	}
	
	public static String tetrahedralChirality2String(TetrahedralChirality thc, IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		IAtom chiralAtom = thc.getChiralAtom();
		
		if (chiralAtom == null)
			sb.append("center = null ");
		else
			sb.append("center = " + chiralAtom.getSymbol() + "(" + mol.getAtomNumber(chiralAtom) + ") ");
				
		IAtom ligands[] = thc.getLigands();
		
		if (ligands == null)
		{
			sb.append("ligands = null ");
		}
		else
		{	
			sb.append("ligands = ");
			for (int i = 0; i < ligands.length; i++)
			{
				if (ligands[i] == null)
				{	
					sb.append(" null");
					continue;
				}	
				
				IBond b = mol.getBond(chiralAtom, ligands[i]);

				if (b == null)
					sb.append(" ~null bond~");
				else
				{
					switch (b.getOrder())
					{
					case SINGLE:
						sb.append(" -");
						break;
					case DOUBLE:
						sb.append(" =");
						break;	
					case TRIPLE:
						sb.append(" #");
						break;	
					case QUADRUPLE:
						sb.append(" ~4~");
						break;
					case QUINTUPLE:
						sb.append(" ~5~");
						break;
					case SEXTUPLE:
						sb.append(" ~6~");
						break;
					case UNSET:
						sb.append(" ~unset~");
						break;
					}
				}
				
				if (ligands[i] == null)
					sb.append("null ");
				else
					sb.append(ligands[i].getSymbol() + "(" + mol.getAtomNumber(ligands[i]) + ") ");
			}
		}	
		
		sb.append(" stereo = " + thc.getStereo());
		
		return sb.toString();
	}
	
	public static String extendedTetrahedral2String(ExtendedTetrahedral eth, IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		IAtom focusAtom = eth.focus();
		
		if (focusAtom == null)
			sb.append("focus = null ");
		else
			sb.append("focus = " + focusAtom.getSymbol() + "(" + mol.getAtomNumber(focusAtom) + ") ");
				
		
		IAtom terminals[] = eth.findTerminalAtoms(mol);
		
		if (terminals == null)
			sb.append("terminals = null ");
		else
		{
			sb.append("terminals = ");
			for (int i = 0; i < terminals.length; i++)
			{
				if (terminals[i]== null){	
					sb.append(" null");
					continue;
				}
				
				sb.append(terminals[i].getSymbol() + "(" + 
						mol.getAtomNumber(terminals[i]) + ") ");
			}
			
		}
		
		IAtom peripherals[] = eth.peripherals();
		
		if (peripherals == null)
			sb.append("peripherals = null ");
		else
		{	
			sb.append("peripherals = ");
			for (int i = 0; i < peripherals.length; i++)
			{
				if (peripherals[i]== null){	
					sb.append(" null");
					continue;
				}
				
				sb.append(peripherals[i].getSymbol() + "(" + 
						mol.getAtomNumber(peripherals[i]) + ") ");
				
				//TODO
			}
		}
		
		return sb.toString();
	}
	
	public static String stereoElement2String(IStereoElement element, IAtomContainer mol)
	{
		if (element instanceof DoubleBondStereochemistry)
			return doubleBondStereochemistry2String((DoubleBondStereochemistry) element, mol);
		if (element instanceof TetrahedralChirality)
			return tetrahedralChirality2String((TetrahedralChirality) element, mol);
		if (element instanceof ExtendedTetrahedral)
			return extendedTetrahedral2String((ExtendedTetrahedral) element, mol);
		return null;
	}
	
	public static String getStereoChangesAsString(Map<IStereoElement, StereoChange> stereoChanges, IAtomContainer mol) {
		StringBuffer sb = new StringBuffer();
		Set<IStereoElement> keys = stereoChanges.keySet();
		for (IStereoElement stEl : keys) {
			sb.append(stereoElement2String (stEl, mol) + "  " + stEl + " ");
			sb.append(" -> ");
			StereoChange stChange = stereoChanges.get(stEl);
			if (stChange == null) 
				sb.append("null");	
			else
				sb.append(stChange.toString(mol));
			//sb.append("  " + stChange);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	public static void cloneAndCheckStereo(IAtomContainer cloneMol, IAtomContainer originalMol)
	{
		for (IStereoElement element : originalMol.stereoElements() )
    	{
			if (element instanceof DoubleBondStereochemistry)
    		{
    			DoubleBondStereochemistry dbs0 = (DoubleBondStereochemistry) element;
    			DoubleBondStereochemistry dbs = 
    					StereoChemUtils.cloneDoubleBondStereochemistry(dbs0, originalMol, cloneMol);
    			if (dbs != null)
    			{	
    				int checkRes = checkDoubleBondStereochemistry(dbs, cloneMol);
    				if (checkRes == 0)
    					cloneMol.addStereoElement(dbs);
    			}	
    			continue;
    		}
			
			if (element instanceof TetrahedralChirality)
			{
				TetrahedralChirality thc0 = (TetrahedralChirality) element;
				TetrahedralChirality thc =     			 
						StereoChemUtils.cloneTetrahedralChirality(thc0, originalMol, cloneMol);
				
				if (thc != null)
				{	
					int checkRes = checkTetrahedralChirality(thc, cloneMol);
					if (checkRes == 0)
						cloneMol.addStereoElement(thc);
				}	
				continue;
			}

			//TODO handle ExtendedTetrahedral (... allene like chiral centers)
    	}
		
		
	}
	
	
	public static String getStereoElementsStatus(IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		List<IStereoElement> molStereoElements = mol.getProperty(STEREO_ELEMENTS_PROPERTY);
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);			
				sb.append("DBStereo status = " + status + "   " 
						+ doubleBondStereochemistry2String((DoubleBondStereochemistry) element, mol) + "\n");
				continue;
			}			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				sb.append("Chiral atom status = " + status + "   " + 
						tetrahedralChirality2String((TetrahedralChirality) element, mol) + "\n");
				continue;
			}
			//TODO handle ExtendedTetrahedral stereo elements
		}
		return sb.toString();
	}
	
	public static String getAllStereoElementsStatus(IAtomContainer mol, List<IStereoElement> invElements)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(" Normal elements \n");
		List<IStereoElement> molStereoElements = mol.getProperty(STEREO_ELEMENTS_PROPERTY);
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);			
				sb.append("    DBStereo status = " + status + "   " 
						+ doubleBondStereochemistry2String((DoubleBondStereochemistry) element, mol) + "\n");
				continue;
			}			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				sb.append("    Chiral atom status = " + status + "   " + 
						tetrahedralChirality2String((TetrahedralChirality) element, mol) + "\n");
				continue;
			}
			//TODO handle ExtendedTetrahedral stereo elements
		}
		sb.append(" Invalidated elements \n");
		for (IStereoElement element : invElements)
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);			
				sb.append("   DBStereo status = " + status + "   " 
						+ doubleBondStereochemistry2String((DoubleBondStereochemistry) element, mol) + "\n");
				continue;
			}			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				sb.append("   Chiral atom status = " + status + "   " + 
						tetrahedralChirality2String((TetrahedralChirality) element, mol) + "\n");
				continue;
			}
			//TODO handle ExtendedTetrahedral stereo elements
		}
		return sb.toString();
	}
	
	static public void setSteroElementsAsAtomAndBondProperties(IAtomContainer container){

		for (IStereoElement element : container.stereoElements())
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				DoubleBondStereochemistry dbsc = (DoubleBondStereochemistry)element;
				IBond bond = dbsc.getStereoBond();
				if (bond != null)
					bond.setProperty("StereoElement", element);
				continue;
			}

			if (element instanceof TetrahedralChirality)
			{
				TetrahedralChirality thc = (TetrahedralChirality)element;
				IAtom atom = thc.getChiralAtom();
				if (atom != null)
					atom.setProperty("StereoElement", element);
				continue;
			}

			if (element instanceof ExtendedTetrahedral)
			{
				/*
				ExtendedTetrahedral et = (ExtendedTetrahedral)element;
				IAtom atom = et.focus();
				if (atom != null)
					atom.setProperty("StereoElement", element);
				*/	
				continue;
			}
		}
	}
	
	
	//---------------Utilities for manipulation(update) of stereo elements------------------
	
	/*
	
	 //this is function version without StereoChange
	  
	public static DoubleBondStereochemistry deleteAtom(IAtom at, DoubleBondStereochemistry dbsc)
	{
		if (dbsc.getStereoBond().contains(at))
			return null; //entire element will be removed since the deleted atom is part of the double bond

		//Stereo element is invalidated: 
		//bond is 'removed' i.e. replaced with null pointer
		IBond bonds[] = dbsc.getBonds();

		if (bonds == null)
			return dbsc;

		if (bonds.length == 0)
			return dbsc;
		
		IBond newBonds[] = deleteBondFromLigands(at, bonds);
		
		if (bonds == newBonds)
			return dbsc; //no change (bond containing the atom is not found among the ligand bonds)
		
		return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
	}
	*/
	
		
	
	public static TetrahedralChirality deleteAtom(IAtom at, 
			//IAtomContainer target, 
			TetrahedralChirality thc)
	{
		if (thc.getChiralAtom() == at)
			return null; //entire element will be removed since the deleted atom is the chiral center
		
		IAtom ligands[] = thc.getLigands();
		if (ligands == null)
			return thc;
				
		if (ligands.length == 0)
			return thc; 
		
		IAtom newLigands[] = deleteAtomFromLigands(at, ligands);
		
		if (ligands == newLigands)
			return thc;  //no change (atom is not found among the ligands)
		
		return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
	}
	
	public static ExtendedTetrahedral deleteAtom(IAtom at, 
			//IAtomContainer target, 
			ExtendedTetrahedral etc)
	{
		//TODO
		return null;
	}
	
	
	/*
	//this is function version without StereoChange
	  
	public static DoubleBondStereochemistry bondChange(IAtom at1, IAtom at2,
		 	IBond.Order initialBondOrder, IBond.Order updatedBondOrder, 
			IAtomContainer target,
			DoubleBondStereochemistry dbsc)
	{
		if (dbsc.getStereoBond() == null)
			return dbsc;
		
		
		IAtom dbAtom = null; //the atom that is part of the double bond
		IAtom perAtom = null; //the peripheral atom
		
		if (dbsc.getStereoBond().contains(at1))
			dbAtom = at1;
		else
			perAtom = at1;
		
		if (dbsc.getStereoBond().contains(at2))
		{
			if (dbAtom == at1)
			{
				//both atoms (at1, at2) are part of the double bond
				if (updatedBondOrder != IBond.Order.DOUBLE)
					return null; //element is 'totally' removed
				else
					return dbsc; //this case should not happen 
			}
			else
				dbAtom = at2;
		}
		else
			perAtom = at2;
		
		if (dbAtom == null)
			return dbsc;  //Both atoms are not part of the double bond. No change done
		
		IBond bonds[] = dbsc.getBonds();
		
		if (bonds == null)
			return dbsc;  //stereo element is not valid. No change done
		
		
		if (initialBondOrder == null)
		{
			if (updatedBondOrder == null)
				return dbsc; //This case should not appear. No change
			
			
			//Check for a very rare case 
			//(most probably this is not needed since 'bo' is a newly created bond 
			//and it is not among bonds[]):
			
			//for (int i = 0; i < bonds.length; i++)
			//	if (bonds[i] == bo)
			//		return dbsc;
			
			
			IBond bo = target.getBond(at1, at2);
			IBond newBonds[] = addBondToLigands(bo, bonds);
			return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
		}
		else
		{
			if (updatedBondOrder == null)
			{
				IBond newBonds[] = deleteBondFromLigands(perAtom, bonds);
				return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
			}
			
			//Bond order is changed hence nothing is done to the stereo element
			//If inappropriate order is set, the final stereo check will clear this stereo element
			return dbsc;
		}
	}
	*/
	
	
	public static TetrahedralChirality bondChange(IAtom at1, IAtom at2,
			IBond.Order initialBondOrder, IBond.Order updatedBondOrder, 
			IAtomContainer target,
			TetrahedralChirality thc)
	{
		IAtom ligands[] = thc.getLigands();
		IAtom ligAtom = null;
		
		if (thc.getChiralAtom() == at1)
			ligAtom = at2;
		else
			if (thc.getChiralAtom() == at2)
				ligAtom = at1;
			else
				return thc; //neither at1 nor at2 is the chiral center 
							//hence no change is done to the stereo element
			
		if (initialBondOrder == null)
		{
			if (updatedBondOrder == null)
				return thc; //This case should not appear. No change
			
			IAtom newLigands[] = addAtomToLigands(ligAtom, ligands);
			return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
		}
		else
		{
			if (updatedBondOrder == null)
			{
				IAtom newLigands[] = deleteAtomFromLigands(ligAtom, ligands);
				return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
			}
			
			//Bond order is changed hence no change is done to the stereo element
			//If inappropriate order is set, the final stereo check will clear this stereo element
			return thc;
		}
	}
	
	
	public static ExtendedTetrahedral bondChange(IAtom at1, IAtom at2,
			IBond.Order initialBondOrder, IBond.Order updatedBondOrder, 
			IAtomContainer target,
			ExtendedTetrahedral etc)
	{
		//TODO
		return null;
	}
	
	
	static IAtom[] addAtomToLigands(IAtom at, IAtom ligands[])
	{
		int n = -1; //index of the first null ligand (if present)		
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == null)
			{
				n = i;
				break;
			}
		}
		
		if (n == -1)
		{
			//no null ligand is present
			//the atom is added as an extra array element
			IAtom newLigands[] = new IAtom[ligands.length+1];
			for (int i = 0; i < ligands.length; i++)
				newLigands[i] = ligands[i];
			newLigands[ligands.length] = at;
			return newLigands;
		}
		else
		{
			//a null ligand is present
			//the atom is added in the place of the first null ligand (i.e. index n)
			ligands[n] = at;
			return ligands;
		}
	}
	
	/*
	
	//this is function version without StereoChange
	
	static IBond[] addBondToLigands(IBond bo, IBond ligandBonds[])
	{
		int n = -1; //index of the first null ligand bond (if present)		
		for (int i = 0; i < ligandBonds.length; i++)
		{
			if (ligandBonds[i] == null)
			{
				n = i;
				break;
			}
		}
		
		if (n == -1)
		{
			//no null ligand bond is present
			//the bond is added as an extra array element
			IBond newLigandBonds[] = new IBond[ligandBonds.length+1];
			for (int i = 0; i < ligandBonds.length; i++)
				newLigandBonds[i] = ligandBonds[i];
			newLigandBonds[ligandBonds.length] = bo;
			return newLigandBonds;
		}
		else
		{
			//a null ligand is present
			//the bond is added in the place of the first null ligand bond (i.e. index n)
			ligandBonds[n] = bo;
			return ligandBonds;
		}
	}
	*/
	
	static IAtom[] deleteAtomFromLigands(IAtom at, IAtom ligands[])
	{
		int n = -1; //the index of atom to be deleted
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == at)
			{
				n = i;
				break;
			}
		}
		
		if (n == -1)
			return ligands;
		
		if (ligands.length > 4)
		{	
			//There is at least one extra ligand added (index 4) 
			//And it will be put in place of the deleted atom
			if (n < 4)
			{
				IAtom newLigands[] = new IAtom[ligands.length-1];
				for (int i = 0; i < 4; i++)
					if (i == n)
						newLigands[i] = ligands[4];
					else
						newLigands[i] = ligands[i];
				
				//Shifting the ligands after index 4 (if more that one extra ligand is present)
				for (int i = 4; i < newLigands.length; i++)
					newLigands[i] = ligands[i+1];
				return newLigands;
			}
			else
			{
				//This case should not appear
				//Set the new ligands where ligand n is set to null 
				IAtom newLigands[] = ligands.clone();
				newLigands[n] = null;
				return newLigands;
			}	
		}
		else
		{
			//Set the new ligands where ligand n is set to null 
			IAtom newLigands[] = ligands.clone();
			newLigands[n] = null;
			return newLigands;
		}
	}
	
	
	/*
	
	//this is function version without StereoChange
	
	static IBond[] deleteBondFromLigands(IAtom at, IBond ligandBonds[])
	{
		int n = -1; //the index of bond to be deleted
		for (int i = 0; i < ligandBonds.length; i++)
		{
			if (ligandBonds[i].contains(at))
			{
				n = i;
				break;
			}
		}
		
		if (n == -1)
			return ligandBonds;
		
		if (ligandBonds.length > 2)
		{	
			//There is at least one extra ligand bond added (index 2) 
			//And it will be put in place of the deleted bond
			if (n < 2)
			{
				IBond newLigandBonds[] = new IBond[ligandBonds.length-1];
				for (int i = 0; i < 2; i++)
					if (i == n)
						newLigandBonds[i] = ligandBonds[2];
					else
						newLigandBonds[i] = ligandBonds[i];
				
				//Shifting the ligands after index 2 (if more that one extra ligand bond is present)
				for (int i = 2; i < newLigandBonds.length; i++)
					newLigandBonds[i] = ligandBonds[i+1];
				return newLigandBonds;
			}
			else
			{
				//This case should not appear
				//Set the new ligand bonds where ligand bond n is set to null 
				IBond newLigandBonds[] = ligandBonds.clone();
				newLigandBonds[n] = null;
				return newLigandBonds;
			}	
		}
		else
		{
			//Set the new ligand bonds where ligand bond n is set to null 
			IBond newLigandBonds[] = ligandBonds.clone();
			newLigandBonds[n] = null;
			return newLigandBonds;
		}
	}
	*/
	
	
	//------------- functions that are beased of StereoChange -----------------------
	
	
	public static DoubleBondStereochemistry deleteAtom(IAtom at, DoubleBondStereochemistry dbsc, StereoChange stChange)
	{	
		if (dbsc.getStereoBond().contains(at))
			return null; //entire element will be removed since the deleted atom is part of the double bond

		//Stereo element is invalidated: 
		//bond is 'removed' i.e. replaced with null pointer and stereo change is set
		IBond bonds[] = dbsc.getBonds();

		if (bonds == null)
			return dbsc;

		//if (bonds.length == 0)  this check not needed
		//	return dbsc;

		IBond newBonds[] = deleteBondFromLigands(at, bonds, stChange);

		if (bonds == newBonds)
		{	
			return dbsc; //no change (bond containing the atom is not found among the ligand bonds)
		}	

		return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
	}
	
	
	static IBond[] deleteBondFromLigands(IAtom at, IBond ligandBonds[], StereoChange stChange)
	{
		int nLigToDelete = -1;
		if (!stChange.ligand0Deleted)
		{
			//System.out.println(" ligand0 bond : " + ligandBonds[0].getAtom(0).getSymbol() + " " + ligandBonds[0].getAtom(1).getSymbol());
			if (ligandBonds[0].contains(at))
				nLigToDelete = 0;
		}

		if (!stChange.ligand1Deleted)
		{
			//System.out.println(" ligand1 bond : " + ligandBonds[1].getAtom(0).getSymbol() + " " + ligandBonds[1].getAtom(1).getSymbol());
			if (ligandBonds[1].contains(at))
				nLigToDelete = 1;
		}
		
		if (nLigToDelete == -1)
			return ligandBonds; //no change (bond containing the atom is not found among the ligand bonds)

		IBond newBonds[] = ligandBonds.clone();

		if (nLigToDelete == 0)
		{	
			if (stChange.addLigands0.isEmpty())
			{	
				stChange.ligand0Deleted = true;
				newBonds[0] = null;
			}	
			else
			{
				//Atoms is "deleted" but then
				//Transferring the first element of addLigands0
				newBonds[0] = stChange.addLigands0.get(0);
				stChange.addLigands0.remove(0);
			}
		}	
		else
		{	
			if (stChange.addLigands1.isEmpty())
			{	
				stChange.ligand1Deleted = true;
				newBonds[1] = null;
			}	
			else
			{
				//Atoms is "deleted" but then
				//Transferring the first element of addLigands1
				newBonds[1] = stChange.addLigands1.get(0);
				stChange.addLigands1.remove(0);
			}
		}	
		
		return newBonds;
	}
	
	static IBond[] addBondToLigands(IBond bo, IBond stereoDB, IBond ligandBonds[], StereoChange stChange)
	{
		int nLig = -1;
		if (bo.contains(stereoDB.getAtom(0)))
			nLig = 0;
		else
			if (bo.contains(stereoDB.getAtom(1)))
				nLig = 1;
		
		if (nLig == -1)
			return ligandBonds;
		
		if (nLig == 0)
		{
			if (stChange.ligand0Deleted)
			{
				//when flag ligand0Deleted = true addLigands0 list must be empty
				IBond newLigandBonds[] = ligandBonds.clone();
				newLigandBonds[0] = bo;
				stChange.ligand0Deleted = false;
				return newLigandBonds;
			}
			else
				stChange.addLigands0.add(bo);
		}
		else
		{
			if (stChange.ligand1Deleted)
			{
				//when flag ligand0Deleted = true addLigands0 list must be empty
				IBond newLigandBonds[] = ligandBonds.clone();
				newLigandBonds[1] = bo;
				stChange.ligand1Deleted = false;
				return newLigandBonds;
			}
			else
				stChange.addLigands1.add(bo);
		}	
		
		return ligandBonds;
	}
	
	public static DoubleBondStereochemistry bondChange(IAtom at1, IAtom at2,
		 	IBond.Order initialBondOrder, IBond.Order updatedBondOrder, 
			IAtomContainer target,
			DoubleBondStereochemistry dbsc,
			StereoChange stChange)
	{
		if (dbsc.getStereoBond() == null)
			return dbsc;
		
		
		IAtom dbAtom = null; //the atom that is part of the double bond
		IAtom perAtom = null; //the peripheral atom
		
		if (dbsc.getStereoBond().contains(at1))
			dbAtom = at1;
		else
			perAtom = at1;
		
		if (dbsc.getStereoBond().contains(at2))
		{
			if (dbAtom == at1)
			{
				//both atoms (at1, at2) are part of the double bond
				if (updatedBondOrder != IBond.Order.DOUBLE)
					return null; //element is 'totally' removed
				else
					return dbsc; //this case should not happen 
			}
			else
				dbAtom = at2;
		}
		else
			perAtom = at2;
		
		if (dbAtom == null)
			return dbsc;  //Both atoms are not part of the double bond. No change done
		
		IBond bonds[] = dbsc.getBonds();
		
		if (bonds == null)
			return dbsc;  //stereo element is not valid. No change done
		
		
		if (initialBondOrder == null)
		{
			if (updatedBondOrder == null)
				return dbsc; //This case should not appear. No change
			
			/*
			//Check for a very rare case 
			//(most probably this is not needed since 'bo' is a newly created bond 
			//and it is not among bonds[]):
			
			for (int i = 0; i < bonds.length; i++)
				if (bonds[i] == bo)
					return dbsc;
			*/
			
			IBond bo = target.getBond(at1, at2);
			IBond newBonds[] = addBondToLigands(bo, dbsc.getStereoBond(), bonds, stChange);
			return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
		}
		else
		{
			if (updatedBondOrder == null)
			{
				IBond newBonds[] = deleteBondFromLigands(perAtom, bonds, stChange);
				return new DoubleBondStereochemistry(dbsc.getStereoBond(), newBonds, dbsc.getStereo());
			}
			
			//Bond order is changed hence nothing is done to the stereo element
			//If inappropriate order is set, the final stereo check will clear this stereo element
			return dbsc;
		}
	}
	
	
	public static boolean isInvalidated(DoubleBondStereochemistry dbsc)
	{
		//if (dbsc.contains(null))
		//	return true;
		
		if (dbsc.getBonds() == null)
			return true;
		
		if (dbsc.getBonds().length != 2)
			return true;
		
		if (dbsc.getBonds()[0] == null)
			return true;
		else
		{	
			if (dbsc.getBonds()[0].getOrder() != Order.SINGLE)
				return true;
		}			
		
		if (dbsc.getBonds()[1] == null)
			return true;
		else
		{	
			if (dbsc.getBonds()[1].getOrder() != Order.SINGLE)
				return true;
		}
		
		return false;
	}
	
	public static boolean isInvalidated(TetrahedralChirality thc)
	{	
		if (thc.getLigands() == null)
			return true;
		
		if (thc.getLigands().length != 4)
			return true;
		
		//TODO handle sulfoxide cases 
		if (contains(thc, null))
			return true;
		
		return false;
	}
	
	public static boolean isInvalidated(ExtendedTetrahedral etc)
	{
		//TODO
		return false;
	}
	
	
	/**
	 * Checks whether the stereo element contains the atom
	 * This function works with invalidated  stereo elements.
	 * The standard IStereoElement.contains() method would throws Null pointer exception 
	 * in some cases of invalidated elements
	 * @param element
	 * @param at
	 * @return
	 */
	public static boolean contains(IStereoElement element, IAtom at)
	{
		if (element instanceof DoubleBondStereochemistry)
		{
			DoubleBondStereochemistry dbsc = (DoubleBondStereochemistry)element;

			IBond bo = dbsc.getStereoBond();
			if (bo != null)
			{	
				if (bo.getAtom(0) == at)
					return true;
				if (bo.getAtom(1) == at)
					return true;
			}
			
			IBond bonds[] = dbsc.getBonds();
			for (int i = 0; i < bonds.length; i++)
				if (bonds[i] != null)
				{
					if (bonds[i].getAtom(0) == at)
						return true;
					if (bonds[i].getAtom(1) == at)
						return true;
				}
		}
		

		if (element instanceof TetrahedralChirality)
		{
			TetrahedralChirality thc =  (TetrahedralChirality)element;
			
			if (thc.getChiralAtom() == at)
				return true;
			
			IAtom ligands[] = thc.getLigands();
			for (int i = 0; i < ligands.length; i++)
				if (ligands[i] == at)
					return true;
		}
		
		//TODO handle ExtendedTetrahedral
		
		return false;
	}
	
	public static DoubleBondStereochemistry findDBStereoElementByStereoBond(IBond stereoBond, IAtomContainer container)
	{
		List<IStereoElement> molStereoElements = container.getProperty(STEREO_ELEMENTS_PROPERTY);
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				DoubleBondStereochemistry dbsc = (DoubleBondStereochemistry)element;
				if (stereoBond == dbsc.getStereoBond())
					return dbsc;
			}
		}	
		
		return null;
	}
	
	public static TetrahedralChirality findTetrahedralChiralityByChiralCenter(IAtom chirCenter, IAtomContainer container)
	{
		List<IStereoElement> molStereoElements = container.getProperty(STEREO_ELEMENTS_PROPERTY);
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof TetrahedralChirality)
			{
				TetrahedralChirality thc = (TetrahedralChirality)element;
				if (chirCenter == thc.getChiralAtom())
					return thc;
			}
		}	
		return null;
	}
	
	public static ExtendedTetrahedral findExtendedTetrahedralByChiralCenter(IAtom chirCenter, IAtomContainer container)
	{
		List<IStereoElement> molStereoElements = container.getProperty(STEREO_ELEMENTS_PROPERTY);
		for (IStereoElement element : molStereoElements)
		{
			if (element instanceof ExtendedTetrahedral)
			{
				ExtendedTetrahedral etc = (ExtendedTetrahedral)element;
				if (chirCenter == etc.focus())
					return etc;
			}
		}	
		return null;
	}
	
	public static DoubleBondStereochemistry restoreDBStereo(
    		IAtomContainer target,
    		DoubleBondStereochemistry dbsc, 
    		StereoChange stChange)
    {	
		IBond newLigandBonds[] = dbsc.getBonds().clone();
		boolean FlagInvertStereo = false;
		
		if (stChange.ligand0Deleted)
    	{
			IAtom at = dbsc.getStereoBond().getAtom(0);
			//Searching for a neighbor with a single bond
			for (IBond bo : target.getConnectedBondsList(at))
			{
				if (bo.getOrder() == IBond.Order.SINGLE)
				{
					newLigandBonds[0] = bo;
					FlagInvertStereo = !FlagInvertStereo;
					break;
				}
			}
			
			if (newLigandBonds[0] == null)
				return null; //Restoration not possible
    	}
		
		if (stChange.ligand1Deleted)
    	{
			IAtom at = dbsc.getStereoBond().getAtom(1);
			//Searching for a neighbor with a single bond
			for (IBond bo : target.getConnectedBondsList(at))
			{
				if (bo.getOrder() == IBond.Order.SINGLE)
				{
					newLigandBonds[1] = bo;
					FlagInvertStereo = !FlagInvertStereo;
					break;
				}
			}
			if (newLigandBonds[1] == null)
				return null; //Restoration not possible
    	}
		
		Conformation stereo;
		if (FlagInvertStereo)
			stereo = dbsc.getStereo().invert();
		else
			stereo = dbsc.getStereo();
		
		return new DoubleBondStereochemistry(dbsc.getStereoBond(), newLigandBonds, stereo);
    }
	
	public static void storeStereoElementsAsProperty(IAtomContainer target) 
	{
		List<IStereoElement> elements = new ArrayList<IStereoElement>();
		for (IStereoElement el : target.stereoElements())
			elements.add(el);
		
		target.setProperty(STEREO_ELEMENTS_PROPERTY, elements);
	}
	
	public static void extractStereoElementsFromProperty(IAtomContainer target) 
	{
		List<IStereoElement> elements = target.getProperty(STEREO_ELEMENTS_PROPERTY);
		if (elements != null ) {
			target.setStereoElements(elements);			
			target.removeProperty(STEREO_ELEMENTS_PROPERTY);
		}
		else
			target.setStereoElements(new ArrayList<IStereoElement>()); //set empty list
	}
	
	public static void setStereoElementsListAsProperty(IAtomContainer target, List<IStereoElement> elements) 
	{
		target.setProperty(STEREO_ELEMENTS_PROPERTY, elements);
	}
	
}	
