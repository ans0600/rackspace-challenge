package exception;

public class WarningException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String message;

	public WarningException(String msg)
	{
		super();
		this.message=msg;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
}
