package ambit2.core.data;



import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import ambit2.core.io.Property;

/**
 * A set of properties, defining a (e.g. toxicological) profile
 * @author nina
 *
 */
public class Profile extends Hashtable<String, Property> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818041819278493531L;
	
	/**
	 * 
	 * @return
	 */
	public Iterator<Property> getProperties(final boolean enabled) {
		return new Iterator<Property>() {
			Property nextProperty = null;
			Enumeration<Property> properties = elements();
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

}
