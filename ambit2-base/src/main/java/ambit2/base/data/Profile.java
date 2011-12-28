package ambit2.base.data;



import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * A set of properties, defining a (e.g. toxicological) profile
 * @author nina
 *
 */
public class Profile<P extends Property> /*implements Collection<P> */ implements Serializable {
	static public String profile_property_change = "property_change";
	static public String profile_property_added = "property_added";
	static public String profile_property_removed = "property_removed";
	transient PropertyChangeSupport ps = new PropertyChangeSupport(this);
	protected ArrayList<P> container;
	protected String name;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818041819278493531L;
	public Profile() {
		container = new ArrayList<P>();
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	/**
	 * 
	 * @return
	 */
	public Iterator<P> getProperties(final boolean enabled) {
		return new Iterator<P>() {
			P nextProperty = null;
			Iterator<P> properties = container.iterator();
		public boolean hasNext() {
				while (properties.hasNext()) {
					nextProperty = properties.next();
					if (nextProperty.isEnabled() == enabled) 
						return true;
				} 
				return false;
			}

		public P next() {
			return nextProperty;
		}

		public void remove() {
			
		}	
		};
		
		
	}
	/*
	public P get(String key) {
		return container.get(key);
	}
	*/
	public P get(P query) {
		int index = container.indexOf(query);
		if (index < 0) return null;
		else return container.get(index);
	}	
	public void setChanged() {
		ps.firePropertyChange(profile_property_change,null,this);
	}
	public boolean add(P property) {
		if (property.getOrder()<=0)
			property.setOrder(size()+1000);
		ps.firePropertyChange(profile_property_added,null,property);
		if(container.contains(property))  return false;
		container.add(property);
		return true;
	}

	public int size() {
		return container.size();
	}
	public Collection<P> values() {
		return container;
	}
	public void clear() {
		ps.firePropertyChange("profile",this,null);		
		container.clear();
	}
	public void add(Profile<P> profile, boolean enabled) {
		Iterator<P> i = profile.getProperties(enabled);
		while (i.hasNext()) 
			add(i.next());
	}
	public Iterator<P> iterator() {
		return values().iterator();
	}
/*

	public boolean addAll(Collection<? extends P> c) {
		int count = 0;
		Iterator<? extends P> i = c.iterator();
		while (i.hasNext()) {
			P p = i.next();
			container.put(p.getName(),p);
			count++;
		}
		return count==c.size();
	}


	public boolean contains(Object o) {
		return container.values().contains(o);
	}


	public boolean containsAll(Collection<?> c) {
		return container.values().containsAll(c);
	}


	public boolean isEmpty() {
		return container.size()==0;
	}


	public boolean remove(Object o) {
		Enumeration<String> keys = container.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (o.equals(container.get(key))) {
				container.remove(key);
			}
		}
		return true;
	}


	public boolean removeAll(Collection<?> c) {
		Enumeration<String> keys = container.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (c.contains(container.get(key))) {
				container.remove(key);
			}
		}
		return true;
	}


	public boolean retainAll(Collection<?> c) {
		Enumeration<String> keys = container.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (!c.contains(container.get(key))) {
				container.remove(key);
			}
		}
		return true;
		
	}


	public Object[] toArray() {
		return container.values().toArray();
	}


	public <T> T[] toArray(T[] a) {
		return container.values().toArray(a);
	}

*/
	public void addPropertyChangeListener(String name,PropertyChangeListener listener) {
		ps.addPropertyChangeListener(name,listener);
	}
	public void removePropertyChangeListener(String name,PropertyChangeListener listener) {
		ps.removePropertyChangeListener(name,listener);
	}	

}
