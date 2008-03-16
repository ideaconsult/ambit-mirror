/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.pcexample;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This class represents an insurance premium. It has a value
 * and a set of debits and credits. Removing the control aspect
 * leaves it fairly simple. The domain knowledge it encapsulates
 * revolves around performing multiplications, adding a debit or
 * a credit, and processing the debits and credits.
 * 
 * @author dam
 */
public class Premium extends Number {
	
	protected HashSet debitsAndCredits=new HashSet();

	public Premium() {
		this(new Double(0));
	}

	public Premium(Double value) {
		super(value);
	}

	public Premium premiumWith(Double value) {
		return new Premium(value);
	}
	
	public Premium mult(Double factor) {
		double product=value.doubleValue()*factor.doubleValue();
		return new Premium(new Double(Math.round(product)));
	}
	
	public void addDebitOrCredit(Double number) {
		debitsAndCredits.add(number);
	}

	public Premium adjustValue() {
		double adjustedValue=value.doubleValue();
		for (Iterator iter = debitsAndCredits.iterator(); iter.hasNext();) {
			Double debitOrCredit = (Double) iter.next();
			adjustedValue+=debitOrCredit.doubleValue();
		}
		return new Premium(new Double(adjustedValue));
	}

}
