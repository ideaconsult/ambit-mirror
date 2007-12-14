/**
 * Created on 2005-3-21
 *
 */
package ambit.data.literature;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.AuthorEntryTableModel;
import ambit.ui.data.literature.JournalEntryTableModel;



/**
 * A journal (name, abbreviation, publisher) <br>
 * To be used in {@link LiteratureEntry} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class JournalEntry extends AmbitObject {
	protected String abbreviation = "";
	protected String publisher = "";
	protected String city = "";
	
	/**
	 * Empty constructor
	 *
	 */
	public JournalEntry() {
		super();
	}
	/**
	 * name same as abbreviation
	 * Empty , publisher, city
	 * @param abbreviation
	 */
	public JournalEntry(String abbreviation) {
		super(abbreviation);
		this.abbreviation = abbreviation;
	}
	/**
	 * empty publisher, city
	 * @param abbreviation
	 * @param name
	 */
	public JournalEntry(String abbreviation, String name) {
		super(name);
		this.abbreviation = abbreviation;
	}
	public void clear() {
		super.clear();
		abbreviation = "";
		publisher = "";
		city = "";
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		boolean m =  !this.city.equals(city);
		this.city = city;
		setModified(m);
	}

	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		boolean m = !this.publisher.equals(publisher);		
		this.publisher = publisher;
		setModified(m);
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		boolean m = !this.abbreviation.equals(abbreviation);		
		this.abbreviation = abbreviation;
		setModified(m);
		
	}
	public Object clone() {
		JournalEntry j = new JournalEntry(abbreviation,name);
		j.setPublisher(publisher);
		j.setCity(city);
		j.setId(id);
		return j;
	}
	public String toString() {
		return abbreviation;
	}
	public void setName(String name){
		super.setName(name);
		if (abbreviation.equals("")) abbreviation = name;
	}
	
	public boolean equals(Object obj) {
		return super.equals(obj) && 
		abbreviation.equals(((JournalEntry) obj).abbreviation) &&
		publisher.equals(((JournalEntry) obj).publisher) &&
		city.equals(((JournalEntry) obj).city)				
				;
	}
	public IAmbitEditor editor(boolean editable) {
		return new AbstractAmbitEditor("Journal",this) {
			protected ambit.ui.data.AbstractPropertyTableModel createTableModel(AmbitObject object) {
				 return new JournalEntryTableModel((JournalEntry)object);
			}
		};
	}
		
}
