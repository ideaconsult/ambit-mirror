package ambit.data.molecule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;

import ambit.log.AmbitLogger;
/**
 * An attempt to implement {@link org.openscience.cdk.interfaces.ISetOfAtomContainers} interface 
 * as a {@link java.util.List} 
 * @author Nina Jeliazkova
 *
 */
public class ListOfAtomContainers extends ArrayList implements
		ISetOfAtomContainers {
	protected transient AmbitLogger logger = new AmbitLogger(ListOfAtomContainers.class);
	protected String id = "";
	protected Hashtable properties = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7586214506838048913L;

	public ListOfAtomContainers() {
		super();
	}

	public ListOfAtomContainers(int arg0) {
		super(arg0);
	}

	public ListOfAtomContainers(Collection arg0) {
		super(arg0);
		
	}

	public void addAtomContainer(IAtomContainer atomContainer) {
		add(atomContainer);
	}

	public void removeAtomContainer(IAtomContainer atomContainer) {
		remove(atomContainer);
	}

	public void removeAllAtomContainers() {
		clear();
	}

	public void removeAtomContainer(int pos) {
		remove(pos);
	}

	public boolean setMultiplier(IAtomContainer container, double multiplier) {
		return false;
	}

	public void setMultiplier(int position, double multiplier) {
		// TODO Auto-generated method stub

	}

	public double[] getMultipliers() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setMultipliers(double[] newMultipliers) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addAtomContainer(IAtomContainer atomContainer, double multiplier) {
		addAtomContainer(atomContainer);
	}

	public void add(ISetOfAtomContainers atomContainerSet) {
		for (int i=0; i< atomContainerSet.getAtomContainerCount(); i++) 
			addAtomContainer(atomContainerSet.getAtomContainer(i));
	}
	public IAtomContainer[] getAtomContainers() {
		try {
			AtomContainer[] m = new AtomContainer[getAtomContainerCount()];
			for (int i=0; i<size();i++) 
				m[i] = (AtomContainer) get(i);
			return m;
		} catch (Exception x) {
			logger.error(x);
			return null;
		}
	}

	public IAtomContainer getAtomContainer(int number) {
	    if ((number >=0) && (number < size())) 
	        return (IAtomContainer)get(number);
	    else return null;
	}

	public double getMultiplier(int number) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMultiplier(IAtomContainer container) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAtomContainerCount() {
		return size();
	}

	public void addListener(IChemObjectListener col) {
		// TODO Auto-generated method stub

	}

	public int getListenerCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#removeListener(org.openscience.cdk.interfaces.IChemObjectListener)
     */
    public void removeListener(IChemObjectListener col) {
        // TODO Auto-generated method stub

    }
	public void notifyChanged() {
		// TODO Auto-generated method stub

	}

	public void notifyChanged(ChemObjectChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public void setProperty(Object description, Object property) {
		properties.put(description,property);
	}

	public void removeProperty(Object description) {
		properties.remove(description);
	}

	public Object getProperty(Object description) {
		// TODO Auto-generated method stub
		return null;
	}

	public Hashtable getProperties() {
		return properties;
	}

	public String getID() {
		return id;
	}

	public void setID(String identifier) {
		this.id = identifier;
	}

	public void setFlag(int flag_type, boolean flag_value) {
		// TODO Auto-generated method stub

	}

	public boolean getFlag(int flag_type) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setProperties(Hashtable properties) {
		this.properties = properties;

	}

	public void setFlags(boolean[] flagsNew) {
		// TODO Auto-generated method stub

	}

	public boolean[] getFlags() {
		// TODO Auto-generated method stub
		return null;
	}

	public IChemObjectBuilder getBuilder() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#setNotification(boolean)
     */
    public void setNotification(boolean bool) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getNotification()
     */
    public boolean getNotification() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#notifyChanged(org.openscience.cdk.interfaces.IChemObjectChangeEvent)
     */
    public void notifyChanged(IChemObjectChangeEvent evt) {
        // TODO Auto-generated method stub

    }
    
}
