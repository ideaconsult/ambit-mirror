package ambit2.core.data;



import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import ambit2.core.io.Property;

/**
 * A set of properties, defining a (e.g. toxicological) profile
 * @author nina
 *
 */
public class Profile {
	protected Hashtable<String,Property> container;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5818041819278493531L;
	public Profile() {
		container = new Hashtable<String, Property>();
	}
	/**
	 * 
	 * @return
	 */
	public Iterator<Property> getProperties(final boolean enabled) {
		return new Iterator<Property>() {
			Property nextProperty = null;
			Enumeration<Property> properties = container.elements();
		public boolean hasNext() {
				while (properties.hasMoreElements()) {
					nextProperty = properties.nextElement();
					if (nextProperty.isEnabled() == enabled) 
						return true;
				} 
				return false;
			}

		public Property next() {
			return nextProperty;
		}

		public void remove() {
			
		}	
		};
		
		
	}
	public Property get(String key) {
		return container.get(key);
	}
	public void add(Property property) {
		container.put(property.getName(),property);

	}
	public int size() {
		return container.size();
	}
	public Collection<Property> values() {
		return container.values();
	}
}
