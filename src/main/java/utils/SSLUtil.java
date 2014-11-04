package utils;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import model.SSLDataModel;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class SSLUtil {

	public static void getSSLCert(String domain,SSLDataModel m)
	{
		try{
			 Security.addProvider(new BouncyCastleProvider());
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair KPair = keyPairGenerator.generateKeyPair();
			
			X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
			
			v3CertGen.setSerialNumber(BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())));
	        v3CertGen.setIssuerDN(new X509Principal("CN=" + domain + ", OU=None, O=None L=None, C=None"));
	        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
	        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)));
	        v3CertGen.setSubjectDN(new X509Principal("CN=" + domain + ", OU=None, O=None L=None, C=None"));
	        v3CertGen.setPublicKey(KPair.getPublic());
	        v3CertGen.setSignatureAlgorithm("MD5WithRSAEncryption"); 
	        
	        X509Certificate PKCertificate = v3CertGen.generateX509Certificate(KPair.getPrivate());
	        
	        StringWriter sw = new StringWriter();
	        PEMWriter pw = new PEMWriter(sw);
	        pw.writeObject(PKCertificate);
	        pw.close();
	        
	        m.setCert(sw.toString());
	        
	        sw = new StringWriter();
	        pw = new PEMWriter(sw);
	        pw.writeObject(KPair.getPrivate());
	        pw.close();
	        m.setPriavetKey(sw.toString());
	        
	    //    PrintWriter out = new PrintWriter("/home/frank/testCert.pem");
	    //    out.print(sw.toString());
	    //    out.close();
	        
	     //   FileOutputStream fos = new FileOutputStream("/home/frank/testCert.cert");
	       // fos.write(PKCertificate.getEncoded());
	       // fos.(sw.);
	      //  fos.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
}
