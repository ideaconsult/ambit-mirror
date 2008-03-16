package com.microworkflow.pcexample;
/**
 * Protection device discount; percentage of base premium credit.
 *  
 * @author dam
 */
public class PDDiscount implements Endorsement {
	
	protected double value;
	
	public PDDiscount(double value) {
		this.value=value;
	}

	public Double computeDebitOrCreditFor(Double basePremium) {
		return new Double(-value*basePremium.doubleValue());
	}
	
}