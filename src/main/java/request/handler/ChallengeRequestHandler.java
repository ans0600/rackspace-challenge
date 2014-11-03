package request.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.BaseRequestDataModel;

public abstract class ChallengeRequestHandler {

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(ChallengeRequestHandler.class);
	
	protected HashMap<String, String> arguments;
	
	protected String[] requiredFields;

	abstract public boolean shouldProcess(String id);

	abstract public void process(String[] params);

	protected void processCommandArgs(String[] args) {
		arguments = new HashMap<String, String>();

		// declare other helper variables
		String key = null;
		String value = null;
		int index = 0;

		// loop through the list of arguments
		for (int i = 0; i < args.length; i++) {

			// look for a key
			if (args[i].startsWith("--")) {
				// this is a key that starts with a --
				key = args[i].substring(2);
			} else if (args[i].startsWith("-")) {
				// this is a key that start with a -
				key = args[i].substring(1);
			} else {
				// this is a key that starts with nothing as a value
				arguments.put(args[i], null);

				// end this iteration of the loop
				continue;
			}

			// look for a value
			// does the key contain an = character?
			index = key.indexOf('=');

			if (index == -1) {
				// no - use the next argument as the value
				// is there a value to use
				if ((i + 1) < args.length) {
					// yes - but does the value look like a key?
					if (args[i + 1].charAt(0) != '-') {
						// no - so add the key and value
						arguments.put(key, args[i + 1]);

						// increment the count so we don't process this value
						// again
						i++;
					} else {
						// yes - so add just the key
						arguments.put(args[i], null);
					}
				} else {
					// no - so just add the key
					arguments.put(args[i], null);
				}
			} else {
				// yes - extract the value from the key
				value = key.substring(index + 1);

				// fix the key
				key = key.substring(0, index);

				// add the key and value to the map
				arguments.put(key, value);
			}
		} // end loop
	}
	
	public String getValue(String key) {
		  
	    // check to ensure the key is valid
	    if(arguments.containsKey(key)) {    
	      // return the key if found or null if it isn't
	      return arguments.get(key);    
	    }
	    
	    // invalid key so return null
	    return null;
	  } // end getValue method
	
	public boolean checkSetParams(BaseRequestDataModel data)
	{	
		ArrayList<String> missingFields=new ArrayList<String>();
		Class param[]={String.class};
		for(String r:requiredFields)
		{
			if(this.getValue(r)==null)missingFields.add(r);
			else
			{
				try{
					Method method = data.getClass().getSuperclass().getDeclaredMethod(
							"set"+r.substring(0,1).toUpperCase() + r.substring(1), param);
					method.invoke(data, this.getValue(r));
				}catch(Exception e)
				{
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			
		}
		
		if(missingFields.size()==0)return true;
		else
		{
			slf4jLogger.error("The following parameters are missing:"+missingFields.toString());
			return false;
		}
		
	}
}
