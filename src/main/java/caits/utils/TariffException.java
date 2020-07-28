package caits.utils;

public class TariffException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int code;
	private final boolean isInternal;
	
	public TariffException(String msg, int code) {
     super(msg);
     this.code = code;
     this.isInternal = code >= 1380 && code <= 1399;
	}

	public int getCode() {
     return code;	
	}
	

	public boolean getInternal() {
     return isInternal;	
	}	
	
	public String toString() {
     if (code==0) return super.getMessage();
     else return super.getMessage() + " (код " + code + ")";
	}
}
