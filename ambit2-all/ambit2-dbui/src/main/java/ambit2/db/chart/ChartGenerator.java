package ambit2.db.chart;

import java.awt.Image;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.search.IQueryObject;



public abstract class ChartGenerator<T extends IQueryObject<IStructureRecord>> extends AbstractDBProcessor<T,Image> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5231855179501628510L;
	protected int width = 300;
	protected int height= 300;


}
