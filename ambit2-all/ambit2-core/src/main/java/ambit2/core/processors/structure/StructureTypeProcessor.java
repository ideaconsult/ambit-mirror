package ambit2.core.processors.structure;

import java.util.Iterator;

import javax.vecmath.Point2d;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.groups.SuppleAtomContainer;

public class StructureTypeProcessor extends DefaultAmbitProcessor<IAtomContainer, STRUC_TYPE> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 470417245691484159L;

	public STRUC_TYPE process(IAtomContainer target) throws AmbitException {
		if (target == null) return STRUC_TYPE.NA;
		if (target.getAtomCount()==0) return STRUC_TYPE.NA;
		if (target instanceof SuppleAtomContainer) return STRUC_TYPE.MARKUSH;
		if (hasHydrogens(target)) 
			if (has2DCoordinates(target)>0) return STRUC_TYPE.D2withH;
			else if (has3DCoordinates(target)) return STRUC_TYPE.D3withH; 
			else 
				return STRUC_TYPE.D1;
		else
			if (has2DCoordinates(target)>0) return STRUC_TYPE.D2noH;
			else if (has3DCoordinates(target)) return STRUC_TYPE.D3noH; else 
				return STRUC_TYPE.D1;			
	}
	public static boolean hasHydrogens(IAtomContainer container) {
		Iterator<IAtom> atoms = container.atoms().iterator();
	    while (atoms.hasNext()) 
	    	if (atoms.next().getSymbol().equals("H")) return true;
	    
	    return false;
	}
	public static boolean has3DCoordinates(IAtomContainer container) {
		Iterator<IAtom> atoms = container.atoms().iterator();
	    while (atoms.hasNext()) 
	    	if (atoms.next().getPoint3d()!=null) return true;
	    
	    return false;
	}
	public static int has2DCoordinates(IAtomContainer container) {
		if (container == null) return 0;
		Point2d minC = null;
		Point2d maxC = null;
		
		boolean no2d=false;
		boolean with2d=false;
		Iterator<IAtom> atoms = container.atoms().iterator();
	    while (atoms.hasNext()) {
	    	IAtom atom = atoms.next();
	    	Point2d p = atom.getPoint2d();
	        if (p == null) {
	            no2d = true;
	        } else {
	        	if (minC == null) { minC = new Point2d(); minC.set(p.x,p.y);}
	        	else {
	        		if (p.x < minC.x) minC.x = p.x;
	        		if (p.y < minC.y) minC.y = p.y;
	        	}
	        	if (maxC == null) { maxC = new Point2d(); maxC.set(p.x,p.y); }
	        	else {
	        		if (p.x > maxC.x) maxC.x = p.x;
	        		if (p.y > maxC.y) maxC.y = p.y;
	        	}
	            with2d = true;
	        }
	    }
	    if ((minC != null) && (maxC!=null))
	    	with2d = (((minC.x-maxC.x)==0) && ((minC.y-maxC.y)==0))?false:with2d;
		if(!no2d && with2d){
			return 2;
		} else if(no2d && with2d){
			return 1;
		} else{
			return 0;
		}
	}

}
