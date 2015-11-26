/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.dbui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.MoleculeTools;

public class CachedRowSetTableModel extends AbstractTableModel {
	protected CachedRowSet records;
    protected int page=0;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5428865165072188779L;

	public int getColumnCount() {
        if (records == null) return 1;
        else try {
            return records.getMetaData().getColumnCount()+1;
        } catch (SQLException x) {
            return 1;
        }
	}

	public int getRowCount() {
		if (records != null)
			return records.size();
		else 
			return 0;
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			int type = records.getMetaData().getColumnType(columnIndex);
	        switch (type) {
	        case java.sql.Types.BIT: {
	        	records.updateBoolean(columnIndex, ((Boolean)aValue).booleanValue());
	        	records.updateRow();
	        }
	        default: {
	        }
	        }
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex ==0) return 1 + rowIndex + getOffset();
        else if (records == null) return "";
        else try {
            int type = records.getMetaData().getColumnType(columnIndex);
            records.first();
            records.relative(rowIndex);
            //records.absolute(rowIndex+1);

            if (records.getMetaData().getColumnName(columnIndex).equals("uncompress(structure)")) {
            	if (type == java.sql.Types.VARBINARY) {
	                InputStream s = records.getBinaryStream(columnIndex);
	                IAtomContainer m = null;
	                if ("SDF".equals(records.getString("format")))
	                	m = MoleculeTools.readMolfile(new InputStreamReader(s));
	                else
	                	m = MoleculeTools.readCMLMolecule(s);
	                s = null;
	                return m;
            	} else {

            		return MoleculeTools.readCMLMolecule(records.getString(columnIndex));
            	}
                
            } else
                switch (type) {
                case java.sql.Types.INTEGER:  return records.getInt(columnIndex);
                case java.sql.Types.BOOLEAN:  return records.getBoolean(columnIndex);
                case java.sql.Types.TINYINT:  return records.getBoolean(columnIndex);
                case java.sql.Types.BIT:  return records.getBoolean(columnIndex);
                case java.sql.Types.CHAR:  return records.getString(columnIndex);
                case java.sql.Types.TIMESTAMP: return records.getDate(columnIndex);
                case java.sql.Types.VARCHAR:  return records.getString(columnIndex);
                default: {
                	System.out.println(type);
                	return records.getString(columnIndex);
                }
                }
        } catch (CDKException x) {
            x.printStackTrace();
            return null;
        } catch (SQLException x) {
            x.printStackTrace();
            return rowIndex + " " + x.getMessage();
        } catch (Exception x) {
            x.printStackTrace();
            return null;            
        }
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    if (columnIndex == 0) return Integer.class;
        try {
            if (records.getMetaData().getColumnName(columnIndex).equals("uncompress(structure)")) {
                return IAtomContainer.class;
            } else {
                int type = records.getMetaData().getColumnType(columnIndex);
                switch (type) {
                case java.sql.Types.INTEGER:  return Integer.class;
                case java.sql.Types.BOOLEAN:  return Boolean.class;
                case java.sql.Types.TINYINT:  return Boolean.class;
                case java.sql.Types.BIT:  return Boolean.class;
                case java.sql.Types.CHAR:  return String.class;
                case java.sql.Types.TIMESTAMP:  return Date.class;
                case java.sql.Types.VARCHAR:  return String.class;
                default: return String.class;
                }
            }
        } catch (SQLException x) {
            return String.class;
        }
	}
    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) return "Page #"+(page+1);
        if (records == null) return "";
        try {
            return records.getMetaData().getColumnLabel(columnIndex);
        } catch (SQLException x) {
            return "";
        }

    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	// TODO Auto-generated method stub
    	return true;
    }
	public CachedRowSet getRecords() {
		return records;
	}
    protected int getOffset() {
        if (records == null) return 0;
        else return records.getPageSize()*page; 
    }

	public void setRecords(CachedRowSet records) {
		this.records = records;
        this.page =0;
		fireTableStructureChanged();
	}
	public boolean nextPage() throws SQLException {
		if (records == null) return false;
	    boolean ok = records.nextPage();
        if (ok) page++;
        fireTableStructureChanged();
        return ok;
    }
    public boolean previousPage() throws SQLException {
    	if (records == null) return false;
        boolean ok = records.previousPage();
        if (ok) page--; else page = 0;
        fireTableStructureChanged();
        return ok;
    }    
}



/*
http://www.jguru.com/faq/view.jsp?EID=776543
 */
