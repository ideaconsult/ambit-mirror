package ambit.ui.domain;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;

import ambit.domain.QSARDataset;

public class ADCategoryDataSet extends AbstractDataset implements
		CategoryDataset {
	protected ADBarPlotsType plotType;
	protected QSARDataset dataset = null;
	protected List bins = null;
	protected List series = null;
	
	public ADCategoryDataSet(QSARDataset dataset,ADBarPlotsType plotType) {
		super();
		this.plotType = plotType;
		this.dataset = dataset;
		bins = new ArrayList(10);
		for (int i=0; i < bins.size();i++) bins.set(i,new Integer(i));
		series = new ArrayList();
		series.add("Frequency");
	}
	public Comparable getRowKey(int arg0) {
		return plotType.getXname(dataset,plotType.getXindex());
	}

	public int getRowIndex(Comparable arg0) {
		// Series number
		return 0;
	}

	public List getRowKeys() {
		return series;
	}

	public Comparable getColumnKey(int arg0) {
		return (Integer)bins.get(arg0);
	}

	public int getColumnIndex(Comparable arg0) {
		return bins.indexOf(arg0);
	}

	public List getColumnKeys() {
		return bins;
	}

	public Number getValue(Comparable arg0, Comparable arg1) {
		return new Integer(bins.indexOf(arg1));
	}

	public int getRowCount() {
		return 1;
	}

	public int getColumnCount() {
		return bins.size();
	}

	public Number getValue(int arg0, int arg1) {
		return (Integer)(bins.get(arg1));
	}

}
