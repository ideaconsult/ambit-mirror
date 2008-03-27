/*
 * Created on 2005-9-3
 *
 */
package ambit2.data.molecule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMoleculeSet;

import ambit2.log.AmbitLogger;
import ambit2.ui.editors.IdentifiersProcessor;


/**
 * A class to store a set of molecules, implementing both bidirectional iterator {@link ambit2.data.molecule.IMoleculesIterator} and {@link org.openscience.cdk.interfaces.ISetOfAtomContainers}.
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-3
 */
public class MoleculesIterator extends ChemObject implements IMoleculesIterator, IAtomContainerSet {
	
	IAtomContainerSet containers = null;
	protected int currentNo = 0;	
    protected static AmbitLogger logger = null;
    protected PropertyTranslator availableProperties;
    protected SortedPropertyList sortedProperties;

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8781174694308416291L;

	/**
	 * 
	 */
	public MoleculesIterator() {
		this(new ListOfAtomContainers());
	}	
	public MoleculesIterator(IAtomContainerSet atomContainers) {
		super();
		containers = atomContainers;
        //containers.addAtomContainer(new org.openscience.cdk.Molecule());
        first();        
        if (logger == null) logger = new AmbitLogger();
        sortedProperties = new SortedPropertyList("");
        availableProperties = new PropertyTranslator();
        
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		containers.removeAtomContainer(currentNo);
        sortedProperties.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (containers == null) return false;
		return ((currentNo) < containers.getAtomContainerCount());
	}

