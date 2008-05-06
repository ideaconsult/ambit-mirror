/**
 * Created on 2005-1-18
 *
 */
package ambit2.domain;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.exceptions.AmbitIOException;
import ambit2.io.AmbitSettingsListener;
import ambit2.io.FileInputState;
import ambit2.io.IReadWriteStream;
import ambit2.io.MolPropertiesIOSetting;
import ambit2.data.AmbitObject;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.model.Model;
import ambit2.data.model.ModelType;
import ambit2.data.molecule.Compound;



/**
 * Stores data of a QSAR model, dataset and applicability domain statistics  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QSARDataset extends AmbitObject implements IReadWriteStream {
	//protected DataCoverageStats coverage;
	protected BayesianCoverageStats coverage;
	protected AllData data = null;
	protected Model model = null; 
	
	protected boolean readonly;
	protected boolean visible = true;
	protected boolean adEstimated = false;
	
	protected String originType = "";

	/**
	 * 
	 * @param model
	 * @param data
	 */
	public QSARDataset(Model model,AllData data) {
		super();
		clear(false);
		//coverage = new DataCoverageStats();
		coverage = new BayesianCoverageStats();
		setModel(model);
		setData(data);
	}
	
	/**
	 * 
	 * @param model
	 */
	public QSARDataset(Model model) {
		super();
		clear(false);
		setModel(model);
		setData(new AllData());
		data.setEditable(true);
		data.initialize(model.getDescriptors());
		//coverage = new DataCoverageStats();
		coverage = new BayesianCoverageStats();
	}	
	/**
	 * 
	 */
	public QSARDataset() {
		super();
		clear(false);
		coverage = new BayesianCoverageStats();
		
		Model model = new Model("", new ModelType("Linear Regression"));
		//model.addDescriptor(new Descriptor("X1",ReferenceFactory.createEmptyReference()));

		model.setReference(ReferenceFactory.createEmptyReference());
		
		setModel(model);
		model.setEditable(true);
		setData(new AllData());
		data.initialize(model.getDescriptors());		
	}
	/*
	public QSARDataset(int ndescriptors) {
		ids = new java.util.Vector();
		coverage = new DataCoverageStats();		
		caption = "Model "+Long.toString(System.currentTimeMillis());		
		setNdescriptors(ndescriptors,false);
		fireDatasetEvent();		
		fireChangeCoverageEvent();		
	}
	public QSARDataset(int ndescriptors,int npoints) {
		ids = new java.util.Vector();
		coverage = new DataCoverageStats();		
		caption = "Model "+Long.toString(System.currentTimeMillis());		
		setNdescriptors(ndescriptors,false);
		setNpoints(npoints,false);
		initData();
		fireDatasetEvent();		
		fireChangeCoverageEvent();		
	}
	*/
	public boolean isEmpty() {
		return (data == null) || (data.isEmpty());
	}
	public void refreshMethod() {
		fireAmbitObjectEvent(coverage);		
	}
	public void setMethod(DataCoverage method) {
		coverage.setMethod(method);
		fireAmbitObjectEvent(coverage);		
	}
	public void clear(boolean notify) {
		if (model != null) model.clear();
		readonly = false;
		if (data != null) data.clear();
		if (coverage != null) coverage.clear();
		if (notify) {
			fireAmbitObjectEvent();
			fireAmbitObjectEvent(coverage);
		}
	}
	
	/*
	public void setNpoints(int npoints,boolean notify) {
		model.setN_Points(npoints);
		data.set NN_Points(npoints);
		//TODO setnpoints
		coverage.clear();
		if (notify) {
			fireAmbitObjectEvent();
			coverage.fireAmbitObjectEvent();
		}
	}
	
	public void setNdescriptors(int ndescriptors,boolean notify) {
		model.setN_Descriptors(ndescriptors);
		/*
		if (this.ndescriptors != ndescriptors) {       
			this.ndescriptors = ndescriptors;			
			xnames = new String[ndescriptors];
			for (int i = 0; i < ndescriptors; i++) {
				xnames[i] = "X"+i;
			}
			data = null;
			coverage.clear();
		}
		if (notify) {
			fireAmbitObjectEvent();
			coverage.fireAmbitObjectEvent();
		}
		
	
		//TODO setndescriptors
	}
*/
	public void setDescriptors(DescriptorsList xdescriptors,boolean notify) {
		model.setDescriptors(xdescriptors);
		data.initialize(xdescriptors);
		if (notify) {
			fireAmbitObjectEvent();
			fireAmbitObjectEvent(coverage);
		}
	}
	
	/**
	 * @return Returns the xnames.
	 */
	
	public String getXname(int i) {
		return data.getXName(i);
		
	}
	
	/**
	 * @return Returns the yname.
	 */
	public String getYname() {
		return data.getYPredictedName();
	}
	/**
	 * @return Returns the npoints.
	 */
	public int getNpoints() {
		return data.getNPoints();
	}
	
	public int getNdescriptors() {
		return data.getNDescriptors();
	}
	
	/**
	 * @return Returns the adEstimated.
	 */
	public boolean isAdEstimated() {
		return adEstimated;
	}
	/**
	 * @return Returns the visible.
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @param visible The visible to set.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		fireAmbitObjectEvent();
		fireAmbitObjectEvent(coverage);
	}
	/*
	public synchronized void addDatasetListener(ADdatasetListener listener) 
	{
	     dsListeners.add(ADdatasetListener.class, listener);
	}

	public void removeDatasetListener(ADdatasetListener listener) 
	{
		dsListeners.remove(ADdatasetListener.class, listener);
	}
	*/

