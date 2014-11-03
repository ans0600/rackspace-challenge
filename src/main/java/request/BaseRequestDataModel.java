package request;


public abstract class BaseRequestDataModel {
	
	protected String userName;
	
	protected String apiKey;
	
	protected String region;
	
	protected String zone;
	
	protected String pubKeyPath;
	
	protected String keyPairName;
	
	protected String imageID;
	
	protected String flavorID;
	
	protected String numOfInstances;
	
	protected String stackName;
	
	protected String pathToLBErrorPage;
	
	protected String domainName;
	
	protected String containerName;
	
	protected String networkID;
	
	abstract public String getCloudServerRegion();
	
	abstract public String getCloudLoadBalancersRegion();
	
	abstract public String getCloudDnsRegion();
	
	abstract public String getCloudFileRegion();
	
	abstract public String getCloudBlockServiceRegion();
	
	
	public String getStackName() {
		return stackName;
	}

	public void setStackName(String stackName) {
		this.stackName = stackName;
	}

	public abstract void validate();

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	protected String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getPubKeyPath() {
		return pubKeyPath;
	}

	public void setPubKeyPath(String pubKeyPath) {
		this.pubKeyPath = pubKeyPath;
	}

	public String getKeyPairName() {
		return keyPairName;
	}

	public void setKeyPairName(String keyPairName) {
		this.keyPairName = keyPairName;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public String getFlavorID() {
		return flavorID;
	}

	public void setFlavorID(String flavorID) {
		this.flavorID = flavorID;
	}

	public String getNumOfInstances() {
		return numOfInstances;
	}

	public void setNumOfInstances(String numOfInstances) {
		this.numOfInstances = numOfInstances;
	}

	public String getPathToLBErrorPage() {
		return pathToLBErrorPage;
	}

	public void setPathToLBErrorPage(String pathToLBErrorPage) {
		this.pathToLBErrorPage = pathToLBErrorPage;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getNetworkID() {
		return networkID;
	}

	public void setNetworkID(String networkID) {
		this.networkID = networkID;
	}
	


}
