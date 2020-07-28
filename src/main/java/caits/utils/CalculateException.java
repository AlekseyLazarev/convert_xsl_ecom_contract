package caits.utils;

public class CalculateException extends Exception {
	private static final long serialVersionUID = 1L;
	
	final public int code;

	public CalculateException(String msg) {
		super(msg);
		this.code = 0;
	}
	
	public CalculateException(String msg, int code) {
		super(msg);
		this.code = code;
	}	

}
