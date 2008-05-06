/**
 * Created on 2005-1-25
 *
 */
package ambit2.domain;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import ambit2.data.AmbitList;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.molecule.AmbitPoint;
import ambit2.data.molecule.Compound;
import ambit2.io.IColumnTypeSelection;

/**
 * @deprecated
 * A class to store, load and export a QSAR data set   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
//TODO needs major refactoring 
public class AllData extends AmbitList<AmbitPoint> {
	
	protected int nDescriptors = 0;
	protected Descriptor d_ypredicted;
	protected Descriptor d_yobserved;
	protected Descriptor d_yresidual;
	protected Descriptor d_equation;	
	protected DescriptorsList xdescriptors=null;
	protected DescriptorsList identifiers=null;
	//data
	protected Vector data = null;
	// items to be double[3], indexed as in AmbitPoint above 
	protected Vector y = null;	
	protected Vector equation = null;
	
	protected DescriptorsList xmatch = null;
	private AmbitPoint p = null;
	
	/**
	 * 
	 */
	public AllData() {
		super();
		
	}
	public boolean isEmpty() {
		return (data == null) || (data.size() == 0) || (nDescriptors == 0);
	}
	public void clear() {
		super.clear();
		d_ypredicted = null; 
		d_yobserved = null;
		d_yresidual = null;
		d_equation = null;
		
		if (xdescriptors != null) xdescriptors.clear();
		if (identifiers != null) identifiers.clear();
		if (data != null)	data.clear();
		
		if (list != null) list.clear();
		
		if (y != null) y.clear();
//		if (ypredicted != null) ypredicted.clear();	
//		if (yobserved != null) yobserved.clear();
//		if (yresidual != null) yresidual.clear();
		
		if (equation != null) equation.clear();		
		
		nDescriptors = 0;
	}
	public double getX(int row, int col) {
		double[] val = (double[]) data.get(row);
		return val[col];
	}
	public double[] getX(int row) {
		return (double[]) data.get(row);
	}
	public String getXName(int i) {
		if (xdescriptors == null) return "";
		else return xdescriptors.getDescriptorName(i);
	}
	public void setPredictedValue(int row, double d) {
		double[] val = (double[]) y.get(row);
		val[AmbitPoint._ypIndex] = d;
	}
	public void setPredictedValue(int row, String d) {
		double[] val = (double[]) y.get(row);
		try {
		    val[AmbitPoint._ypIndex] = Double.parseDouble(d);
		} catch (NumberFormatException x) {
		    val[AmbitPoint._ypIndex] = Double.NaN;
		}
	}
	
	public void setObservedValue(int row, double d) {
		double[] val = (double[]) y.get(row);
		val[AmbitPoint._yoIndex] = d;
	}
	public void setObservedValue(int row, String d) {
		double[] val = (double[]) y.get(row);
		try {
		val[AmbitPoint._yoIndex] = Double.parseDouble(d);
		} catch (NumberFormatException x) {
		    val[AmbitPoint._ypIndex] = Double.NaN;
		}

	}
	public void setResidualValue(int row, String d) {
		double[] val = (double[]) y.get(row);
		try {
		    val[AmbitPoint._yrIndex] = Double.parseDouble(d);
		} catch (NumberFormatException x) {
		    val[AmbitPoint._ypIndex] = Double.NaN;
		}

	}			
	public void setResidualValue(int row, double d) {
		double[] val = (double[]) y.get(row);
		val[AmbitPoint._yrIndex] = d;
	}
	protected String getYString(int row, int col) {
		try {
		if (y == null) return "";
		double[] val = (double[]) y.get(row);
		return Double.toString(val[col]);
		} catch (Exception x) {return "";}
	}
	protected double getYdouble(int row, int col) {
		if (y == null) return Double.NaN;
		double[] val = (double[]) y.get(row);
		return val[col];
	}
	public double getYResidual(int row) {
		double d = getYdouble(row,AmbitPoint._yrIndex);
		if (new Double(d).isNaN()) 
			return getYdouble(row,AmbitPoint._yoIndex) -
				   getYdouble(row,AmbitPoint._ypIndex);
		return d;
	}
	public double getYObserved(int row) {
		return getYdouble(row,AmbitPoint._yoIndex);
	}
	public double getYPredicted(int row) {
		return getYdouble(row,AmbitPoint._ypIndex);
	}
	
	
	public String getYPredictedString(int row) {
		return getYString(row,AmbitPoint._ypIndex);
	}
	public String getYObservedString(int row) {
		return getYString(row,AmbitPoint._yoIndex);
	}	
	public String getYResidualString(int row) {
		return getYString(row,AmbitPoint._yrIndex);
	}
	public void setData(Descriptor d, String value) {
		int nPoints = data.size();
		switch (d.getTypeInModel().getId()) {
		case(IColumnTypeSelection._ctX): {
			int m = d.getOrderInModel();
			Double dvalue ;
			double[] val = (double[]) data.get(nPoints-1);
			try {
				val[m] =  new Double(value).doubleValue();
			} catch (Exception e) {
				//TODO default value
				val[m] = 0;
			}
			return;
		}
		case(IColumnTypeSelection._ctYpredicted): {
			int m = d.getOrderInModel();
			setPredictedValue(nPoints-1,value);
			return;
		}
		case(IColumnTypeSelection._ctYresidual): {
			int m = d.getOrderInModel();
			setResidualValue(nPoints-1,value);
			return;
		}				
		case(IColumnTypeSelection._ctYobserved): {
			int m = d.getOrderInModel();
			setObservedValue(nPoints-1,value);			
			return;
		}		
		case (IColumnTypeSelection._ctRowID): {
			int o = -1;
			try { o = Integer.parseInt(value); } catch (NumberFormatException x) { o = -1; }
			
			getItem(nPoints-1).setOrderInModel(o);
			return;
		}
		case (IColumnTypeSelection._ctCAS): {
            getItem(nPoints-1).setCAS_RN(value);			
			return;
		}
		case (IColumnTypeSelection._ctSMILES): {
            getItem(nPoints-1).setSMILES(value);			
			return;
		}
		case (IColumnTypeSelection._ctChemName): {
            getItem(nPoints-1).setName(value);			
			return;
		}
		case (IColumnTypeSelection._ctEquation): {
			equation.set(nPoints-1,value);
			return;
		}		
		default: {
		
		}
		}
		
	}
	public AmbitPoint getItem(int i) {
		if (p==null) p = new AmbitPoint();
		//p.setCompound(getItem(i));
		p.setXvalues((double[])data.get(i));
		p.setYvalues((double[])y.get(i));		
		
		p.setDescriptors(xdescriptors);		
		if (data != null) { 
			p.setXvalues((double[] )data.get(i));
			return p;
		} 
		return p;
	}
	/* (non-Javadoc)
	 * @see ambit2.data.AmbitList#addItem(ambit2.data.AmbitObject)
	 */
	public int addItem(AmbitPoint entry) {
		try {
			if (entry instanceof AmbitPoint) {
				AmbitPoint point = (AmbitPoint) entry;
				//check if uninitialized
				if (data == null) {
					initialize(point.getDescriptors());
				}				
				
				int r = addRow(point,
						point.getDescriptors(),
						point.getXvalues());
				
				setPredictedValue(r-1,point.getYPredictedDouble()); 
				setObservedValue(r-1,point.getYObservedDouble());
				//setResidualValue(r-1,point.getYResidualDouble());
			} else if (entry instanceof Compound) {
				addRow((Compound) entry);
			}
			return size()-1;
		} catch (Exception x) {
			x.printStackTrace();
			return -1;
		}
	}
	/*
	*	add an empty row
	*/
	public int addRow() {
		return addRow(null);	
	}
	
	public int addRow(Compound c) {
		try {
			double[] val = null;
			if (xdescriptors != null) { 
				val = new double[xdescriptors.size()];
				for (int i = 0; i < val.length; i++) val[i] = 0;
			}	
			data.add(val);
			if (c == null) c = new Compound();
			if (equation != null) equation.add("");		
			if (y != null) {
				double[] v = new double[AmbitPoint._yrIndex+1];
				for (int i = 0; i < v.length; i++) v[i] = Double.NaN;
				y.add(v);
			}
			//addItem(c);


		} catch (Exception x) {
			x.printStackTrace();
		}
		//System.out.println(nPoints);
		return size();
	}
	/* (non-Javadoc)
	 * @see ambit2.io.IReadData#addRow(ambit2.data.descriptors.DescriptorsList, java.util.List)
	 */
	public int addRow(DescriptorsList dlist, List values) {
		return addRow(null,dlist,values);
	}
	public int addRow(Compound c, DescriptorsList dlist,double[] xvalues) {
		int r = addRow(c);
		Descriptor d;
		if (dlist != null) {
			int n = dlist.size();
			if (n > xvalues.length) n = xvalues.length;
			for (int i = 0; i < dlist.size(); i++) {
				d = dlist.getDescriptor(i);
				setData(d,Double.toString(xvalues[i]));
			}
		}
		return r;
	}	
	public int addRow(Compound c, DescriptorsList dlist,List values) {
		int r = addRow(c);
		Descriptor d;
		int n = dlist.size();
		if (n > values.size()) n = values.size();
		for (int i = 0; i < dlist.size(); i++) {
			d = dlist.getDescriptor(i);
			setData(d,values.get(i).toString());
		}
		return r;
	}
	public int addRow(Compound c, DescriptorsList dlist,Map values, boolean removeMatched) {
		int r = addRow(c);
		Descriptor d;
		int n = dlist.size();
		if (n > values.size()) n = values.size();
		for (int i = 0; i < dlist.size(); i++) {
			d = dlist.getDescriptor(i);
			Object value = values.get(d.getName());
			if (value != null) {
			    setData(d,value.toString());
			    if (removeMatched)
			        values.remove(d.getName());
			}
		}
		return r;
	}	
	public void setMatch(DescriptorsList dlist) {
		xmatch = dlist;
	}
	public void initialize(DescriptorsList dlist) {
		//System.out.println("All data initialize "+ toString());
		if (dlist != null) System.out.println(dlist.toString());
		if (xdescriptors != null) xdescriptors.clear(); 
		else xdescriptors = new DescriptorsList();
		
		if (identifiers != null) identifiers.clear(); 
		else identifiers = new DescriptorsList();

		if (y != null) y.clear();
		else y = new Vector();
		/**
		if (ypredicted != null) ypredicted.clear();
		else ypredicted = new Vector();	
		if (yobserved != null) yobserved.clear();
		else yobserved = new Vector();
		if (yresidual != null) yresidual.clear();
		else yresidual = new Vector();
		*/
		if (equation != null) equation.clear();
		else equation = new Vector();
		
		Descriptor d;
		nDescriptors = 0;
		if (data == null) data = new Vector(); else data.clear();
		//studyList is always created in the constructor 
		list.clear();
		if (dlist != null)
		for (int i = 0; i < dlist.size(); i++) {
			d = dlist.getDescriptor(i);
			//try {
				//d = (Descriptor)(dlist.getDescriptor(i)).clone();
			//} catch (Exception e) {
//				d = null;
				//e.printStackTrace();
			//}
			switch (d.getTypeInModel().getId()) {
			case(IColumnTypeSelection._ctCAS): {
				d.setOrderInModel(1);
				identifiers.addItem(d);
				break;
			}
			case(IColumnTypeSelection._ctSMILES): {
				d.setOrderInModel(2);
				identifiers.addItem(d);
				break;
			}
			case(IColumnTypeSelection._ctChemName): {
				d.setOrderInModel(3);
				identifiers.addItem(d);
				break;
			}			
			case(IColumnTypeSelection._ctX): {
				d.setOrderInModel(nDescriptors);
				xdescriptors.addItem(d);
				nDescriptors++;
				break;
			}
			case(IColumnTypeSelection._ctYobserved): {
				d_yobserved = d;
//				if (yobserved == null) yobserved = new Vector();
				break;
			}
			case(IColumnTypeSelection._ctYpredicted): {
				d_ypredicted = d;
//				if (ypredicted == null) ypredicted = new Vector();
				break;
			}
			case(IColumnTypeSelection._ctYresidual): {
				d_yresidual = d;
		//		if (yresidual == null) yresidual = new Vector();
				break;
			}			
			case(IColumnTypeSelection._ctRowID): {
				d.setOrderInModel(0);
				identifiers.addItem(d);
				break;
			}
			case(IColumnTypeSelection._ctEquation): {
				d_equation = d;
//				if (equation == null) equation = new Vector();
			}				
			}
		}
		if (xmatch != null)
			xdescriptors.matchTo(xmatch);
		xmatch = null;
	}
	public int getNPoints() {
		return size();
	}
	public int getNDescriptors() {
		return nDescriptors;
	}
	public DescriptorsList getXDescriptors() {
		return xdescriptors;
	}

	public String getID(int point) {
		if ((point < 0) || (point >= size())) 
				return Integer.toString(point+1);
		else return Integer.toString(getItem(point).getId());
		
	}

	public String getHeader(char d) {
		StringBuffer buf = new StringBuffer();
		buf.append("#");buf.append(d);
		
		buf.append("ID");buf.append(d);
		buf.append("CAS");buf.append(d);		
		buf.append("NAME");buf.append(d);

		buf.append("SMILES");buf.append(d);
		for (int j = 0; j < xdescriptors.size(); j++) {
			buf.append(xdescriptors.getItem(j)); buf.append(d);
		}
		
		buf.append("Predicted"); buf.append(d);
		buf.append("Observed"); 
		buf.append(d);
		buf.append("Error"); buf.append(d);		
		
		buf.append("Equation"); buf.append(d);
				
		return buf.toString();
	}	

	public String rowToString(int row, char d) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(row+1);buf.append(d);
		buf.append("\""); 
		buf.append((row+1));
		buf.append("\"");	buf.append(d);
	
		buf.append(getCompoundCAS(row));	buf.append(d);
		
		buf.append("\"");
		buf.append(super.getItem(row).getName());
		buf.append("\"");	
		buf.append(d);
		
		buf.append(getSMILES(row));	
		buf.append(d);
		
		double[] val = (double[]) data.get(row);		
		for (int j = 0; j < xdescriptors.size(); j++) {
			buf.append(val[j]); buf.append(d);
		}
		if (y != null) {
			double[] yvalues = (double[]) y.get(row);
			buf.append(yvalues[AmbitPoint._ypIndex]); buf.append(d);
			buf.append(yvalues[AmbitPoint._yoIndex]); buf.append(d);
			buf.append(yvalues[AmbitPoint._yrIndex]); buf.append(d);			
		}
		if (equation != null) {
			buf.append(equation.get(row)); buf.append(d);}				
		return buf.toString();
	}	
	
	public Compound readCompound(int row) {
		return (Compound) super.getItem(row);
	}
	
	public String getSMILES(int row) {
		return ((Compound) super.getItem(row)).getSMILES();
	}
	
	public double[] getDataRow(int row) {
		return (double[]) data.get(row);
			
	}
	public double getDataItem(int row,int col) throws Exception {
		return ((double[]) data.get(row))[col];
	}
	public String getDataItemToString(int row,int col) throws Exception {
		return Double.toString(((double[]) data.get(row))[col]);
	}	
	public Vector getData() {
		return data;
	}
	public String getYPredictedName() {
		if (d_ypredicted != null) return d_ypredicted.getName(); else return "NA";
	}
	public String getYObservedName() {
		if (d_ypredicted != null) return d_yobserved.getName(); else return "NA";
	}
	public String getCompoundName(int row) {
		try {
		return ((Compound) super.getItem(row)).getName();
		} catch (Exception x) {return "";}
	}
	public String getCompoundCAS(int row) {
		try {
		return  ((Compound) super.getItem(row)).getCAS_RN();
		} catch (Exception x) {return "";}
	}	
	public Object clone() throws CloneNotSupportedException {
		//TODO clone
		throw new CloneNotSupportedException();
	}
	public Compound getCompound(int row) {
		try {
		return (Compound) super.getItem(row);
		} catch (Exception x) {return null;}
	}
	public DescriptorsList getIdentifiers() {
		return identifiers;
	}
	public String toString() {
		return "Data: " + getNPoints() + " points " + nDescriptors + " descriptor(s)" ;
	}
	/* (non-Javadoc)
	 * @see ambit2.data.molecule.CompoundsList#createNewItem()
	 */
	public AmbitPoint createNewItem() {
		return new AmbitPoint();
	}
	
	protected AmbitPoint remove(int i, boolean notify) {
		if ((i >= 0) && (i < size())) {
			p = (AmbitPoint) getItem(i);
			list.remove(i);
			if (data != null) data.remove(i);
			if (y != null) y.remove(i);
			if (equation != null) equation.remove(i);			

			setSelectedIndex(size()-1,false);
			if (notify)	setModified(true);
			return p;
		} else return null;	
	}
	
	public AmbitPoint remove(int i) {
		return remove(i,true);
	}
	public boolean remove(Object o) {
		return false;
	}
	public int getRowID(int row) {
		if (identifiers == null) return row;
		else return	((Compound) super.getItem(row)).getOrderInModel();
	}
	
	public AmbitPoint findData2D(int index1, int index2, 
			double x, double y, double distance) {
		if (data == null) return null;
		int found = -1;
		double [] val = null;
		for (int i =0; i < size(); i++) {
			val = (double[]) data.get(i);
			if ((Math.abs(val[index1]-x) <= distance) &&
				(Math.abs(val[index2]-y) <= distance)) {
					found = i;
					break;
				}
		}
		if (found > -1) return (AmbitPoint) getItem(found);
		else return null;
	}
	public double[][] getDataRange() {
		if (data == null) return null;
		int nd = getNDescriptors();
		double [][] range = new double[2][nd];
		double [] val = null;
		for (int i =0; i < size(); i++) {
			val = (double[]) data.get(i);
			for (int j=0; j < nd; j++) 
				if (i==0) {
					range[0][j] = val[j];range[1][j] = val[j];
				} else {
					if (range[0][j] > val[j]) range[0][j] = val[j];
					if (range[1][j] < val[j]) range[1][j] = val[j];
				}
		}
		return range;
	}
}
