/**
 * Created on 2005-3-21
 *
 */
package ambit.data.literature;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.literature.LiteratureEntryTableModel;

/**
 * A Literature entry: <br>
 * title, <@link JournalEntry> , {@link AuthorEntries}, www  
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class LiteratureEntry extends AmbitObject {
	protected JournalEntry journal = null;
	protected AuthorEntries authors = null;
	protected String www = "";
	
	public String getWWW() {
		return www;
	}
	public void setWWW(String url) {
		this.www = url;
	}
	protected String volume = "";
	protected String pages = "";
	protected int year=-1;

	
	/**
	 * 
	 */
	public LiteratureEntry() {
		super();
		authors = new AuthorEntries();
		journal = new JournalEntry();
	}

	public LiteratureEntry(String title, JournalEntry journal, 
			String volume, String pages, int year, AuthorEntries authors) {
		super();
		this.authors = authors;
		this.journal = journal;
		this.name = title;
		this.volume = volume;
		this.pages = pages;
		this.year = year;
	}
	public void clear() {
		super.clear();
		journal.clear();
		authors.clear();
		www = "";		
	}
	public AuthorEntry addAuthor(AuthorEntry author) {
		authors.addItem(author);
		return author; 
	}
	public AuthorEntry addAuthor(String name) {
		AuthorEntry author = new AuthorEntry(name);		
		authors.addItem(author);
		return author;
	}	
	public AuthorEntries getAuthors() {
		return authors;
	}
	public void setAuthors(AuthorEntries authors) {
		boolean m = !this.authors.equals(authors);		
		this.authors = authors;
		setModified(m);
	}

	public JournalEntry getJournal() {
		return journal;
	}
	public void setJournal(JournalEntry journal) {
		boolean m = true;
		if (this.journal != null) 
			m = !this.journal.equals(journal);		
		this.journal = journal;
		setModified(m);
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		boolean m = !this.pages.equals(pages);		
		this.pages = pages;
		setModified(m);
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		boolean m =  !this.volume.equals(volume);		
		this.volume = volume;
		setModified(m);
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		boolean m = (this.year != year);
		this.year = year;
		setModified(m);
	}
	
	public Object clone()  throws CloneNotSupportedException {
		LiteratureEntry ref = new LiteratureEntry(name,
				(JournalEntry) journal.clone(),
				volume,pages,year,
				(AuthorEntries) authors.clone()
				);
		ref.setWWW(www);
		return ref;
	}
	
	public boolean equals(Object obj) {
		LiteratureEntry exp = (LiteratureEntry) obj;
		return super.equals(obj) &&
		   	   (journal.equals(exp.journal)) &&
			   (www.equals(exp.getWWW())) &&
			   (authors.equals(exp.getAuthors()) );
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String a = authors.toString();
		if (!a.equals("" )) {
			buf.append(a);
			buf.append(',');	
		}
		buf.append('"');
		buf.append(name);
		buf.append('"');
		if (!journal.toString().equals("")) { 
			buf.append(',');		
			buf.append(journal.toString());
		}
		if (!volume.equals("")) {
			buf.append(",Vol.");
			buf.append(volume);
		}
		if (!pages.equals("")) {
			buf.append(",pp.");
			buf.append(pages);
		}
		if (year > 0) {
			buf.append('(');
			buf.append(year);
			buf.append(')');
		}
		return buf.toString();
	}
	/*
    public IAmbitEditor editor() {
        //return new LiteratureEntryEditor(this);
    }
    */
	@Override
	public IAmbitEditor editor(boolean editable) {
		IAmbitEditor e = new AbstractAmbitEditor("Reference",this) {
			protected ambit.ui.data.AbstractPropertyTableModel createTableModel(ambit.data.AmbitObject object) {
				return new LiteratureEntryTableModel((LiteratureEntry) object);
			};
			
		};
		return e;
	}
	@Override
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		super.setEditable(editable);
		if (authors!= null) authors.setEditable(editable);
		if (journal!=null) journal.setEditable(editable);
	}
}
