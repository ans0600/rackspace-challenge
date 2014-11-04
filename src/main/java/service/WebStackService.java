package service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import model.BaseStackDataModel;
import model.SSLDataModel;

import org.jclouds.ContextBuilder;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;
import org.jclouds.openstack.cinder.v1.predicates.VolumePredicates;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.predicates.ServerPredicates;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.features.DomainApi;
import org.jclouds.rackspace.clouddns.v1.features.RecordApi;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HealthMonitor;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SSLTermination;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIPWithId;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode.Condition;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.predicates.LoadBalancerPredicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.BaseRequestDataModel;
import utils.SSLUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class WebStackService extends BaseService {

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(WebStackService.class);

	private static WebStackService instance = null;

	private NovaApi apiHandler;

	private CloudLoadBalancersApi lbApiHandler;

	private CloudDNSApi dnsApiHandler;

	private CinderApi cbsApi;

	private BaseRequestDataModel request;

	private BaseStackDataModel stackData;

	protected WebStackService(BaseStackDataModel stackData,
			BaseRequestDataModel request) {
		this.stackData = stackData;
		this.request = request;
		this.apiHandler = ContextBuilder
				.newBuilder(request.getCloudServerRegion())
				.credentials(request.getUserName(), request.getApiKey())
				.buildApi(NovaApi.class);
		this.lbApiHandler = ContextBuilder
				.newBuilder(request.getCloudLoadBalancersRegion())
				.credentials(request.getUserName(), request.getApiKey())
				.buildApi(CloudLoadBalancersApi.class);
		this.dnsApiHandler = ContextBuilder
				.newBuilder(request.getCloudDnsRegion())
				.credentials(request.getUserName(), request.getApiKey())
				.buildApi(CloudDNSApi.class);
		this.cbsApi = ContextBuilder
				.newBuilder(request.getCloudBlockServiceRegion())
				.credentials(request.getUserName(), request.getApiKey())
				.buildApi(CinderApi.class);

	}

	public static WebStackService getInstance(BaseStackDataModel stackData,
			BaseRequestDataModel request) {
		if (instance == null) {
			instance = new WebStackService(stackData, request);
		}

		return instance;
	}

	/**
	 * Upload and create keypair entry
	 * @return Status of the action
	 */
	public int createKeyPair() {
		KeyPairApi keyPairApi = this.apiHandler.getKeyPairExtensionForZone(
				this.request.getZone()).get();

		File keyPairFile = new File(this.request.getPubKeyPath());
		String publicKey;
		try {
			publicKey = Files.toString(keyPairFile, StandardCharsets.UTF_8);
			keyPairApi.createWithPublicKey(this.request.getKeyPairName(),
					publicKey);
		} catch (IOException e1) {
			slf4jLogger
					.error("Error when read the key file:" + e1.getMessage());
			return ERROR_FILE_NOT_FOUND;
		} catch (Exception e) {
			slf4jLogger.error("Error when create keypair: " + e.getMessage());
			return ERROR_KEY_ALREADY_EXIST;
		}

		return SUCCESS;

	}

	/**
	 * Create instance(s) in a synchronized manner
	 * @return Status of the action
	 */
	public int createInstancesSync() {
		ServerApi serverApi = this.apiHandler.getServerApiForZone(this.request
				.getZone());
		CreateServerOptions options = CreateServerOptions.Builder
				.keyPairName(this.request.getKeyPairName());

		if (this.request.getNetworkID() != null) {
			Set<String> networks = new HashSet<String>();
			// adding public network first
			networks.add("00000000-0000-0000-0000-000000000000");
			networks.add(this.request.getNetworkID());
			options.networks(networks);
		}

		ArrayList<ServerCreated> createdList = new ArrayList<ServerCreated>();

		for (int i = 0; i < Integer.parseInt(this.request.getNumOfInstances()); i++) {
			ServerCreated serverCreated = serverApi.create(
					this.request.getStackName() + "_" + i,
					this.request.getImageID(), this.request.getFlavorID(),
					options);
			createdList.add(serverCreated);
			slf4jLogger.info("Creating instance " + serverCreated.getId());
		}
		for (ServerCreated sc : createdList) {
			int retry = 0;
			while (retry < RETRY_COUNT) {
				slf4jLogger.debug("Checking Server Creation for:" + sc.getId());
				if (ServerPredicates.awaitActive(serverApi).apply(sc.getId())) {
					slf4jLogger.info("Instance " + sc.getId()
							+ " created successfully");
					break;
				}
				if (retry >= RETRY_COUNT - 1) {
					slf4jLogger.error("Instance " + sc.getId()
							+ " creation failed");
					createdList.remove(sc);
				}
				retry++;
			}

		}

		this.stackData.setServerCreated(createdList);

		return createdList.size() == Integer.parseInt(this.request
				.getNumOfInstances()) ? SUCCESS
				: ERROR_INSTANCE_CREATION_FAILED;

	}
	/**
	 * Create and attach volume to each instances
	 * @return status of the action
	 */
	public int createBlockStorageVolume() {
		this.getAdditionalServerDetails();
		this.stackData.setVolumes(new ArrayList<Volume>());
		VolumeApi volumeApi = this.cbsApi.getVolumeApiForZone(this.request
				.getZone());
		for (Server s : this.stackData.getServers()) {

			CreateVolumeOptions options = CreateVolumeOptions.Builder.name(
					s.getName() + "_storage").volumeType("SATA");

			Volume volume = volumeApi.create(76, options);

			VolumeAttachmentApi volumeAttachmentApi = this.apiHandler
					.getVolumeAttachmentExtensionForZone(this.request.getZone())
					.get();

			VolumeAttachment volumeAttachment = volumeAttachmentApi
					.attachVolumeToServerAsDevice(volume.getId(), s.getId(),
							"/dev/sdb");

			slf4jLogger.info("Attaching volume: " + volume.getId()
					+ " to instance " + s.getId() + " at "
					+ volumeAttachment.getDevice());
			this.stackData.getVolumes().add(volume);

		}

		for (Volume v : this.stackData.getVolumes()) {
			// Wait for the volume to become Attached (In Use) before move
			// on
			int retry = 0;
			boolean attachSuccess = false;
			while (retry < RETRY_COUNT) {
				if (!VolumePredicates.awaitInUse(volumeApi).apply(v)) {
					retry++;
					slf4jLogger
							.error("Attaching status checking failed. Retrying");
					continue;
				}
				attachSuccess = true;
				break;
			}
			if (attachSuccess)
				slf4jLogger.info("Attach Complete for Volume " + v.getId());
			else
				slf4jLogger.info("Attach Failed!");
		}

		return SUCCESS;
	}

	/**
	 * Create Load Balancer and wait in a synchronized manner
	 * @return Status of the action
	 */
	public int createLBSync() {
		// TODO we create only 1 LB for now
		LoadBalancerApi lbApi = this.lbApiHandler
				.getLoadBalancerApiForZone(this.request.getZone());

		// server nodes
		this.getAdditionalServerDetails();
		Set<AddNode> addNodes = Sets.newHashSet();
		for (Server s : this.stackData.getServers()) {
			AddNode node = AddNode.builder().address(s.getAccessIPv4())
					.port(80).weight(20).condition(Condition.ENABLED).build();
			addNodes.add(node);
		}

		CreateLoadBalancer createLB = CreateLoadBalancer.builder()
				.name(this.request.getStackName() + "-LB").protocol("HTTP")
				.port(80).nodes(addNodes)
				.algorithm(BaseLoadBalancer.Algorithm.RANDOM)
				.virtualIPType(VirtualIP.Type.PUBLIC).build();

		LoadBalancer loadBalancer = lbApi.create(createLB);

		slf4jLogger.debug("Waiting for LB:" + loadBalancer.getId());

		if (!LoadBalancerPredicates.awaitAvailable(lbApi).apply(loadBalancer)) {
			return ERROR_LB_CREATION_FAILED;
		}

		this.stackData.getLoadBalancers().add(loadBalancer);
		return SUCCESS;
	}

	/**
	 * Create SSL Termination for Load Balancer
	 * @return Status of the action
	 */
	public int createSSLTermination() {
		if (this.stackData.getLoadBalancers().size() > 0) {
			// TODO we handle first lb for now
			LoadBalancer lb = this.stackData.getLoadBalancers().get(0);
			SSLTerminationApi api = this.lbApiHandler
					.getSSLTerminationApiForZoneAndLoadBalancer(
							this.request.getZone(), lb.getId());

			// generate keys
			SSLDataModel m = new SSLDataModel();
			SSLUtil.getSSLCert(this.request.getDomainName(), m);
			this.stackData.setSslData(m);

			SSLTermination terminalion = SSLTermination.builder()
					.securePort(443).secureTrafficOnly(false).enabled(true)
					.certificate(m.getCert()).privatekey(m.getPriavetKey())
					.build();

			api.createOrUpdate(terminalion);

			if (!this.awaitLB(lb))
				return ERROR_LB_CREATION_FAILED;

		}
		return SUCCESS;

	}

	/**
	 * Help function to wait for Load Balancer to be available
	 * @param lb  Load Balancer waiting for
	 * @return Status of the action
	 */
	protected boolean awaitLB(LoadBalancer lb) {
		slf4jLogger.info("Waiting for LB to get ready");
		LoadBalancerApi lbApi = this.lbApiHandler
				.getLoadBalancerApiForZone(this.request.getZone());
		boolean res = LoadBalancerPredicates.awaitStatus(lbApi,
				LoadBalancer.Status.ACTIVE, 600L, 10L).apply(lb);
		if (res)
			slf4jLogger.info("LB is ready");
		else
			slf4jLogger.info("LB is not ready");
		return res;

	}

	/**
	 * Get the Server object based on instance ID in ServerCreated object
	 */
	public void getAdditionalServerDetails() {
		if (this.stackData.getServers().size() == 0) {
			for (ServerCreated sc : this.stackData.getServerCreated()) {
				ServerApi serverApi = this.apiHandler
						.getServerApiForZone(this.request.getZone());
				Server s = serverApi.get(sc.getId());
				this.stackData.getServers().add(s);
			}
		}

	}

	/**
	 * Create Load Balancer Monitoring
	 * @return Status of the action
	 */
	public int createLBMonitoring() {

		LoadBalancer lb = this.stackData.getLoadBalancers().get(0);
		HealthMonitorApi healthMonitorApi = this.lbApiHandler
				.getHealthMonitorApiForZoneAndLoadBalancer(
						this.request.getZone(), lb.getId());

		HealthMonitor healthMonitor = HealthMonitor.builder()
				.type(HealthMonitor.Type.HTTP).delay(5).timeout(5).path("/")
				.statusRegex("^[23][0-9][0-9]$").attemptsBeforeDeactivation(5)
				.build();

		healthMonitorApi.createOrUpdate(healthMonitor);

		if (!this.awaitLB(lb))
			return ERROR_LB_CREATION_FAILED;

		return SUCCESS;

	}

	/**
	 * Upload custom error page for load balancer
	 * @return Status of the action
	 */
	public int createCustomLBErrorPage() {

		LoadBalancer lb = this.stackData.getLoadBalancers().get(0);

		ErrorPageApi errorPageApi = this.lbApiHandler
				.getErrorPageApiForZoneAndLoadBalancer(this.request.getZone(),
						lb.getId());
		String content = "";
		try {
			File f = new File(this.request.getPathToLBErrorPage());
			Scanner s = new Scanner(f);
			content = s.useDelimiter("\\Z").next();
			s.close();

		} catch (Exception e) {
			return ERROR_FILE_NOT_FOUND;
		}

		errorPageApi.create(content.replace("\n", "").replace("\r", ""));

		if (!this.awaitLB(lb))
			return ERROR_LB_CREATION_FAILED;

		return SUCCESS;
	}

	/**
	 * Create A/AAAA record in domain with the public address of load balancer 
	 * @return Status of the action
	 */
	public int attachDomainToLb() {
		LoadBalancer lb = this.stackData.getLoadBalancers().get(0);
		Set<VirtualIPWithId> vipSet = lb.getVirtualIPs();

		String lbPublicIp = null;
		for (VirtualIPWithId vip : vipSet) {
			if (vip.getType() == Type.PUBLIC) {
				lbPublicIp = vip.getAddress();
				break;
			}
		}

		slf4jLogger.debug("Public IP for LB:" + lb.getId() + " is: "
				+ lbPublicIp);

		int domainId = -1;
		if (lbPublicIp != null) {
			PagedIterable<Domain> domains = this.dnsApiHandler.getDomainApi()
					.list();

			for (IterableWithMarker<Domain> d : domains) {
				for (Domain dom : d) {
					slf4jLogger.debug("Comparing request doamin: "
							+ this.request.getDomainName() + " VS "
							+ dom.getName());
					if (dom.getName().equals(this.request.getDomainName())) {
						domainId = dom.getId();
						break;
					}
				}
			}

			slf4jLogger.debug("Selected Domain ID is:" + domainId);
			if (domainId < 0)
				return ERROR_DNS_DOMAIN_NOT_FOUND;

			RecordApi recordApi = this.dnsApiHandler
					.getRecordApiForDomain(domainId);

			// "AAAA" for IPv6 address
			String recType = lbPublicIp.contains(":") ? "AAAA" : "A";

			Record createARecord = Record.builder().type(recType)
					.name(this.request.getDomainName()).data(lbPublicIp)
					.ttl(3600).build();

			List<Record> createRecords = ImmutableList.of(createARecord);

			try {
				Set<RecordDetail> records = JobPredicates.awaitComplete(
						this.dnsApiHandler, recordApi.create(createRecords));
				for (RecordDetail r : records) {
					slf4jLogger.info("DNS Record Created:" + r.toString());
				}
			} catch (TimeoutException e) {
				slf4jLogger.error("DNS Record Creation Timeout:"
						+ e.getMessage());
				return ERROR_DNS_RECORD_CREATION_FAILED;
			}

		}

		return SUCCESS;

	}

	/**
	 * Create the domain record
	 * @return Status of the action
	 */
	public int createDomain() {
		DomainApi domainApi = this.dnsApiHandler.getDomainApi();

		List<CreateDomain> createDomains = Lists.newArrayList();
		//TODO hard code email address for now
		CreateDomain createDomain = CreateDomain.builder()
				.name(this.request.getDomainName()).email("admin@domain.com")
				.ttl(300).build();

		createDomains.add(createDomain);
		slf4jLogger.info("Creating Domain:" + this.request.getDomainName());
		try {
			Set<Domain> domains = JobPredicates.awaitComplete(
					this.dnsApiHandler, domainApi.create(createDomains));
			for (Domain r : domains) {
				slf4jLogger.info("Domain Created:" + r.toString());
			}
		} catch (TimeoutException e) {
			return ERROR_DOMAIN_CREATION_FAILED;
		}
		return SUCCESS;

	}

}
