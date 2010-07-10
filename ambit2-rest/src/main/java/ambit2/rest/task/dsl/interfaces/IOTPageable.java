package ambit2.rest.task.dsl.interfaces;


public interface IOTPageable<T extends IOTObject>{
	
	 T getPage(int page,int pageSize) throws Exception;
}