//	 Notify all listeners that have registered interest for
	 // notification on this event typeValue.  The event instance 
	 // is lazily created using the parameters passed into 
	 // the fire method.
/*
	 public void fireDatasetEvent() {
	     // Guaranteed to return a non-null array
	     Object[] listeners = dsListeners.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==ADdatasetListener.class) {
	             // Lazily create the event:
	             if (changeEvent == null)
	                 changeEvent = new ADChangeEvent(this,this,null);
	             ((ADdatasetListener)listeners[i+1]).datasetChanged(changeEvent);
	         }
	     }
         changeEvent = null;
	     
	     
	 }

	 public void fireChangeCoverageEvent() {
	     // Guaranteed to return a non-null array
	     Object[] listeners = dsListeners.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==ADdatasetListener.class) {
	             // Lazily create the event:
	             if (changeEvent == null)
	                 changeEvent = new ADChangeEvent(coverage,this,null);
	             ((ADdatasetListener)listeners[i+1]).datasetChanged(changeEvent);
	         }
	     }
         changeEvent = null;	             
	     
	 }
	 	*/
	/**
	 * @return Returns the editable.
	 */
	public boolean isReadonly() {
		return readonly;
	}
	/**
	 * 
	 * @param readonly
	 */
	public void setReadonly(boolean readonly) {
		if (this.readonly != readonly) {
			this.readonly = readonly;
			fireAmbitObjectEvent();
		}
	}
	/**
	 * 
	 * @return true if successful
	 */
	public boolean estimateCoverage() {
		//TODO verify is it already exists
		boolean r = coverage.estimate(this);
		fireAmbitObjectEvent();
		fireAmbitObjectEvent(coverage);
		return r;
	}
	
	public boolean estimateCoverage(DataCoverage method) {
		//TODO verify is it already exists
		coverage.setMethod(method);
		boolean r = coverage.estimate(this);
		fireAmbitObjectEvent(coverage);
		return r;
	}
	public boolean assessCoverage(DataCoverage method) {
		//TODO verify is it already exists
		coverage.setMethod(method);		
		boolean r = coverage.assess(this);
		fireAmbitObjectEvent(coverage);
		return r;
	}
	/*
	public void setXname(int i, String name) {
		if (xnames != null) xnames[i] = name; 
	}
	public String getXname(int i) {
		if (xnames != null) return xnames[i]; else return "NA"; 
	}	
	public void randomData(double[] meanvector) {
		if ((npoints >0) && (ndescriptors >0)) {
			initData();
			ambit2.stats.Tools.GaussianClusters(data,npoints,ndescriptors, meanvector,2);
			for (int i = 0; i < npoints; i++) ids.add(Integer.toString(i+1));
			filename = "random data";
			fireDatasetEvent();			
			coverage.clear();
			fireChangeCoverageEvent();			
		}
	}
	*/
	public void getXData(int point, double[] values, int[] index) throws Exception {
		for (int i = 0; i < index.length; i++) 
			values[i] = data.getDataItem(point,index[i]);
	}

	public double getXData(int point, int index) throws Exception {
			return data.getDataItem(point,index);
	}
	
	
	public boolean save(OutputStream out) {
		boolean writeCoverage = coverage.isAssessed();
		char d = ',';
		char textD = '"';
		if (out == null) return false;
		try {
			DataOutputStream ds = new DataOutputStream(out);
			
			ds.writeBytes(data.getHeader(d));
			if (writeCoverage) coverage.writeHeader(ds,d,textD); 
			/*{
				ds.write(textD);
				ds.writeBytes(coverage.getName());ds.write(textD);
				ds.write(d);ds.write(textD);
				ds.writeBytes("in domain");ds.write(textD);
			}*/
			ds.write('\n');
			int nd = getNdescriptors();
			int np = getNpoints();
			System.err.println("points\t"+np+"\tdescriptors\t"+nd);
			String  val;
			for (int i = 0; i < np; i ++) {
				ds.writeBytes(data.rowToString(i,d));
				try {
					if (writeCoverage) coverage.writeData(i,ds,d);
				} catch (Exception e) {
					//TODO exception
					e.printStackTrace();
				}

				ds.write('\n');
				
			}
			return true;
			
			//ds.close();
		} catch (Exception ex) {
			//TODO exception
			ex.printStackTrace();
			return false;
		}
	}
	
	public void setStreamName(String aname) {
		if (model.getName().equals("")) model.setName(aname);
		//TODO set name without extension
		model.setFileWithData(aname);
		//setName(aname);
		fireAmbitObjectEvent();
	}
	
	public void setType(String type) {
	    originType = type;
		//model.setType(type); ??
	}
	public String getType() {
	    return originType;
		//return model.getType();	
	}
	/* (non-Javadoc)
     * @see ambit2.io.IReadWriteStream#load(java.io.InputStream)
     */
    public boolean load(InputStream in) throws AmbitIOException {
         try {
            IIteratingChemObjectReader reader = FileInputState.getReader(in,getType());
            reader.addChemObjectIOListener(new AmbitSettingsListener(null,IOSetting.LOW));
	        return load(reader);
	     
		} catch (IOException x) {
		    throw new AmbitIOException(x);
		}    
	}
	public boolean load(IIteratingChemObjectReader reader) throws IOException {
	    data.clear();
	    DescriptorsList descriptors = null;
	    IMolecule mol;
	    Object o;
		while (reader.hasNext()) {
		    o = reader.next();
		    if (o instanceof IMolecule) {
		        mol = (IMolecule) o;
		        if (descriptors == null) {
		            descriptors = ((MolPropertiesIOSetting) reader.getIOSettings()[0]).getSelectedProperties();
		            data.initialize(descriptors);
		        }
		        
		        data.addRow(new Compound(mol),descriptors,mol.getProperties(),false);
		    }
		}
		reader.close();
		readonly = true;
		coverage.clear();
		
		setModified(true);
		syncWithModel(model);
		return true;
	}
	/*
	public boolean load(InputStream in) {
		//TODO implement load
		char d = ',';
		if (getType().equals("txt"))	d = '\t';		
		InputStreamReader ir = new InputStreamReader(in);
		boolean result = loadCSVformat(ir,d,new DefaultColumnTypeSelection());
		try {
		ir.close();
		} catch (IOException e) {
			//TODO exception
			e.printStackTrace();
		}
		return result;
		
	}
	
	public boolean loadCSVformat(String filename) {
		return loadCSVformat(filename,new DefaultColumnTypeSelection());
	}
	public boolean loadCSVformat(String filename,IColumnTypeSelection ctSelection) {
		char d = ',';
		if (AmbitFileFilter.getSuffix(filename) == "txt") 
			d = '\t';
		FileReader fr = null;
		try {
			fr = new FileReader(filename) ;
		} catch (FileNotFoundException x) {
			//TODO exception
			x.printStackTrace();
			return false;
		}
		boolean r = false;
		try {
			loadCSVformat(fr,d,ctSelection);
		} catch (Exception x) {
			x.printStackTrace();
		}
		try {
		fr.close();
		} catch (IOException x) {
			//TODO exception
			x.printStackTrace();
		}
		return r;
	}
	
	public boolean loadCSVformat(
					InputStreamReader reader,
					char delimiter,
					IColumnTypeSelection ctSelection) {
		boolean result = false;
		try {
						
			CSV csv = new CSV(delimiter,'"');
			csv.setColumnOption(ctSelection);
			data.clear();
			csv.setReadData(data);

			csv.readDataset(reader);
			reader.close();
			reader = null;
			csv.setReadData(null);
			csv = null;
			//this.filename = filename;
			//caption = filename;
			readonly = true;
			coverage.clear();
			
			setModified(true);
			result = true;
			syncWithModel(model);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
		//fireAmbitObjectEvent();
		fireAmbitObjectEvent(coverage);
		return result;
	}

	*/
	/**
	* Reallocates an array with a new size, and copies the contents
	* of the old array to the new array.
	* @param oldArray  the old array, to be reallocated.
	* @param newSize   the new array size.
	* @return          A new array with the same contents.
	*/
	private static Object resizeArray (Object oldArray, int newSize) {
	   int oldSize = java.lang.reflect.Array.getLength(oldArray);
	   Class elementType = oldArray.getClass().getComponentType();
	   Object newArray = java.lang.reflect.Array.newInstance(
	         elementType,newSize);
	   int preserveLength = Math.min(oldSize,newSize);
	   if (preserveLength > 0)
	      System.arraycopy (oldArray,0,newArray,0,preserveLength);
	   return newArray; }

	public String toString() {
		return model.toString() + "\tDescriptors\t" + getNdescriptors() + "\tpoints\t" + getNpoints();
	}
	

	
	/**
	 * @return Returns the coverage.
	 */
	public IDataCoverageStats getCoverage() {
		return coverage;
	}
	
	public double getResidual(int row) {
		return data.getYResidual(row);
	}
	public double getYObserved(int row) {
		return data.getYObserved(row);
	}
	public double getYPredicted(int row) {
		return data.getYPredicted(row);
	}
	public double getCoverage(int row) {
	    if (coverage.isAssessed())
	        try {
	        return coverage.getCoverage(row);
	        } catch (Exception x) {
	            return Double.NaN;
	        }
	    else return Double.NaN;
	}		
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
		fireAmbitObjectEvent(coverage);		
	}
	public String getName() {
		return model.getName();
	}
	public void setName(String name) {
		//model.setName(name);
	}
	public AllData getData() {
		return data;
	}
	public void setData(AllData data) {
		this.data = data;
		fireAmbitObjectEvent();
	}
	 
	public Object clone() throws CloneNotSupportedException {
		//TODO clone
		throw new CloneNotSupportedException();
	}
	protected void syncWithModel(Model model) {
		if (model.getN_Descriptors() == 0) {
			DescriptorsList d = null;
			try {
				d = (DescriptorsList) (data.getXDescriptors().clone());
				model.setDescriptors(d);					
			} catch (CloneNotSupportedException x) {
				x.printStackTrace();
			}
					
		}
	}
}



