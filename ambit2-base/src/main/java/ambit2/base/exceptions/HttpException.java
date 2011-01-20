package ambit2.base.exceptions;

import ambit2.base.processors.ProcessorException;

public class HttpException extends ProcessorException {
	protected int code;
	protected String url;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6554884630280756615L;
	public HttpException(String url, int code, String message) {
		super(null,message);
		this.code = code;
		this.url = url;
	}
	@Override
	public String toString() {
		return String.format("%s %s %s",url,code,getMessage());
	}
}
