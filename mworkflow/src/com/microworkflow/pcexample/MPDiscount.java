package com.microworkflow.pcexample;

/**
 * Multi-policy discount; fixed amount credit.
 *  
 * @author dam
 */
public class MPDiscount implements Endorsement {

	protected double value;
	
	public MPDiscount(double value) {
		this.value=value;
	}

	public Double computeDebitOrCreditFor(Double basePremium) {
		return new Double(-value);
	}
}