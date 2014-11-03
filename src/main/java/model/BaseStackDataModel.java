package model;

import java.util.ArrayList;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;

public class BaseStackDataModel {

	protected ArrayList<ServerCreated> serverCreated;
	
	protected ArrayList<Server> servers;
	
	protected ArrayList<LoadBalancer> loadBalancers;
	
	protected String cdnUri;
	
	public BaseStackDataModel()
	{
		this.serverCreated=new ArrayList<ServerCreated>();
		this.servers=new ArrayList<Server>();
		this.loadBalancers=new ArrayList<LoadBalancer>();
	}

	public ArrayList<ServerCreated> getServerCreated() {
		return serverCreated;
	}

	public void setServerCreated(ArrayList<ServerCreated> serverCreated) {
		this.serverCreated = serverCreated;
	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}

	public ArrayList<LoadBalancer> getLoadBalancers() {
		return loadBalancers;
	}

	public void setLoadBalancers(ArrayList<LoadBalancer> loadBalancers) {
		this.loadBalancers = loadBalancers;
	}

	public String getCdnUri() {
		return cdnUri;
	}

	public void setCdnUri(String cdnUri) {
		this.cdnUri = cdnUri;
	}
	
	
	
}
