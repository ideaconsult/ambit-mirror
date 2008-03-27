/*
 * Created on 2005-6-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ambit2.domain;

import java.io.DataOutputStream;

import ambit2.data.molecule.CompoundsList;

/**
 * @author Vedina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IDataCoverageStats {
	DataCoverage getMethod();
	void setMethod(DataCoverage method);
	int getNoIn();	
	int getNoOut();
	double getRmseIn();
	double getRmseOut();
	double getRmseAll();
	boolean isAssessed();
	boolean estimate(QSARDataset ds);
	boolean assess(QSARDataset ds);
	boolean isInDomain(int point) throws Exception;
	double getCoverage(int point) throws Exception;
	int getNoNotAssessed();
	void setNoNotAssessed(int noNotAssessed);
	CompoundsList getCompoundsInDomain(QSARDataset ds);
	CompoundsList getCompoundsOutOfDomain(QSARDataset ds);
	CompoundsList getCompoundsNotAssessed(QSARDataset ds);
	void writeData(int index,DataOutputStream out,char delimiter) throws Exception;
	void writeHeader(DataOutputStream out,char delimiter, char textDelimiter) throws Exception;
}
