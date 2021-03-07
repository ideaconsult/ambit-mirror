package ambit2.db.update.bundle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ambit2.base.data.I5Utils;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.UpdateBundle._published_status;
import ambit2.db.update.dataset.AbstractReadDataset;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

/**
 * Retrieve {@link SourceDataset} by id
 * 
 * @author nina
 * 
 */
public class ReadBundle extends AbstractReadDataset<String, SubstanceEndpointsBundle> {

	public static final String select_datasets = "SELECT idbundle as id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer,created,description,hex(bundle_number) bn,version,user_name,published_status,updated FROM bundle join catalog_references using(idreference) %s %s\n";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560670663328542819L;
	protected Set<_published_status> status = null;

	private enum _IDMODE {
		all {
			@Override
			public String getSQL() {
				return "";
			}
			
		},
		idbundle {
			@Override
			public String getSQL() {
				return " idbundle=?";
			}
			
			
		}, 
		name {
			@Override
			public String getSQL() {
				return " name like ?";
			}
			
		}, 
		bundle_number  {
			@Override
			public String getSQL() {
				return " bundle_number=unhex(?)";
			}
						
		}
		;
		public abstract String getSQL();
		
	};

	protected _IDMODE _idmode = _IDMODE.all;

	public Set<_published_status> getStatus() {
		return status;
	}

	public void setStatus(Set<_published_status> status) {
		this.status = status;
	}

	public ReadBundle(Set<_published_status> status) {
		super();
		setStatus(status);
	}

	public ReadBundle(String username, Set<_published_status> status) {
		super();
		setFieldname(username);
		setStatus(status);
	}

	@Override
	protected SubstanceEndpointsBundle createObject(String title, LiteratureEntry le, String username) {
		SubstanceEndpointsBundle d = new SubstanceEndpointsBundle(title);
		d.setTitle(le.getTitle());
		d.setURL(le.getURL());
		d.setUserName(username);
		return d;
	}

	/**
	 * User name
	 */
	@Override
	public String getFieldname() {
		return super.getFieldname();
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		_idmode = _IDMODE.all;
		if (getValue() != null) {
			if (getValue().getID() > 0) {
				_idmode = _IDMODE.idbundle;
				params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
			} else if (getValue().getName() != null) {
				_idmode = _IDMODE.name;
				params.add(new QueryParam<String>(String.class, getValue().getName()));
			} else if (getValue().getBundle_number() != null) {
				_idmode = _IDMODE.bundle_number;
				String bn = getValue().getBundle_number().toString().replaceAll("-", "");
				params.add(new QueryParam<String>(String.class, bn));
				// where bundle_number in (select bundle_number from bundle
				// where idbundle=?)
			} else
				throw new AmbitException("Undefined bundle");
		}
		// user name
		if (getFieldname() != null)
			params.add(new QueryParam<String>(String.class, getFieldname()));

		return params;
	}

	public String getSQL() throws AmbitException {
	    String delimiter = " where ";
	    StringBuilder p = new StringBuilder();
	    switch (_idmode) {
	    case all: {
	        p.append(delimiter);
	        String d = " published_status in (";
	        for (_published_status s : getStatus()) {
	            p.append(d);
	            p.append("'");
	            p.append(s.name());
	            p.append("'");
	            d = ",";
	        }
	        p.append(") ");
	        delimiter = " and ";
	        break;
	    }
	    default: {
	      p.append(delimiter);
	      p.append(_idmode.getSQL());
	    }
	    }
	    if (getFieldname()!=null) {
	      p.append(delimiter);  
	      p.append("user_name =?");
	    }
	    
		String sql = String.format(select_datasets,p.toString(),"");
		return sql;

	}

	@Override
	public String toString() {
		return (getValue() != null ? getValue().getID() > 0 ? String.format("/bundle/%d", getValue().getID())
				: String.format("/bundle/%s", getValue().getName()) : "Bundle");
	}

	@Override
	public SubstanceEndpointsBundle getObject(ResultSet rs) throws AmbitException {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
		try {

			bundle.setTitle(rs.getString(_fields.title.getIndex()));
			bundle.setURL(rs.getString(_fields.url.getIndex()));
			bundle.setReferenceID(rs.getInt(_fields.idreference.name()));
			bundle.setName(rs.getString(_fields.name.name()));
			bundle.setUserName(rs.getString(_fields.user_name.name()));
			bundle.setID(rs.getInt(_fields.id_srcdataset.name()));
			bundle.setLicenseURI(rs.getString(_fields.licenseURI.name()));
			bundle.setrightsHolder(rs.getString(_fields.rightsHolder.name()));
			bundle.setMaintainer(rs.getString(_fields.maintainer.name()));
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		try {
			bundle.setStars(rs.getInt(_fields.stars.name()));
		} catch (SQLException x) {
			bundle.setStars(-1);
		}

		try {
			bundle.setCreated(rs.getTimestamp("created").getTime());
		} catch (Exception x) {
		}
		try {
			bundle.setUpdated(rs.getTimestamp("updated").getTime());
		} catch (Exception x) {
		}
		try {
			bundle.setDescription(rs.getString("description"));
		} catch (Exception x) {
		}
		try {
			bundle.setUserName(rs.getString("username"));
		} catch (Exception x) {
		}
		try {
			bundle.setVersion(rs.getInt("version"));
		} catch (Exception x) {
		}
		try {
			bundle.setStatus(rs.getString("published_status"));
		} catch (Exception x) {
		}
		try {
			String bn = rs.getString("bn");
			if (bn.length() < 32)
				bn = String.format("%32s", bn).replace(" ", "0");
			bundle.setBundle_number(UUID.fromString(I5Utils.addDashes(bn)));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return bundle;
	}

}
