package request.handler;

import model.WebStackDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.Challenge11RequestDataModel;
import stack.Challenge11WebStack;

public class Challenge11RequestHandler extends Challenge10RequestHandler {

	final String challengeID="11";

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(Challenge11RequestHandler.class);
	
	protected Challenge11RequestDataModel requestData;
	
	@Override
	public boolean shouldProcess(String id) {
		return this.challengeID.equals(id);
	}
	
	@Override
	public void process(String[] params) {
		this.requestData=new Challenge11RequestDataModel();
		this.requiredFields=this.requestData.getCompulsoryProperties();
		this.processCommandArgs(params);
		//System.err.println(this.getValue("apiKey"));
		if(this.checkSetParams(this.requestData))
		{
			slf4jLogger.info("User Input:");
			slf4jLogger.info("\n"+this.requestData.toString());
			WebStackDataModel d=new WebStackDataModel();
			Challenge11WebStack stack=new Challenge11WebStack(d, this.requestData);
			stack.buildCallSequence();
			slf4jLogger.info("Runing Web Stack 11");
			stack.buildStack();
			slf4jLogger.info("Stack Info:");
			slf4jLogger.info("\n"+d.toString());
		}
		
	}
}
