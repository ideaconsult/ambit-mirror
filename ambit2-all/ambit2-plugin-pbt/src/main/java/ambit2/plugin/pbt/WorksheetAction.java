package ambit2.plugin.pbt;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ambit2.base.interfaces.IProcessor;

public class WorksheetAction<Target,Result> extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2720315911931642395L;
	protected int targetRow;
	protected int targetCol;
	protected int resultRow;
	protected int resultCol;
	protected int errorRow;
	protected int errorCol;
	
	public int getErrorRow() {
		return errorRow;
	}
	public void setErrorRow(int errorRow) {
		this.errorRow = errorRow;
	}
	public int getErrorCol() {
		return errorCol;
	}
	public void setErrorCol(int errorCol) {
		this.errorCol = errorCol;
	}
	protected boolean sourceExtended = false;
	public boolean isSourceExtended() {
		return sourceExtended;
	}
	public void setSourceExtended(boolean sourceExtended) {
		this.sourceExtended = sourceExtended;
	}
	public boolean isResultExtended() {
		return resultExtended;
	}
	public void setResultExtended(boolean resultExtended) {
		this.resultExtended = resultExtended;
	}
	protected boolean resultExtended = false;	
	protected PBTWorksheet worksheet;
	
	public PBTWorksheet getWorksheet() {
		return worksheet;
	}
	public void setWorksheet(PBTWorksheet worksheet) {
		this.worksheet = worksheet;
	}
	public WorksheetAction(String name) {
		super(name);
	}
	protected IProcessor<Target,Result> processor;
	
	public void actionPerformed(ActionEvent e) {
		setEnabled(false);
		Object o = null;
		if (isSourceExtended())
			o = worksheet.getExtendedCell(targetRow,targetCol);
		else
			o = worksheet.get(targetRow-1,targetCol-1);
		try {
			Result result = processor.process((Target)o);
			if (isResultExtended())
				worksheet.setExtendedCell(result,resultRow,resultCol);
			else
				worksheet.set(resultRow-1,resultCol-1,result);
		} catch (Exception x) {
			Throwable error = x;
			while (error.getCause()!=null) 
				error = error.getCause();
			/*
			if (error.getMessage()==null)
				worksheet.set(errorRow-1,errorCol-1,error);
			else
				worksheet.set(errorRow-1,errorCol-1,error.getMessage());
				*/
		}
		setEnabled(true);
	}
	public int getTargetRow() {
		return targetRow;
	}
	public void setTargetRow(int targetRow) {
		this.targetRow = targetRow;
	}
	public int getTargetCol() {
		return targetCol;
	}
	public void setTargetCol(int targetCol) {
		this.targetCol = targetCol;
	}
	public int getResultRow() {
		return resultRow;
	}
	public void setResultRow(int resultRow) {
		this.resultRow = resultRow;
	}
	public int getResultCol() {
		return resultCol;
	}
	public void setResultCol(int resultCol) {
		this.resultCol = resultCol;
	}
	public IProcessor<Target, Result> getProcessor() {
		return processor;
	}
	public void setProcessor(IProcessor<Target, Result> processor) {
		this.processor = processor;
	}	
}
