package request;

import java.util.HashMap;

import utils.FieldDump;

public class Challenge11RequestDataModel extends BaseRequestDataModel {

	@Override
	public void validate() {
		// TODO Auto-generated method stub

	}

	public String getCloudServerRegion() {
		return "rackspace-cloudservers-" + this.getRegion();
	}

	public String getCloudLoadBalancersRegion() {

		return "rackspace-cloudloadbalancers-" + this.getRegion();
	}

	public String getCloudDnsRegion() {
		return "rackspace-clouddns-" + this.getRegion();
	}

	public String getCloudFileRegion() {
		return "rackspace-cloudfiles-" + this.getRegion();
	}
	
	public String getCloudBlockServiceRegion()
	{
		return "rackspace-cloudblockstorage-"+ this.getRegion();
	}

	public String[] getCompulsoryProperties() {
		return new String[] { "userName", "apiKey", "region", "zone",
				"pubKeyPath", "keyPairName", "imageID", "flavorID",
				"numOfInstances", "stackName", "pathToLBErrorPage", "domainName", "containerName","networkID" };
	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		HashMap<String, Object> data = FieldDump.dump(this, this.getClass()
				.getSuperclass());

		for (String k : data.keySet()) {
			sb.append(k + ": " + data.get(k) + "\n");
		}

		return sb.toString();

	}

}
