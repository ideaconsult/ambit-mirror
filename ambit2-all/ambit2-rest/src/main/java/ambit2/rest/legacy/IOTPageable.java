package ambit2.rest.legacy;


/**
 * Pageable OpenTox resources (support ?page={pageno}&pagesize={size} parameters, e.g. {@link OTDataset}
 * @author nina
 *
 * @param <T>
 */
@Deprecated
public interface IOTPageable<T extends IOTObject>{
	
	 T getPage(int page,int pageSize) throws Exception;
}
