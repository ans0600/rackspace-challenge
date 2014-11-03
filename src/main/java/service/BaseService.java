package service;

public abstract class BaseService {

	public static final int SUCCESS = 0;
	public static final int ERROR_FILE_NOT_FOUND = -1;
	public static final int ERROR_KEY_ALREADY_EXIST = -2;
	public static final int ERROR_INSTANCE_CREATION_FAILED = -3;
	public static final int ERROR_LB_CREATION_FAILED = -4;
	public static final int ERROR_DNS_DOMAIN_NOT_FOUND = -4;
	public static final int ERROR_DNS_RECORD_CREATION_FAILED = -5;
	public static final int ERROR_DOMAIN_CREATION_FAILED = -6;
	public static final int RETRY_COUNT = 2;
}
