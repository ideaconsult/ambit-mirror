package ambit2.rest.task.dsl.interfaces;

import ambit2.rest.task.Task.TaskStatus;

public interface IOTTask extends IOTObject {
	String getCreator();
	void setCreator(String object);
	TaskStatus getStatus();
	void setStatus(TaskStatus status);
	double getPercentCompleted();
	void setPercentCompleted(double percent);
	String getResultURI();
	void setResultURI(String result);
	long getStartTime();
	void setStartTime(long timestamp);
}
