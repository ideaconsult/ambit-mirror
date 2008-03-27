package ambit2.database.query;

import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;

/**
 * A class to define a query for distance between atoms. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DistanceQuery extends DescriptorQuery {
	protected String[] atoms = {"",""};
	public DistanceQuery(String name, LiteratureEntry reference) {
		super(name, reference);

	}

	public DistanceQuery(String atom1, String atom2, double distance) {
		super("Distance"+atom1+"_"+atom2, 0,ReferenceFactory.createDatasetReference("distance between two atoms", "http://www.ambit2.acad.bg/ambit"));
		if (atom1.compareTo(atom2) < 0) {
			atoms[0] = atom1;
			atoms[1] = atom2;
		} else {
			atoms[1] = atom1;
			atoms[0] = atom2;			
		}
		setValue(distance);
	}	
	public DistanceQuery(String name, int type, LiteratureEntry reference) {
		super(name, type, reference);
	}

	public DistanceQuery(String name, int type, int dtype,
			LiteratureEntry reference) {
		super(name, type, dtype, reference);
	}

	public String[] getAtoms() {
		return atoms;
	}
	public void setAtoms(String atom1, String atom2) {
		if (atom1.compareTo(atom2) < 0) {
			this.atoms[0] = atom1;
			this.atoms[1] = atom2;
		} else {
			this.atoms[1] = atom1;
			this.atoms[0] = atom2;			
		}		
	}
	public void setAtoms(String[] atoms) {
		setAtoms(atoms[0],atoms[1]);
	}
	public void setAtom1(String atom1) {
		atoms[0] = atom1;
	}
	public void setAtom2(String atom2) {
		atoms[1] = atom2;
	}
		
	public String getAtom1() {
		return atoms[0];
	}
	public String getAtom2() {
		return atoms[1];
	}
	
	public String toSQL() {
		return toSQL("distance"+atoms[0]+"_"+atoms[1]);
	}
	public String toSQL(String field) {
		String a1 = atoms[0]; String a2 = atoms[1];
		setAtoms(a1,a2); //to get the right order
		//select idstructure,atom1,atom2,distance,atom_structure.iddistance from atom_structure join atom_distance using(iddistance)
		//where atom1="C" and atom2 = "O" and distance = 20
		StringBuffer b = new StringBuffer();
		b.append("select idstructure from atom_structure join atom_distance using(iddistance) where\n");
		b.append("atom1=\"");
		b.append(atoms[0]);
		b.append("\" and atom2=\"");
		b.append(atoms[1]);
		b.append("\" and distance ");
		b.append(condition2SQL());
		b.append('\n');
		return b.toString();
	}	
	public String toSQLOLD(String field) {
		String a1 = atoms[0]; String a2 = atoms[1];
		setAtoms(a1,a2); //to get the right order
		//select idstructure,atom1,atom2,distance,atom_structure.iddistance from atom_structure join atom_distance using(iddistance)
		//where atom1="C" and atom2 = "O" and distance = 20
		StringBuffer b = new StringBuffer();
		b.append("select idstructure,atom1,atom2,atom_structure.iddistance,distance as ");
		b.append(field);
		b.append(" from atom_structure join atom_distance using(iddistance) where\n");
		b.append("atom1=\"");
		b.append(atoms[0]);
		b.append("\" and atom2=\"");
		b.append(atoms[1]);
		b.append("\" and distance ");
		b.append(condition2SQL());
		b.append('\n');
		return b.toString();
	}
	public String getSQLTable(String alias) {
	    return "atom_structure as "+alias; 
	}
	public String id2SQL(String alias) {
	    StringBuffer b = new StringBuffer();
	    b.append("atom1=\"");
		b.append(atoms[0]);
		b.append("\" and atom2=\"");
		b.append(atoms[1]);
		b.append("\" and distance ");
		b.append(condition2SQL());
		return b.toString();
	}	
	public String toString() {
		return "Distance between atoms " + atoms[0] + " and " + atoms[1];
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String condition2SQL() {
		StringBuffer b = new StringBuffer();
		if (condition == 5) {
			b.append(getCondition());
			b.append(' ');
			b.append(nfSQL.format(Math.floor(minValue)));
			b.append(" and "); 
			b.append(nfSQL.format(Math.ceil(maxValue)));
			
		} else {
			b.append(getCondition());
			b.append(' ');
			b.append(nfSQL.format(value));
			
		}
		return b.toString();
	}
		
}
