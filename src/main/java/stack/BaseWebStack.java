package stack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exception.WarningException;

public abstract class BaseWebStack {

	protected int step;

	protected ArrayList<String> callSequence;

	protected final Logger slf4jLogger = LoggerFactory
			.getLogger(BaseWebStack.class);

	public final void buildStack() {
		Class[] param = {};
		for (int i = step; i < this.callSequence.size(); i++) {
			try {
				slf4jLogger.debug("Calling sequence:"
						+ this.callSequence.get(i));
				Method method = this.getClass().getDeclaredMethod(
						this.callSequence.get(i), param);
				method.invoke(this, null);
			} catch (Exception e) {
				if (e instanceof InvocationTargetException) {
					InvocationTargetException et = (InvocationTargetException) e;
					Throwable targetEx = et.getTargetException();
					
					if (targetEx instanceof WarningException) {
						slf4jLogger.error("[Warning]Error during process: "+this.callSequence.get(i)+" Messages:"+targetEx.getMessage());
						if (i < this.callSequence.size()) {
							this.step++;
							this.buildStack();
						}
					}else
					{
						slf4jLogger.error("[Fatal]Error during process:"+this.callSequence.get(i)+" Messages:"+targetEx.getMessage());
					}
					
				}
				break;
			}
		}
	}

	public abstract void buildCallSequence();

	abstract void createKeyPair() throws Exception;

	abstract void createInstances() throws Exception;
	
	abstract void attachStorage() throws Exception;

	abstract void createLB() throws Exception;

	abstract void addInstancesToLB() throws Exception;
	
	abstract void createSSLTermination() throws Exception;

	abstract void setUpLBMonitoring() throws Exception;

	abstract void createCustomLBErrorPage() throws Exception;
	
	abstract void createDomain() throws Exception;

	abstract void attachDomainToLb() throws Exception;
	
	abstract void uploadCloudFiles() throws Exception;
	
	abstract void enableCDN() throws Exception;

	abstract void handleExceptions(Exception e);

}
