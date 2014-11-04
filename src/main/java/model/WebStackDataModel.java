package model;

import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIPWithId;

public class WebStackDataModel extends BaseStackDataModel {

	
	public WebStackDataModel()
	{
		super();
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		
		sb.append("Instance(s): \n");
		int i=0;
		for(Server s:this.getServers())
		{
			sb.append(s.getName()+" @ ");
			for(String key:s.getAddresses().keySet())
			{
				for(Address addr:s.getAddresses().get(key))
				{
					sb.append(" "+key+": "+addr.getAddr()+" ("+addr.getVersion()+") ");
				}
				
			}
			sb.append(" AdminPass:"+this.serverCreated.get(i++).getAdminPass().get()+"\n");
		}
		
		sb.append("Load Balancer(s): \n");
		for(LoadBalancer lb:this.getLoadBalancers())
		{
			sb.append(lb.getName()+" @ ");
			for(VirtualIPWithId vip:lb.getVirtualIPs())
			{
				sb.append(vip.getAddress()+" ("+vip.getIpVersion()+") ");
			}
			sb.append("\n");
		}
		if(this.getCdnUri()!=null)
		{
			sb.append("Container CDN URI: \n");
			sb.append(this.getCdnUri());
		}
		return sb.toString();
	}
	
	
	
}
