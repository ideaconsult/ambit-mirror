package ambit2.base.data;

import java.io.Serializable;

public interface ILiteratureEntry extends Serializable {
	public enum _type {Unknown,Dataset,Algorithm,Model,BibtexEntry,BibtexArticle,BibtexBook,Feature};
	   int getId();
	   String getName();
	   boolean hasID();
	   void setId(int id);
	   String getTitle();
	   String getURL();
	   _type getType();
	   void setType(_type type);

}
