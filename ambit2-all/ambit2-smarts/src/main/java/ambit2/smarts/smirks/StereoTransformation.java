package ambit2.smarts.smirks;

import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

import ambit2.smarts.StereoChange;

public class StereoTransformation 
{
	public static IAtom DELETED_ATOM = new PseudoAtom();
	
	public static TetrahedralChirality deleteAtom(IAtom at,
			TetrahedralChirality thc,
			StereoChange stereoChange)
	{
		if (thc.getChiralAtom() == at)
			return null; //entire element will be removed since the deleted atom is the chiral center
		
		IAtom ligands[] = thc.getLigands();
		
		IAtom newLigands[] = deleteAtomFromLigands(at, ligands, stereoChange);
		
		if (ligands == newLigands)
			return thc;  //no change (atom is not found among the ligands)
		
		return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
	}
	
	
	static IAtom[] deleteAtomFromLigands(IAtom at, IAtom ligands[], StereoChange stereoChange)
	{	
		//Check for addLigandsAtoms.isEmpty is not performed since 
		//in SMIRKSManager atom deletion is performed always before atom addition 
		
		int n = -1; //index of the atom to be deleted
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == at)
			{
				n = i;
				break;
			}
		}
		
		if (n == -1) //Atom is not found between the ligands
			return ligands;
		
		if (stereoChange.addLigandsAtoms.isEmpty())
		{
			//Set the new ligands where ligand n is set to be PseudoAtom (i.e. it marked deleted) 
			IAtom newLigands[] = ligands.clone();
			newLigands[n] = DELETED_ATOM;
			stereoChange.ligand0Deleted = true;
			return newLigands;
		}
		else	
		{	
			//There is at least one extra ligand added 
			//and it will be put in place of the deleted atom
			IAtom extraLigandAt = stereoChange.addLigandsAtoms.get(0);
			
			//StereoChange update
			stereoChange.addLigandsAtoms.remove(0);
			
			IAtom newLigands[] = new IAtom[ligands.length-1];
			for (int i = 0; i < 4; i++)
				if (i == n)
					newLigands[i] = extraLigandAt;
				else
					newLigands[i] = ligands[i];
			
			return newLigands;
		}
	}
	
	public static TetrahedralChirality bondChange(IAtom at1, IAtom at2,
			IBond.Order initialBondOrder, IBond.Order updatedBondOrder, 
			IAtomContainer target,
			TetrahedralChirality thc,
			StereoChange stereoChange)
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
			
			IAtom newLigands[] = addAtomToLigands(ligAtom, ligands, stereoChange);
			//TODO check bond order 2 or 3
			return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
		}
		else
		{
			if (updatedBondOrder == null)
			{
				IAtom newLigands[] = deleteAtomFromLigands(ligAtom, ligands, stereoChange);
				return new TetrahedralChirality(thc.getChiralAtom(), newLigands, thc.getStereo());
			}
			
			//Bond order is changed hence no change is done to the stereo element
			//If inappropriate order is set, the final stereo check will clear this stereo element
			//TODO check what happens in the new CDK 2.x
			return thc;
		}
	}
	
	static IAtom[] addAtomToLigands(IAtom at, IAtom ligands[], StereoChange stereoChange)
	{
		int n = -1; //index of the first DELETED_ATOM/PseudiAtom ligand (if present)		
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == DELETED_ATOM)
			{
				n = i;
				break;
			}
		}
		
		if (n == -1)
		{
			//no DELETED_ATOM is present
			//the atom is added as an extra in StereoChange
			stereoChange.addLigandsAtoms.add(at);
			return ligands;
		}
		else
		{
			//a DELETED_ATOM is present
			//the atom is added in the place of the first DELETED_ATOM ligand (i.e. index n)
			ligands[n] = at;
			return ligands;
		}
	}
	
	//---------------- Utilities for double bond stereo transformation -------------------
	
	public static DoubleBondStereochemistry deleteAtom(IAtom at, DoubleBondStereochemistry dbsc, StereoChange stChange)
	{	
		if (dbsc.getStereoBond().contains(at))
			return null; //entire element will be removed since the deleted atom is part of the double bond

		//Stereo element is invalidated: 
		//bond is 'removed' i.e. it is designated in stereo change
		IBond bonds[] = dbsc.getBonds();

		if (bonds == null)
			return dbsc;

		IBond newBonds[] = deleteBondFromLigands(at, bonds, stChange);

		if (bonds == newBonds)
		{	
			//no change 
			//- bond containing the atom is not found among the ligand bonds or
			//- or bond is deleted and change is designated in StereoChange
			return dbsc; 
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

		
		if (nLigToDelete == 0)
		{	
			if (stChange.addLigands0.isEmpty())
			{	
				stChange.ligand0Deleted = true;
				return ligandBonds;
			}	
			else
			{
				//this should not be needed if atom deletions are performed first
				//Atoms is "deleted" but then
				//Transferring the first element of addLigands0
				IBond newBonds[] = ligandBonds.clone();
				newBonds[0] = stChange.addLigands0.get(0);
				stChange.addLigands0.remove(0);
				return newBonds;
			}
		}	
		else
		{	
			if (stChange.addLigands1.isEmpty())
			{	
				stChange.ligand1Deleted = true;
				return ligandBonds;
			}	
			else
			{
				//this should not be needed if atom deletions are performed first
				//Atoms is "deleted" but then
				//Transferring the first element of addLigands1
				IBond newBonds[] = ligandBonds.clone();
				newBonds[1] = stChange.addLigands1.get(0);
				stChange.addLigands1.remove(0);
				return newBonds;
			}
		}
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
				stChange.addLigands0.add(bo); //register new added ligand
		}
		else
		{
			//nLig == 1
			if (stChange.ligand1Deleted)
			{
				//when flag ligand0Deleted = true addLigands0 list must be empty
				IBond newLigandBonds[] = ligandBonds.clone();
				newLigandBonds[1] = bo;
				stChange.ligand1Deleted = false;
				return newLigandBonds;
			}
			else
				stChange.addLigands1.add(bo); //register new added ligand
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
			
			//TODO check the cases when new bond has order is not SINGLE ??
			
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
			
			//TODO check the cases when new bond has order is not SINGLE ??
			
			//Bond order is changed hence nothing is done to the stereo element
			//If inappropriate order is set, the final stereo check will clear this stereo element
			return dbsc;
		}
	}
	
	
}
