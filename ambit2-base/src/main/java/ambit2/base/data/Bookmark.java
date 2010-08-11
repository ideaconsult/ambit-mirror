package ambit2.base.data;

import java.io.Serializable;

/**
 * http://www.w3.org/2003/07/Annotea/BookmarkSchema-20030707
 * @author nina
 *
 */
public class Bookmark implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7323544303939680815L;
	public enum ResourceType {Dataset,Compound,Model,Algorithm,Validation,Feature};
	
	public Bookmark() {
		this(null);
	}
	public Bookmark(String creator) {
		this.creator = creator;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	int id = -1;
	
	String creator;
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getRecalls() {
		return recalls;
	}
	public void setRecalls(String recalls) {
		this.recalls = recalls;
	}
	public String getHasTopic() {
		return hasTopic;
	}
	public void setHasTopic(String hasTopic) {
		this.hasTopic = hasTopic;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}

	String recalls;
	String hasTopic;
	String title;
	String description;
	long created; //creation date
	long date; //modification date
	@Override
	public String toString() {
		return String.format("<a href='%s' title='%s'>%s</a>",recalls,description,title);
	}
}
