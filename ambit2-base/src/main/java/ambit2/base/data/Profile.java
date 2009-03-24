package ambit2.base.data;



import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * A set of properties, defining a (e.g. toxicological) profile
 * @author nina
 *
 */
public class Profile<P extends Property> {
	protected Hashtable<String,P> container;
	protected String name;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818041819278493531L;
	public Profile() {
		container = new Hashtable<String, P>();
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
			Enumeration<P> properties = container.elements();
		public boolean hasNext() {
				while (properties.hasMoreElements()) {
					nextProperty = properties.nextElement();
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
	public P get(String key) {
		return container.get(key);
	}
	public P get(P query) {
		return container.get(query.getName());
	}	
	public void add(P property) {
		container.put(property.getName(),property);
	}

	public int size() {
		return container.size();
	}
	public Collection<P> values() {
		return container.values();
	}
	public void clear() {
		container.clear();
	}
	public void add(Profile<P> profile, boolean enabled) {
		Iterator<P> i = profile.getProperties(enabled);
		while (i.hasNext()) 
			add(i.next());
	}
}
