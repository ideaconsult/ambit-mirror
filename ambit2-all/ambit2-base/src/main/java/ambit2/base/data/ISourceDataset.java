package ambit2.base.data;

import java.io.Serializable;

public interface ISourceDataset extends Serializable {
	public enum fields {title,source,url,seeAlso,license, rightsHolder,maintainer,stars };
	enum license {
		Unknown {
			@Override
			public String getURI() {
				return "Unknown";
			}
			@Override
			public String getTitle() {
				return "License not defined";
			}
		},
		PDDL {
			@Override
			public String getURI() {
				return "http://www.opendatacommons.org/licenses/pddl/";
			}
			@Override
			public String getTitle() {
				return "Public Domain Dedication and License (PDDL) - 'Public Domain for data/databases'";
			}
		},
		ODC_By {
			@Override
			public String getURI() {
				return "http://www.opendatacommons.org/licenses/by/";
			}
			@Override
			public String getTitle() {
				return "Open Data Commons Attribution (ODC-By) - 'Attribution for data/databases'";
			}
		},
		ODC_ODbl {
			@Override
			public String getURI() {
				return "http://www.opendatacommons.org/licenses/odbl/";
			}
			@Override
			public String getTitle() {
				return "Open Database License (ODC-ODbL) - 'Attribution Share-Alike for data/databases'";
			}
		},
		CC0_1_0 {
			@Override
			public String getURI() {
				return "http://creativecommons.org/publicdomain/zero/1.0/";
			}
			@Override
			public String getTitle() {
				return "CC0 1.0 Universal - 'Creative Commons public domain waiver'";
			}
		},
		CC_BY_SA {
			@Override
			public String getURI() {
				return "http://creativecommons.org/licenses/by-sa/3.0/";
			}
			@Override
			public String getTitle() {
				return "Creative Commons Attribution-ShareAlike (CC-BY-SA)";
			}
		},
		GFDL {
			@Override
			public String getURI() {
				return "http://www.gnu.org/copyleft/fdl.html";
			}
			@Override
			public String getTitle() {
				return "GNU Free Documentation License (GFDL)";
			}
		},
		ODL10 {
			@Override
			public String getURI() {
				return "http://opendatacommons.org/licenses/odbl/1.0/";
			}
			@Override
			public String getTitle() {
				return "Open Database License v1.0";
			}
		},
		ODCBy10 {
			@Override
			public String getURI() {
				return "http://opendatacommons.org/licenses/by/1.0/";
			}
			@Override
			public String getTitle() {
				return "http://opendatacommons.org/licenses/by/1.0/";
			}
		}		
		;
		public String toString() {
			return name().replace("_", "-");
		};
		public abstract String getURI();
		public String getTitle() {
			return toString();
		}
		
		
		
	}
	public void setName(String name);
	public String getName();
    public int getID();
    public void setID(int id);
    public String getSource();
    public void setSource(String name);
    public String getLicenseURI();
    public void setLicenseURI(String uri);
    public String getrightsHolder();
    public void setrightsHolder(String uri);
    public String getMaintainer();
    public void setMaintainer(String uri);    
    public int getStars();
    public void setStars(int rating);
}
