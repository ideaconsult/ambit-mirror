package ambit2.data.molecule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.log.AmbitLogger;

/**
 * This is a container class, holding {@link ambit2.data.molecule.IMoleculesIterator} object.
 * Used in {@link ambit2.data.IDataContainers} and {@link ambit2.applications.dbadmin.AmbitDatabase} application.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DataContainer extends Observable implements PropertyChangeListener {
	protected static String p_select = "select";
	protected static AmbitLogger logger = null;
	protected IMoleculesIterator containers = null;
	protected boolean modified = false;
	protected boolean enabled = true;

    public DataContainer() {
        super();
		if (logger == null) logger = new AmbitLogger(DataContainer.class);
        containers = new MoleculesIterator();
        //if (inputFile != null) 
        	//openFile(inputFile);
		setChanged();
		notifyObservers();        

    }
    public IAtomContainer getMolecule() {
        return containers.getAtomContainer(containers.getSelectedIndex());
    }    

    public void clear() {
    	containers.removeAllAtomContainers();
    	containers.clearAvailableProperties();
    }
    public void newMolecule() {
    	if (!enabled) return;
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	mol.setProperty("SMILES","C");
    	newMolecule(mol);
    }
    public void newMolecule(IAtomContainer molecule) {
    	if (!enabled) return;
    	containers.removeAllAtomContainers();
    	addMolecule(molecule);
    }
    
    public void addMolecule() {
    	if (!enabled) return;
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	addMolecule(mol);
    }

    public void addMoleculeNoNotify(IAtomContainer molecule) {
    	if (!enabled) return;
        containers.addAtomContainer(molecule);
    }
    
    public void addMolecule(IAtomContainer molecule) {
    	if (!enabled) return;
        containers.addAtomContainer(molecule);
        modified = true;        
        containers.last();
        setChanged();
        notifyObservers();
    }
    
    public void setMolecule(IAtomContainer molecule) {
    	if (!enabled) return;
    	if (containers.getAtomContainerCount() == 0) addMolecule(molecule);
    	else   	containers.set(molecule);
        modified = true;    	
        setChanged();
        notifyObservers();    	
    }

	public int getMoleculesCount() {
		return containers.getAtomContainerCount();
	}
	public int getCurrentNo() {
		return containers.getSelectedIndex();
	}
	public Object first() {
		if (!enabled) return null;
		Object o = containers.first();
		setChanged();
		notifyObservers();
		return o;
	}
	public Object last() {
		if (!enabled) return null;
		Object o = containers.last();
		setChanged();
		notifyObservers();
		return o;		
	}
	public Object next() {
		if (!enabled) return null;
		if (containers.hasNext()) {
			Object o = containers.next();
			if (o==null) o = containers.last();
			setChanged();
			notifyObservers();
			return o;
		} else return null;
				
	}
	public Object prev() {
		if (!enabled) return null;
		Object o = containers.previous();
		setChanged();
		notifyObservers();
		return o;		
	}
	public Object setSelectedIndex(int index) {
		if (containers == null) return null;
		if (!enabled) return null;
		Object o = containers.setSelectedIndex(index);
		setChanged();
		notifyObservers();
		return o;		
		
	}
	public int getSelectedIndex() {
		return containers.getSelectedIndex();
	}
	public String toString() {
		return containers.toString();
	}

	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setChanged();
		notifyObservers();		
	}

	public IIteratingChemObjectReader getReader() {
		return new MoleculesIteratorReader(containers); 
	}
    public synchronized IMoleculesIterator getContainers() {
        return containers;
    }
    public synchronized void setContainers(IMoleculesIterator containers) {
        this.containers = containers;
    }
    public void propertyChange(PropertyChangeEvent e) {
    	if (e.getPropertyName().equals(p_select)) {
    		Integer o = (Integer)e.getNewValue();
    		if (o != null)
    			setSelectedIndex(o.intValue());
    	}
    	
    }
}
