package ambit2.db.simiparity.space;

import java.io.Serializable;

import ambit2.base.interfaces.IStructureRecord;

public class QMapSpace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -888827119842958432L;
	protected QMap qmap;
	protected IStructureRecord record;
	protected double g2;
	protected double a,b,c,d;

	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	public double getC() {
		return c;
	}
	public void setC(double c) {
		this.c = c;
	}
	public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	protected double fisher;

	public QMapSpace() {
		this(null);
	}
	public QMapSpace(QMap qmap) {
		setQmap(qmap);
	}
	public void clear() {
		getQmap().clear();
		getQmap().getDataset().setID(-1);
		record.clear();
		g2=Double.NaN;
		fisher=Double.NaN;
	}

	public QMap getQmap() {
		return qmap;
	}
	public void setQmap(QMap qmap) {
		this.qmap = qmap;
	}
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	public double getG2() {
		return g2;
	}
	public void setG2(double g2) {
		this.g2 = g2;
	}
	public double getFisher() {
		return fisher;
	}
	public void setFisher(double fisher) {
		this.fisher = fisher;
	}	
}

