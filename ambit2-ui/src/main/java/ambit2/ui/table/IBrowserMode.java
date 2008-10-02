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

package ambit2.ui.table;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;

import ambit2.ui.Utils;

public interface IBrowserMode {
	public static String PROPERTY_MODE="browserMode";
	public static String PROPERTY_ZOOM="zoom";
	
	public enum BrowserMode {
	    Spreadsheet {
	    	protected int idcolumn = 1;
	    	protected int contentcolumn = 1;
	    	
	    	@Override
	    	public String getTitle() {
	    		return "T";
	    	}
	    	public ImageIcon getIcon(boolean selected) {
	    		try {
	    		return Utils.createImageIcon("images/application_view_table.png");
	    		} catch (Exception x) {
	    			x.printStackTrace();
	    			return null;
	    		}
	    	};
	    	@Override
	    	public String getTooltip() {
	    		return "Table";
	    	}
	    	@Override
	    	public Dimension getCellSize(int row,int col) {
	    		/*
	    		if (row==0)
	    			return new Dimension(100,100);
	    		else	
	    		*/
	    			return new Dimension(100,18);
	    	}
	    	@Override
	    	public int getColumns() {
	    		return 0;
	    	}
	    	@Override
	    	public int getIDColumn() {
	    		return idcolumn;
	    	}
	    	@Override
	    	public void setIDColumn(int arg0) {
	    		idcolumn = arg0;
	    		
	    	}
	    	@Override
	    	public int getContentColumn() {
	    		return contentcolumn;
	    	}
	    	@Override
	    	public void setContentColumn(int arg0) {
	    		contentcolumn = arg0;
	    		
	    	}
	    	@Override
	    	public boolean isColumnSelectionAllowed() {
	    		return false;
	    	}
	    	@Override
	    	public boolean isRowSelectionAllowed() {
	    		return true;
	    	}
	    	@Override
	    	public void setCellSize(Dimension arg0,int row,int col) {
	    		// TODO Auto-generated method stub
	    		
	    	}
	    	@Override
	    	public int cellToRecord(int row,int col) {
	    		return row;
	    	}
	    	@Override
	    	public int[] recordToCell(int record) {
	    		return new int[] {record,-1};
	    		
	    	}
	    },		
	    Matrix {
	    	protected int columns =3;
	    	protected int idcolumn = 1;
	    	protected int contentcolumn = 1;	    	
	    	protected Dimension cellSize = new Dimension(100,100);
	    	
	    	@Override
	    	public String getTitle() {
	    		return "M";
	    	} 
	    	public ImageIcon getIcon(boolean selected) {
	    		try {
	    		return Utils.createImageIcon("images/application_view_matrix.png");
	    		} catch (Exception x) {
	    			return null;
	    		}
	    	};

	    	@Override
	    	public String getTooltip() {
	    		return  "Matrix";
	    	}
	    	@Override
	    	public Dimension getCellSize(int row,int col) {
	    		return cellSize;
	    	}
	    	@Override
	    	public void setCellSize(Dimension arg0,int row,int col) {
	    		cellSize = arg0;
	    		
	    	}
	    	@Override
	    	public int getColumns() {
	    		return columns;
	    	}
	    	@Override
	    	public int getIDColumn() {
	    		return idcolumn;
	    	}
	    	@Override
	    	public void setIDColumn(int arg0) {
	    		idcolumn = arg0;
	    		
	    	}	
	    	@Override
	    	public int getContentColumn() {
	    		return contentcolumn;
	    	}
	    	@Override
	    	public void setContentColumn(int arg0) {
	    		contentcolumn = arg0;
	    		
	    	}	
	    	@Override
	    	public boolean isColumnSelectionAllowed() {
	    		return true;
	    	}
	    	@Override
	    	public boolean isRowSelectionAllowed() {
	    		return true;
	    	}
	    	@Override
	    	public int cellToRecord(int row, int col) {
	    		if (col<0) return -1;
	    		return  row*getColumns()+col;
	    	}
	    	@Override
	    	public int[] recordToCell(int record) {
	    		return new int[] {record / getColumns(),record % getColumns()};
	    		
	    	}	    	
	    } ,

	    Columns {

			@Override
			public int cellToRecord(int row, int col) {
				return col;
			}

			@Override
			public Dimension getCellSize(int row, int col) {
				return new Dimension(100,18);
			}

			@Override
			public int getColumns() {
				return 1;
			}

			@Override
			public int getContentColumn() {
				return 1;
			}

			@Override
			public int getIDColumn() {
				return 1;
			}

			@Override
			public ImageIcon getIcon(boolean selected) {
				try {
				return Utils.createImageIcon("images/application_view_columns.png");
				} catch (Exception x) {
					return null;
				}
			}

			@Override
			public String getTitle() {
				return "Columns";
			}

			@Override
			public String getTooltip() {
				return "Columns";
			}

			@Override
			public boolean isColumnSelectionAllowed() {
				return true;
			}

			@Override
			public boolean isRowSelectionAllowed() {
				return false;
			}

			@Override
			public int[] recordToCell(int record) {
				return new int[] {-1,record};
			}

			@Override
			public void setCellSize(Dimension dim, int row, int col) {
				
				
			}

			@Override
			public void setContentColumn(int id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setIDColumn(int id) {
				// TODO Auto-generated method stub
				
			}
	    	
	    };
	    public abstract String getTitle();
	    public abstract ImageIcon getIcon(boolean selected);
	    public abstract String getTooltip();
	    public abstract Dimension getCellSize(int row,int col);
	    public abstract void setCellSize(Dimension dim,int row,int col);
	    public abstract int getColumns();
	    public abstract int cellToRecord(int row,int col);
	    public abstract int[] recordToCell(int record);
		/**
		 * 
		 * @return # of the column to be used as ID from a TableModel
		 */	    
	    public abstract int getIDColumn();
		/**
		 * 
		 * @param id # of the column to be used as ID from a TableModel
		 */	    
	    public abstract void setIDColumn(int id);
	    
	    public abstract int getContentColumn();
	    public abstract void setContentColumn(int id);
	    public abstract boolean isRowSelectionAllowed();
	    public abstract boolean isColumnSelectionAllowed();
	}
	void setBrowserMode(BrowserMode mode);
	BrowserMode getBrowserMode();

	
	public void zoom(double arg0);
	void addPropertyChangeListener(PropertyChangeListener x);
	void removePropertyChangeListener(PropertyChangeListener x);
}

