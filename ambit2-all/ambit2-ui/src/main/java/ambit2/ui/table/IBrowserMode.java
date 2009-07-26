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

import java.awt.Color;
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
	    	protected int contentcolumn = 3;
	    	protected Dimension[] cellSize = new Dimension[] {
	    			new Dimension(32,18),new Dimension(32,18),new Dimension(64,18),new Dimension(100,18),new Dimension(100,100)
	    	};
	    	
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
	    		if (col >= cellSize.length)
	    			return cellSize[cellSize.length-1];
	    		else
	    			return cellSize[col];
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
	    	public int cellToRecord(int row,int col) {
	    		return row;
	    	}
	    	@Override
	    	public int[] recordToCell(int record) {
	    		return new int[] {record,-1};
	    		
	    	}

			@Override
			public void setCellSize(Dimension dim, int row, int col) {
				int c = col;
				if (col >= cellSize.length)
					c = cellSize.length-1;
				cellSize[c].setSize(dim.width,dim.height);
			}
	    	
			public Dimension zoom(double x, double y) {
				Dimension column1 = null;
				for (int i=0; i < cellSize.length;i++) {
					Dimension d = getCellSize(0,i);
					Dimension newd;
					if (i == getContentColumn()){
						newd =  new Dimension(
								(int) Math.round(x*d.getWidth()),
								(int) Math.round(x*d.getWidth())
								);
						column1 = newd;
					} else 
						newd =  new Dimension(
								d.width,
								(int) Math.round(x*d.getHeight())
								);
					setCellSize(newd, 0,i);
				}
				return column1;
				
			}				    
		    public boolean showGridHorizontal() {
		    	return true;
		    }
		    public boolean showGridVertical() {
		    	return false;
		    }	 		
	    
	    },		
	    Matrix {
	    	protected int columns = 4;
	    	protected int idcolumn = 3;
	    	protected int contentcolumn = 1;
	    	protected int cpr = 2;
	    	protected Dimension[][] cellSize = {
	    			{new Dimension(32,100),new Dimension(100,100)},
	    			{new Dimension(32,18),new Dimension(100,18)},
	    	};
	    	
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
	    		return cellSize[row % 2][col % 2];
	    	}
	    	@Override
	    	public void setCellSize(Dimension d,int row,int col) {
	    		setCellSize(d.width,d.height);
	    		//cellSize[row % 2][col % 2].setSize(d.width,d.height);
	    		
	    	}
	    	protected void setCellSize(int w, int h) {
	    		cellSize[0][0].setSize(cellSize[0][0].width,h);
	    		cellSize[0][1].setSize(w,h);
	    		cellSize[1][1].setSize(w,cellSize[1][1].height);
	    	}	    	
	    	@Override
	    	public int getColumns() {
	    		return columns * cpr;
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
	    		return  (row/cpr)*columns+col/cpr;
	    	}
	    	@Override
	    	public int[] recordToCell(int record) {
	    		int[] c = new int[] {cpr * (record / columns),cpr* (record % columns)+1};
	    		return c;
	    		
	    	}	    
			public Dimension zoom(double x, double y) {
				Dimension d = getCellSize(0,1);
				Dimension newd =  new Dimension(
						(int) Math.round(x*d.width),
						(int) Math.round(x*d.width)
						);
				setCellSize(newd, 0,1);
			
				return newd;
				
			}
		    public Color getCellColor(int row, int col) {
		    	return Color.white;
		    }			
	    } ,

	    Columns {
	    	protected Dimension cellSize = new Dimension(100,18);
	    	protected Dimension cellSize1 = new Dimension(100,100);
			@Override
			public int cellToRecord(int row, int col) {
				return col-1;
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
				return new int[] {-1,record+1};
			}

			@Override
			public void setCellSize(Dimension dim, int row, int col) {
				if (row == 1)
					cellSize1.setSize(dim.width,dim.height);
				else
					cellSize.setSize(dim.width,dim.height);
			}
			@Override
			public Dimension getCellSize(int row, int col) {
				if (row==1)
					return cellSize1;
				else
					return cellSize;
			}

			@Override
			public void setContentColumn(int id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setIDColumn(int id) {
				// TODO Auto-generated method stub
				
			}
			public Dimension zoom(double x, double y) {
				Dimension d = getCellSize(0,0);
				Dimension newd =  new Dimension(
						(int) Math.round(x*d.width),
						(int) Math.round(d.height)
						);
				setCellSize(newd, 0,0);
				
				d = getCellSize(1,0);
				newd =  new Dimension(
						(int) Math.round(x*d.width),
						(int) Math.round(x*d.width)
						);
				setCellSize(newd, 1,0);			
				return newd;
				
			}		
		    public boolean showGridHorizontal() {
		    	return false;
		    }
		    public boolean showGridVertical() {
		    	return true;
		    }	 			

	    	
	    };
	    public abstract String getTitle();
	    public abstract ImageIcon getIcon(boolean selected);
	    public abstract String getTooltip();
	    public abstract Dimension getCellSize(int row,int col);
	    public abstract void setCellSize(Dimension dim,int row,int col);
	    public abstract Dimension zoom(double x, double y) ;

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
	    public boolean showGridHorizontal() {
	    	return false;
	    }
	    public boolean showGridVertical() {
	    	return false;
	    }
	    public Color getCellColor(int row, int col) {
	    	return ((row % 2)==0) ? Color.white : Color.lightGray;
	    }		    

	}
	void setBrowserMode(BrowserMode mode);
	BrowserMode getBrowserMode();

	
	public void zoom(double x,double y);
	void addPropertyChangeListener(PropertyChangeListener x);
	void removePropertyChangeListener(PropertyChangeListener x);
}

