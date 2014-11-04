package request.handler;

import model.WebStackDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.Challenge10RequestDataModel;
import stack.Challenge10WebStack;

public class Challenge10RequestHandler extends ChallengeRequestHandler {

	final String challengeID="10";
	
	private final Logger slf4jLogger = LoggerFactory
			.getLogger(Challenge10RequestHandler.class);
	
	protected Challenge10RequestDataModel requestData;
	
	@Override
	public boolean shouldProcess(String id) {
		return this.challengeID.equals(id);
	}

	@Override
	public void process(String[] params) {
		this.requestData=new Challenge10RequestDataModel();
		this.requiredFields=this.requestData.getCompulsoryProperties();
		this.processCommandArgs(params);
		if(this.checkSetParams(this.requestData))
		{
			slf4jLogger.info("User Input:");
			slf4jLogger.info("\n"+this.requestData.toString());
			WebStackDataModel d=new WebStackDataModel();
			Challenge10WebStack stack=new Challenge10WebStack(d, this.requestData);
			stack.buildCallSequence();
			slf4jLogger.info("Runing Web Stack 10");
			stack.buildStack();
			slf4jLogger.info("Stack Info:");
			slf4jLogger.info("\n"+d.toString());
		}
		
	}
	
	

}
