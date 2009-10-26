package ambit2.base.data;

import java.io.Serializable;

public interface ILiteratureEntry extends Serializable {
	   int getId();
	   String getName();
	   boolean hasID();
	   void setId(int id);
	   String getTitle();
	   String getURL();
}
