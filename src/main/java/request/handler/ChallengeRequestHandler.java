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
		String key = null;
		String value = null;
		int index = 0;
		for (int i = 0; i < args.length; i++) {

			if (args[i].startsWith("--")) {

				key = args[i].substring(2);
			} else if (args[i].startsWith("-")) {

				key = args[i].substring(1);
			} else {
				arguments.put(args[i], null);
				continue;
			}

			index = key.indexOf('=');

			if (index == -1) {
				if ((i + 1) < args.length) {
					if (args[i + 1].charAt(0) != '-') {
						arguments.put(key, args[i + 1]);
						i++;
					} else {
						arguments.put(args[i], null);
					}
				} else {
					arguments.put(args[i], null);
				}
			} else {
				value = key.substring(index + 1);

				key = key.substring(0, index);

				arguments.put(key, value);
			}
		}
	}

	public String getValue(String key) {

		if (arguments.containsKey(key)) {
			return arguments.get(key);
		}

		return null;
	} 

	public boolean checkSetParams(BaseRequestDataModel data) {
		ArrayList<String> missingFields = new ArrayList<String>();
		Class<?> param[] = { String.class };
		for (String r : requiredFields) {
			if (this.getValue(r) == null)
				missingFields.add(r);
			else {
				try {
					Method method = data
							.getClass()
							.getSuperclass()
							.getDeclaredMethod(
									"set" + r.substring(0, 1).toUpperCase()
											+ r.substring(1), param);
					method.invoke(data, this.getValue(r));
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}

		}

		if (missingFields.size() == 0)
			return true;
		else {
			slf4jLogger.error("The following parameters are missing:"
					+ missingFields.toString());
			return false;
		}

	}
}
