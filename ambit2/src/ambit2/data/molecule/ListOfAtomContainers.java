package ambit2.data.molecule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

import ambit2.log.AmbitLogger;
/**
 * An attempt to implement {@link org.openscience.cdk.interfaces.ISetOfAtomContainers} interface 
 * as a {@link java.util.List} 
 * @author Nina Jeliazkova
 *
 */
public class ListOfAtomContainers extends ArrayList<IAtomContainer> implements IAtomContainerSet {
	protected transient AmbitLogger logger = new AmbitLogger(ListOfAtomContainers.class);
	protected String id = "";
	protected Map properties = null;
	protected boolean notification = true;
	
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

	public ListOfAtomContainers(Collection<IAtomContainer> arg0) {
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

	public void add(IAtomContainerSet atomContainerSet) {
		for (int i=0; i< atomContainerSet.getAtomContainerCount(); i++) 
			addAtomContainer(atomContainerSet.getAtomContainer(i));
	}


	public IAtomContainer getAtomContainer(int number) {
	    if ((number >=0) && (number < size())) 
	        return get(number);
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
		

	}

	public void setProperty(Object description, Object property) {
		properties.put(description,property);
	}

	public void removeProperty(Object description) {
		properties.remove(description);
	}

	public Object getProperty(Object description) {
		return properties.get(description);
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
        this.notification = bool;

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getNotification()
     */
    public boolean getNotification() {
        return notification;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#notifyChanged(org.openscience.cdk.interfaces.IChemObjectChangeEvent)
     */
    public void notifyChanged(IChemObjectChangeEvent evt) {
        // TODO Auto-generated method stub

    }

	public Iterator<IAtomContainer> atomContainers() {
		return iterator();
	}

	public void replaceAtomContainer(int arg0, IAtomContainer arg1) {
		set(arg0,arg1);
		
	}

	public void setProperties(Map<Object, Object> arg0) {
		this.properties = arg0;

		
	}
    public Map<Object, Object> getProperties() {
    	// TODO Auto-generated method stub
    	return this.properties;
    }
  
}
