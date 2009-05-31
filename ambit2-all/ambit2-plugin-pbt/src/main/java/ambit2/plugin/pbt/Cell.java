package ambit2.plugin.pbt;

/**
 * Extended cell content for PBTTableModel
 * @author nina
 *
 * @param <T>
 */
public class Cell<T> implements Comparable<Cell> {

	protected PBTTableModel.NODES mode;
	int row;
	int column;
	int colspan = 0;
	int rowspan=1;
	protected T object = null;
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public PBTTableModel.NODES getMode() {
		return mode;
	}
	public void setMode(PBTTableModel.NODES mode) {
		this.mode = mode;
	}
	public Cell(int row,int col) {
		this(row,col,1);
	}
	public Cell(int row,int col, int colspan) {
		setRow(row);
		setColumn(col);
		setColspan(colspan);
		setMode(PBTTableModel.NODES.NODE_TITLE);
	}	
	public Cell(int row,int col, int colspan,T object) {
		this(row,col,colspan);
		setObject(object);
	}		
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	@Override
	public String toString() {
		return Integer.toString(row) + "," + Integer.toString(column);
	}
	@Override
	public int hashCode() {
	    	int hash = 7;
	    	int var_code = row;
	    	hash = 31 * hash + var_code; 
	    	var_code = column;
	    	hash = 31 * hash + var_code; 
	
	    	return hash;
	}
	public int compareTo(Cell o) {
		int r = row -o.row;
		if (r == 0)
			return column - o.column;
		else return r;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			return (row ==((Cell)obj).row) && 
			(column == ((Cell)obj).column);
		} else return false;
	}

}