package ambit2.core.io.study;

import ambit2.base.data.study.ReliabilityParams._r_flags;

public interface IStudyPrinter {
	public void print(int row, int column, int order, String value, boolean isResult, _r_flags studyResultType);
	public void printHeader(int row, int column, int order, String value);
}