	/* (non-Javadoc)
     * @see ambit2.io.IRandomAccessChemObjectReader#hasPrev()
     */
    public boolean hasPrevious() {
		if (containers == null) return false;
		return ((currentNo) > 0);
    }
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
       	currentNo++;
		return getAtomContainer();
	}
	public int nextIndex() {
		return currentNo+1;
	}
	public int previousIndex() {
		return currentNo-1;
	}
	public Object setSelectedIndex(int index) {
	    currentNo = index;
	    return getAtomContainer();
	}
	/**
	 * @return Returns the molecules.
	 */
	public  Iterator<IAtomContainer> getAtomContainers() {
		return containers.atomContainers();
	}
	/**
	 * @param molecules The molecules to set.
	 */
	public void setAtomContainers(IAtomContainer[] molecules) {
        sortedProperties.clear();
		containers.removeAllAtomContainers();
		for (int i=0; i < molecules.length; i++ )
			containers.addAtomContainer(molecules[i]);
		first();
	}
	/**
	 * @return Returns the currentNo.
	 */
	public int getSelectedIndex() {
		return currentNo;
	}
	public void removeAllAtomContainers() {
		currentNo = -1;
		containers.removeAllAtomContainers();
        sortedProperties.clear();
	}
    public Object previous() {
        if (containers == null) currentNo = 0;
        else { 
        	currentNo--;
        	if (currentNo < 0) currentNo = 0;
        }
        return getAtomContainer();
    }   
	
    public Object first() {
        currentNo = 0;
		return getAtomContainer();
    }
    public Object last() {
        if (containers == null)	currentNo = 0;
        else  { 
        	currentNo = containers.getAtomContainerCount()-1;
        }
        return getAtomContainer();
    }   	
	/**
	 * @return Returns the molecule.
	 */
	public IAtomContainer getAtomContainer() {
		if ((currentNo >= 0) && (currentNo < containers.getAtomContainerCount()))
			return containers.getAtomContainer(currentNo);
		else 
			return null;
	}
	/* (non-Javadoc)
     * @see ambit2.io.IRandomAccessChemObjectReader#readRecord(int)
     */
    public Object readRecord(int record) throws Exception {
        setSelectedIndex(record);
        return getAtomContainer();
    }
	public int getAtomContainerCount() {
		return containers.getAtomContainerCount();
	}

	public void add(Object arg0) {
		if (arg0 instanceof IAtomContainer)
			addAtomContainer((IAtomContainer) arg0);
	}
	public void addAtomContainer(IAtomContainer mol) {
		containers.addAtomContainer(mol);
		last();
        sortedProperties.clear();
	}
	public void set(Object arg0) {
		if (arg0 instanceof IAtomContainer)
		if (containers instanceof ListIterator)
				((ListIterator)containers).set(arg0);
		if (containers instanceof ArrayList)
		    ((ArrayList)containers).set(currentNo,arg0);
	}
	public String toString() {
		if (containers != null) return getAtomContainerCount() + " molecules";
		else return "";
	}
	public IMoleculeSet getSetOfAtomContainers() {
        sortedProperties.clear();
		IMoleculeSet c = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		for (int i=0; i< containers.getAtomContainerCount();i++)
			c.addAtomContainer(containers.getAtomContainer(i));
		return c;
	}

	public IAtomContainer getAtomContainer(int index) {
		return containers.getAtomContainer(index);
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#setMultiplier(org.openscience.cdk.interfaces.IAtomContainer, double)
     */
    public boolean setMultiplier(IAtomContainer container, double multiplier) {
        return containers.setMultiplier(container,multiplier);
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#add(org.openscience.cdk.interfaces.ISetOfAtomContainers)
     */
    public void add(IAtomContainerSet atomContainerSet) {
        containers.add(atomContainerSet);
        sortedProperties.clear();

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#addAtomContainer(org.openscience.cdk.interfaces.IAtomContainer, double)
     */
    public void addAtomContainer(IAtomContainer atomContainer, double multiplier) {
        containers.addAtomContainer(atomContainer,multiplier);
        sortedProperties.clear();

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#addListener(org.openscience.cdk.interfaces.IChemObjectListener)
     */
    public void addListener(IChemObjectListener col) {
        containers.addListener(col);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        return containers.clone();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getBuilder()
     */
    public IChemObjectBuilder getBuilder() {
        return containers.getBuilder();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getFlag(int)
     */
    public boolean getFlag(int flag_type) {
        return containers.getFlag(flag_type);
    }
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getFlags()
     */
    public boolean[] getFlags() {
        return containers.getFlags();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getID()
     */
    public String getID() {
        return containers.getID();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getListenerCount()
     */
    public int getListenerCount() {
        return containers.getListenerCount();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#getMultiplier(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public double getMultiplier(IAtomContainer container) {
        return containers.getMultiplier(container);
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#getMultiplier(int)
     */
    public double getMultiplier(int number) {
        return containers.getMultiplier(number);
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#getMultipliers()
     */
    public double[] getMultipliers() {
        return containers.getMultipliers();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getNotification()
     */
    public boolean getNotification() {
        return containers.getNotification();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getProperties()
     */
    public Map getProperties() {
        return containers.getProperties();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#getProperty(java.lang.Object)
     */
    public Object getProperty(Object description) {
        return containers.getProperty(description);
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#notifyChanged()
     */
    public void notifyChanged() {
        containers.notifyChanged();

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#notifyChanged(org.openscience.cdk.interfaces.IChemObjectChangeEvent)
     */
    public void notifyChanged(IChemObjectChangeEvent evt) {
        containers.notifyChanged(evt);
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#removeAtomContainer(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public void removeAtomContainer(IAtomContainer atomContainer) {
        containers.removeAtomContainer(atomContainer);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#removeAtomContainer(int)
     */
    public void removeAtomContainer(int pos) {
        containers.removeAtomContainer(pos);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#removeListener(org.openscience.cdk.interfaces.IChemObjectListener)
     */
    public void removeListener(IChemObjectListener col) {
        containers.removeListener(col);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#removeProperty(java.lang.Object)
     */
    public void removeProperty(Object description) {
        containers.removeProperty(description);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#setFlag(int, boolean)
     */
    public void setFlag(int flag_type, boolean flag_value) {
        containers.setFlag(flag_type,flag_value);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.IChemObject#setFlags(boolean[])
     */
    public void setFlags(boolean[] flagsNew) {
        containers.setFlags(flagsNew);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#setMultiplier(int, double)
     */
    public void setMultiplier(int position, double multiplier) {
        containers.setMultiplier(position,multiplier);

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.interfaces.ISetOfAtomContainers#setMultipliers(double[])
     */
    public boolean setMultipliers(double[] newMultipliers) {
        return containers.setMultipliers(newMultipliers);
    }
    public Map getProperties(int index) {
    	return containers.getAtomContainer(index).getProperties();
    }
    public Object getProperty(int index, Object arg0) {
    	if ((arg0 != null) && (containers.getAtomContainer(index)!=null)) {
    		Object key = availableProperties.getProperty(PropertyTranslator.type_identifiers, arg0);
    		if (key == null)
    			key = availableProperties.getProperty(PropertyTranslator.type_descriptors, arg0);
    		else 
    			key = availableProperties.getProperty(PropertyTranslator.type_results, arg0);
    		if (key == null) key = arg0;
    		
    		return containers.getAtomContainer(index).getProperty(key);
    	} else return null;
    }
    public void setProperty(int index, Object key, Object value) {
    	containers.getAtomContainer(index).setProperty(key,value);
    	
    }
    public void close() {
        // TODO Auto-generated method stub
        
    }
	public PropertyTranslator getAvailableProperties() {
		return availableProperties;
	}
    public void clearAvailableProperties() {
        if (availableProperties != null) availableProperties.clear();
        updateTranslator();
    }
	public void addAvailableProperties(PropertyTranslator availableProperties) {
		this.availableProperties.add(availableProperties);
        updateTranslator();
    }    
    public void updateTranslator() {	
		IdentifiersProcessor ip = new IdentifiersProcessor();
		if (availableProperties != null) {
			Hashtable h = availableProperties.getIdentifiers();
			if (h != null)
				ip.addIdentifiers(h);
			h = availableProperties.getDescriptorProperties();
			if (h != null)
				ip.addIdentifiers(availableProperties.getDescriptorProperties());
		}	
        for (int i=0; i < containers.getAtomContainerCount();i++)
        	try {
        		ip.process(containers.getAtomContainer(i));
        	} catch (Exception x) {
        		x.printStackTrace();
        	}
	}
    public int indexOf(Object property,Object value) throws Exception {
        if ((sortedProperties.size()==0) || (!sortedProperties.getPropertyName().equals(property))) {
            sortedProperties.setPropertyName(property);
            sortedProperties.addList(this);
        }    
        return sortedProperties.getOriginalIndexOf(value);
    }
    public void sortBy(Object property, boolean ascending) throws Exception {
        if ((sortedProperties.size()==0) || (!sortedProperties.getPropertyName().equals(property))) {
            sortedProperties.clear();
            sortedProperties.setPropertyName(property);
            sortedProperties.addList(this);
        }    
        sortedProperties.sort(ascending);
     //   System.out.println(sortedProperties);
        
    }
    public Collection<Comparable> getValuesPerField(Object field,int limit) {
        ArrayList<Comparable> values = new ArrayList<Comparable>();
        for (int i=0; i < getAtomContainerCount();i++) {
            Object o = getProperty(i, field);
            if (o==null) continue;
            Comparable c=null;
            if (o instanceof Comparable) c = (Comparable)o;
            else c = o.toString();
            if (!values.contains(c)) values.add(c); 
            if (values.size()==limit) break;
        }
        return values;
    }
	public Iterator<IAtomContainer> atomContainers() {
		return containers.atomContainers();
	}
	public void replaceAtomContainer(int arg0, IAtomContainer arg1) {
		containers.replaceAtomContainer(arg0, arg1);
		
	}
}
