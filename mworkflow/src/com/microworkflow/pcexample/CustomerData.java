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
 * Instances of this class hold data about the policy holder. A real application
 * would initialize this data from a GUI. Because the control aspect has been
 * stripped off, these objects act as simple data containers. Therefore, the
 * behavior revolves around setters and getters.
 * 
 * @author dam
 */
public class CustomerData extends Object {
	public static final Integer HO_00_03=new Integer(3);
	public static final Integer HO_00_06=new Integer(6);
	public static final Integer CONSTRUCTION_TYPE_FRAME=new Integer(1);
	public static final Integer CONSTRUCTION_TYPE_MASONRY=new Integer(2);
	public static final Integer COVERAGE_A=new Integer(1);
	public static final Integer COVERAGE_C=new Integer(3);
	
	protected Integer policyType;
	protected Integer territory;
	protected Integer constructionType;
	protected Integer protectionClass;
	protected Integer coverageType;
	protected Double coverageAmount;
	protected Double increaseOrdinanceOrLawCoverage=new Double(0);
	protected Boolean superiorConstruction;
	protected HashSet endorsements=new HashSet();

	public Integer getTerritory() {
		return territory;
	}

	public void setPolicyType(Integer policyType) {
		this.policyType = policyType;
	}

	public Integer getPolicyType() {
		return policyType;
	}

	public void setTerritory(Integer territory) {
		this.territory = territory;
	}

	public Integer getConstructionType() {
		return constructionType;
	}

	public Integer getProtectionClass() {
		return protectionClass;
	}

	public void setConstructionType(Integer constructionType) {
		this.constructionType = constructionType;
	}

	public void setProtectionClass(Integer protectionClass) {
		this.protectionClass = protectionClass;
	}

	public Integer getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(Integer coverageType) {
		this.coverageType = coverageType;
	}

	public Double getCoverageAmount() {
		return coverageAmount;
	}

	public void setCoverageAmount(Double coverageAmount) {
		this.coverageAmount = coverageAmount;
	}

	public Double getIncreaseOrdinanceOrLawCoverage() {
		return increaseOrdinanceOrLawCoverage;
	}

	public void setIncreaseOrdinanceOrLawCoverage(Double increaseOrdinanceOrLawCoverage) {
		this.increaseOrdinanceOrLawCoverage = increaseOrdinanceOrLawCoverage;
	}

	public Boolean isSuperiorConstruction() {
		return superiorConstruction;
	}

	public void setSuperiorConstruction(Boolean superiorConstruction) {
		this.superiorConstruction = superiorConstruction;
	}

	public Iterator getEndorsements() {
		return endorsements.iterator();
	}

	public void addEdorsement(Endorsement e) {
		endorsements.add(e);
	}

}
