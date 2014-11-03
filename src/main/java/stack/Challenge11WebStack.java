package stack;

import java.util.ArrayList;

import model.WebStackDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.Challenge11RequestDataModel;
import service.CloudFileService;
import service.WebStackService;
import exception.WarningException;

public class Challenge11WebStack extends BaseWebStack {

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(Challenge11WebStack.class);

	protected WebStackDataModel stackData;

	protected Challenge11RequestDataModel requestData;

	protected WebStackService apiService;
	
	protected CloudFileService cloudFileService;

	public Challenge11WebStack(WebStackDataModel stackData,
			Challenge11RequestDataModel requestData) {
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
	void attachStorage() throws Exception {
		slf4jLogger.info("Start Attaching Storage");
		int res = this.apiService.createBlockStorageVolume();
		if (res != WebStackService.SUCCESS)
			throw new Exception(
					"[Error] Unable to attach storage to instance(s)");
		slf4jLogger.info(" Attaching Storage Finished");
		
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
		slf4jLogger.error("Error when creating stack: " + e.getMessage());
	}
	
	@Override
	void createDomain() throws Exception {
		slf4jLogger.info("Start Creating Domain");
		int res = this.apiService.createDomain();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to create domain");
		slf4jLogger.info("Creating Domain Finished");
	}

	@Override
	void attachDomainToLb() throws Exception {
		Thread.sleep(5000);
		slf4jLogger.info("Start Attach Domain To LB");
		int res = this.apiService.attachDomainToLb();
		if (res != WebStackService.SUCCESS)
			throw new Exception("[Error] Unable to attach domain to LB");
		slf4jLogger.info("Attach Domain To LB Finished");

	}
	
	@Override
	void uploadCloudFiles() throws Exception {

	}
	
	@Override
	void enableCDN() throws Exception {
	}

	@Override
	public void buildCallSequence() {
		this.callSequence = new ArrayList<String>();
		this.callSequence.add("createKeyPair");
		this.callSequence.add("createDomain");
		this.callSequence.add("createInstances");
		this.callSequence.add("attachStorage");
		this.callSequence.add("createLB");
//		// this.callSequence.add("addInstancesToLB");
//		this.callSequence.add("setUpLBMonitoring");
//		this.callSequence.add("createCustomLBErrorPage");
		this.callSequence.add("attachDomainToLb");
//		this.callSequence.add("uploadCloudFiles");
//		this.callSequence.add("enableCDN");

	}

	

	

	

	

}
