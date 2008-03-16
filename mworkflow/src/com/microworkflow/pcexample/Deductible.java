package com.microworkflow.pcexample;

import java.lang.Double;
/**
 * Deductible; percentage of base premium debit or credit
 * @author dam
 */

public class Deductible implements Endorsement {

	protected Integer value;
	protected Table rateTable;
	
	protected void initialize(){
		rateTable=new Table();
		rateTable.put(new Integer(250),new Double(0.1));	// debit
		rateTable.put(new Integer(500),new Double(0));		// default deductible
		rateTable.put(new Integer(1000),new Double(-0.1));	// credit
		rateTable.put(new Integer(2000),new Double(-0.2));	// credit
	}
	
	public Deductible(int value) {
		initialize();
		this.value=new Integer(value);
	}

	public Double computeDebitOrCreditFor(Double amount) {
		double factor=((Double)rateTable.get(value)).doubleValue();
		return new Double(amount.doubleValue()*factor);
	}
}