package ambit2.base.data;

import java.io.Serializable;

public interface ILiteratureEntry extends Serializable {
	public enum _type {
		Unknown,
		Dataset {
			@Override
			public String toString() {
				return "Dataset(s)";
			}
		},
		Algorithm {
			@Override
			public String toString() {
				return "Calculated";
			}
		},
		Model {
			@Override
			public String toString() {
				return "Predictions";
			}
		},
		BibtexEntry {
			@Override
			public String toString() {
				return "Identifier(s)";
			}
		},
		BibtexArticle,
		BibtexBook,
		Feature {
			@Override
			public String toString() {
				return "Property";
			}			
		},
		Substance 
		;
		public String toString() {
			return name();
		};
	};
	   int getId();
	   String getName();
	   boolean hasID();
	   void setId(int id);
	   String getTitle();
	   String getURL();
	   void setURL(String uRL);
	   _type getType();
	   void setType(_type type);

}
