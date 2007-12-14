/*
 * Created on 26-Mar-2005
 *
 */
package ambit.data.descriptors;

import ambit.data.literature.LiteratureEntry;
import ambit.io.IColumnTypeSelection;

/**
 * Contains information about a single descriptor ( no descriptor values!)<br>
 * The same as {@link DescriptorDefinition} with the addition of a model specific info
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class Descriptor extends DescriptorDefinition {
	protected AmbitColumnType typeInModel = null;
	protected int orderInModel = 1; //order in a model	
	/**
	 * @param name descriptor name
	 * @param reference  literature reference
	 */
	public Descriptor(String name, LiteratureEntry reference) {
		super(name, reference);
		typeInModel = new AmbitColumnType(IColumnTypeSelection._ctX);
	}

	/**
	 * @param name
	 * @param type Double=0, Integer=1, String=2
	 * @param reference
	 */
	public Descriptor(String name, int type, LiteratureEntry reference) {
		super(name, type, reference);
		typeInModel = new AmbitColumnType(IColumnTypeSelection._ctX);

	}

	/**
	 * @param name
	 * @param type type Double=0, Integer=1, String=2
	 * @param dtype as in IColumnTypeSelection
	 * @param reference
	 */
	public Descriptor(String name, int type, int dtype,
			LiteratureEntry reference) {
		super(name, type, dtype, reference);
		this.typeInModel = new AmbitColumnType(dtype);		

	}
	public void clear() {
		super.clear();
		typeInModel.clear();
		orderInModel = 1;	
	}
	public String toString() {
		return super.toString(); 
	}
	/**
	 * @return Returns the typeInModel.
	 */
	public AmbitColumnType getTypeInModel() {
		return typeInModel;
	}
	/**
	 * @param dtype The typeInModel to set.
	 */
	public void setTypeInModel(AmbitColumnType dtype) {
		boolean m = !typeInModel.equals(dtype);
		this.typeInModel.setId(dtype.getId());
		setModified(m);
	}
	/**
	 * @return Returns the orderInModel.
	 */
	public int getOrderInModel() {
		return orderInModel;
	}
	/**
	 * @param morder The orderInModel to set.
	 */
	public void setOrderInModel(int morder) {
		boolean m =  (orderInModel != morder);
		this.orderInModel = morder;
		setModified(m);
	}
	//TODO verify this
	public int compareTo(Object o) {
		Descriptor d = (Descriptor)o;
        int r = (orderInModel - d.orderInModel);
        return ((r < 0) ? -1 : ((r==0) ? name.compareTo(d.name) : 1)); 
        
	}

	
	public boolean equals(Object obj) {
	    if (obj instanceof Descriptor) {
		Descriptor d = (Descriptor) obj;
		return super.equals(d) && 
			(orderInModel == d.orderInModel) &&
			typeInModel.equals(d.typeInModel);
	    } else return  super.equals(obj);
	}
	public Object clone()  throws CloneNotSupportedException {
		Descriptor d = new Descriptor(
				name,typeValue.getId(),
				(LiteratureEntry) reference.clone());
		d.orderInModel = orderInModel;
		d.typeInModel = (AmbitColumnType) typeInModel.clone();
		return d;
	}
	
}
