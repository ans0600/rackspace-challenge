package service;

import java.io.File;
import java.net.URI;

import model.BaseStackDataModel;

import org.jclouds.ContextBuilder;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.jclouds.rackspace.cloudfiles.v1.features.CDNApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import request.BaseRequestDataModel;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class CloudFileService extends BaseService {

	private final Logger slf4jLogger = LoggerFactory
			.getLogger(CloudFileService.class);

	private static CloudFileService instance = null;

	private CloudFilesApi cloudFilesApiHandler;

	private BaseRequestDataModel request;

	private BaseStackDataModel stackData;

	protected CloudFileService(BaseStackDataModel stackData,
			BaseRequestDataModel request) {
		this.stackData = stackData;
		this.request = request;
		this.cloudFilesApiHandler = ContextBuilder
				.newBuilder(request.getCloudFileRegion())
				.credentials(request.getUserName(), request.getApiKey())
				.buildApi(CloudFilesApi.class);
	}

	public static CloudFileService getInstance(BaseStackDataModel stackData,
			BaseRequestDataModel request) {
		if (instance == null) {
			instance = new CloudFileService(stackData, request);
		}

		return instance;
	}

	public int enableCDN()
	{
		CDNApi cdnApi = this.cloudFilesApiHandler.getCDNApiForRegion(this.request.getZone());
		URI cdnUri = cdnApi.enable(this.request.getContainerName());
		this.stackData.setCdnUri(cdnUri.toString());
		return SUCCESS;
	}
	
	public int upLoadFile(String filePath) {
		ObjectApi objectApi = this.cloudFilesApiHandler
				.getObjectApiForRegionAndContainer(this.request.getZone(),
						this.request.getContainerName());

		File f = new File(filePath);
		ByteSource byteSource = Files.asByteSource(f);
		Payload filePayload = Payloads.newByteSourcePayload(byteSource);
		objectApi.put(f.getName(), filePayload);

		return SUCCESS;
	}

}
