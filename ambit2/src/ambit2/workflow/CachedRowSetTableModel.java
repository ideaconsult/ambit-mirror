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

package ambit2.workflow;

import java.awt.Dimension;
import java.awt.Image;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.data.molecule.MoleculeTools;
import ambit2.ui.data.CompoundImageTools;

public class CachedRowSetTableModel extends AbstractTableModel {
    protected CompoundImageTools imageTools;
	protected CachedRowSet records;
    protected int page=0;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5428865165072188779L;

    public CachedRowSetTableModel() {
        imageTools = new CompoundImageTools(new Dimension(100,100));
    }
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

	public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex ==0) return 1 + rowIndex + getOffset();
        else if (records == null) return "";
        else try {
            int type = records.getMetaData().getColumnType(columnIndex);
            records.first();
            records.relative(rowIndex);
            //records.absolute(rowIndex+1);

            if (records.getMetaData().getColumnName(columnIndex).equals("uncompress(structure)")) {
                String s = records.getString(columnIndex);
                IMolecule m = MoleculeTools.readCMLMolecule(s);
                return imageTools.getImage(m);
            } else
                switch (type) {
                case java.sql.Types.INTEGER:  return records.getInt(columnIndex);
                case java.sql.Types.BOOLEAN:  return records.getBoolean(columnIndex);
                case java.sql.Types.CHAR:  return records.getString(columnIndex);
                default: return records.getString(columnIndex);
                }
        } catch (CDKException x) {
            x.printStackTrace();
            return null;
        } catch (SQLException x) {
            x.printStackTrace();
            return rowIndex + " " + x.getMessage();
        }
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    if (columnIndex == 0) return Integer.class;
        try {
            if (records.getMetaData().getColumnName(columnIndex).equals("uncompress(structure)")) {
                return Image.class;
            } else {
                int type = records.getMetaData().getColumnType(columnIndex);
                switch (type) {
                case java.sql.Types.INTEGER:  return Integer.class;
                case java.sql.Types.BOOLEAN:  return Boolean.class;
                case java.sql.Types.CHAR:  return String.class;
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
	    boolean ok = records.nextPage();
        if (ok) page++;
        fireTableStructureChanged();
        return ok;
    }
    public boolean previousPage() throws SQLException {
        boolean ok = records.previousPage();
        if (ok) page--; else page = 0;
        fireTableStructureChanged();
        return ok;
    }    
}


