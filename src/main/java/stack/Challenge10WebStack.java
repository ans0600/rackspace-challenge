package stack;

import java.util.ArrayList;

import model.WebStackDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.Challenge10RequestDataModel;
import service.CloudFileService;
import service.WebStackService;
import exception.WarningException;

public class Challenge10WebStack extends BaseWebStack {

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(Challenge10WebStack.class);

	protected WebStackDataModel stackData;

	protected Challenge10RequestDataModel requestData;

	protected WebStackService apiService;
	
	protected CloudFileService cloudFileService;

	public Challenge10WebStack(WebStackDataModel stackData,
			Challenge10RequestDataModel requestData) {
		this.stackData = stackData;
		this.requestData = requestData;
		this.apiService = WebStackService.getInstance(stackData, requestData);
		this.cloudFileService=CloudFileService.getInstance(stackData, requestData);
	}

	@Override
	void createInstances() throws Exception {
		slf4jLogger.info("Start Creating Instances");
		int res = this.apiService.createInstancesSync();
		if (res != WebStackService.SUCCESS)
			throw new Exception(
					"[Error] One or more instances failed to create");
		slf4jLogger.info("Instances Creation Finished");

	}

	@Override
	void createLB() throws Exception {
		slf4jLogger.info("Start Creating Load Balancer(s)");
		int res = this.apiService.createLBSync();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to create LB");
		slf4jLogger.info("Creating Load Balancer(s) Finished");

	}

	@Override
	void addInstancesToLB() throws Exception {

	}

	@Override
	void setUpLBMonitoring() throws Exception {
		slf4jLogger.info("Start setting up LB monitoring");
		int res = this.apiService.createLBMonitoring();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to create LB Monitoring");
		slf4jLogger.info("Finish setting up LB monitoring");

	}

	@Override
	void createCustomLBErrorPage() throws Exception {
		slf4jLogger.info("Start setting up custom LB Error Page");
		int res= this.apiService.createCustomLBErrorPage();
		if (res != WebStackService.SUCCESS)
			throw new Exception(
					"[Error] Unable to create LB custom LB Error Page");
		
		slf4jLogger.info("Finish setting up custom LB Error Page");

	}

	@Override
	void createKeyPair() throws Exception {
		slf4jLogger.info("Start Creating KeyPair");
		int res = this.apiService.createKeyPair();
		switch (res) {
		case WebStackService.ERROR_FILE_NOT_FOUND:
			throw new Exception(
					"[Fatal Error] Unable to find key file specified");
		case WebStackService.ERROR_KEY_ALREADY_EXIST:
			throw new WarningException("[Warning] KeyPair already exist");
		}

		slf4jLogger.info("KeyPair Creation Finished");

	}

	@Override
	void handleExceptions(Exception e) {
		System.err.println(e instanceof WarningException);
		slf4jLogger.error("Error when creating stack: " + e.getMessage());
	}

	@Override
	void attachDomainToLb() throws Exception {
		slf4jLogger.info("Start Attach Domain To LB");
		int res = this.apiService.attachDomainToLb();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to attach domain to LB");
		slf4jLogger.info("Attach Domain To LB Finished");

	}
	
	@Override
	void uploadCloudFiles() throws Exception {
		slf4jLogger.info("Start Upload Error Page");
		int res=this.cloudFileService.upLoadFile(this.requestData.getPathToLBErrorPage());
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to Upload Error Page to CloudFile");
		slf4jLogger.info("Upload Error Page Finished");
		
	}
	
	@Override
	void enableCDN() throws Exception {
		slf4jLogger.info("Start Enable CDN for Container");
		int res=this.cloudFileService.enableCDN();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to Enable CDN for Container");
		slf4jLogger.info("Enable CDN for Container Finished");
	}

	@Override
	public void buildCallSequence() {
		this.callSequence = new ArrayList<String>();
		this.callSequence.add("createKeyPair");
		this.callSequence.add("createInstances");
		this.callSequence.add("createLB");
		this.callSequence.add("setUpLBMonitoring");
		this.callSequence.add("createCustomLBErrorPage");
		this.callSequence.add("attachDomainToLb");
		this.callSequence.add("uploadCloudFiles");
		this.callSequence.add("enableCDN");

	}

	@Override
	void attachStorage() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	void createDomain() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	void createSSLTermination() throws Exception {
		// TODO Auto-generated method stub
		
	}

	

	

}
