package ambit.data.descriptors;

import java.io.Serializable;

/**
 * An atom environment. Used by  {@link ambit.processors.structure.AtomEnvironmentGenerator} 
 * and {@link ambit.database.writers.AtomEnvironmentWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class AtomEnvironment implements Comparable<AtomEnvironment> , Serializable {
	protected int levels;
	protected int atomno;
	protected String central_atom;
	protected int[] atom_environment;
	protected long time_elapsed;
	protected int status;
	protected int frequency = 0;
	protected transient int sublevel;
	
	/**
	 * 3-levels
	 *
	 */
	public AtomEnvironment() {
		this(3);
	}
	public AtomEnvironment(int levels) {
		this(-1,"",null,levels,0,3);
	}
	public AtomEnvironment(int atomNo, String central_atom, int[] atom_environment,int levels,long  time_elapsed, int status) {
		super();
		this.levels = levels;
		this.sublevel = levels;
		this.atomno = atomNo;
		this.central_atom = central_atom;
		this.atom_environment = atom_environment;
		this.time_elapsed = time_elapsed;
		this.status = status;
	}
    public static String atomFingerprintToString(int[] fpae,char delimiter) {
    	if (fpae == null) return "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fpae.length; i++) {
			buf.append(fpae[i]);
			buf.append(delimiter);
		}
		return buf.toString();	        
    }
    /*
	public int[] getAtom_environment(int level) {

		int atomtypelength = (atom_environment.length-2) / levels;
		int[] subArray = new int[atomtypelength];		
		System.arraycopy(atom_environment,level*atomtypelength,subArray,0,atomtypelength);
		return subArray;
	}
	*/
	public AtomEnvironment getSubAtomEnvironment(int level) {
		
		int atomtypelength = (atom_environment.length-2)/levels -1;
		int[] subArray = new int[atom_environment.length];
		
		subArray[0] = 0;
		for (int i=1;i < subArray.length;i++) {
			
			if (i > (atomtypelength*level+2)) subArray[i] = 0;
			else subArray[i] = atom_environment[i];
			//control sum on the fly
			subArray[0] += subArray[i];
		}
		
		
		AtomEnvironment ae = new AtomEnvironment(atomno,central_atom,subArray,levels,time_elapsed,status);
		return ae;
	}
	
	
	public int[] getAtom_environment() {
		return atom_environment;
	}

	public void setAtom_environment(int[] atom_environment) {
		this.atom_environment = atom_environment;
	}

	public int getAtomno() {
		return atomno;
	}

	public void setAtomno(int atomno) {
		this.atomno = atomno;
	}

	public String getCentral_atom() {
		return central_atom;
	}

	public void setCentral_atom(String central_atom) {
		this.central_atom = central_atom;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
    public void incFrequency() {
        this.frequency++;
    }
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTime_elapsed() {
		return time_elapsed;
	}

	public void setTime_elapsed(long time_elapsed) {
		this.time_elapsed = time_elapsed;
	}

	public String toString() {
		return central_atom + " " + atomFingerprintToString(atom_environment,'\t');
	}
	public int compareTo(AtomEnvironment arg0) {
		return toString().compareTo(arg0.toString());
	}
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof AtomEnvironment) {
    		AtomEnvironment e = ((AtomEnvironment) obj);
    		if (central_atom.equals(e.getCentral_atom())) {
    			for (int i=0; i < atom_environment.length; i++)
    				if (atom_environment[i] != e.getAtom_environment()[i]) return false;
    			return true;
    		}
    	} 
    	return false;
    	
        //return toString().equals(obj.toString());
    }
	public int getLevels() {
		return levels;
	}
	public int getSublevel() {
		return sublevel;
	}
	public void setSublevel(int sublevel) {
		this.sublevel = sublevel;
	}

}
