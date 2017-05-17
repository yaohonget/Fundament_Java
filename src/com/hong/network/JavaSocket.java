package com.hong.network;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.hong.basic.Logger;

public class JavaSocket {
	
    final private static String DEFAULT_PROTOCAL_NAME = "TLSv1.2";
    final private static String DEFAULT_URL_PARAMETER_CONNECTOR = "&";
    final private static String DEFAULT_URL_PARAMETER_EQUALMARK = "=";
    
    // variables related with HTTPS Connection
    public static Object SYNC = new Object();
    private static SSLContext mSSLContext = null;
    private static TrustManager[] mTrustManagers = null;
    
    public static void main(String [] args) {
    	JavaSocket tSocketConnection = new JavaSocket();
    	tSocketConnection.doRequest("https://10.0.0.97/is-bin/INTERSHOP.enfinity/WFS/eCat-LnHGroup-Site/en_AU/-/AUD/ViewWestpacPayment-GetWestpacNotification", null);
    }
    
    private void getRequestResult(ByteArrayOutputStream pByteArrayOutputStream) {
    	
    	synchronized (SYNC) {
    		if(pByteArrayOutputStream == null || pByteArrayOutputStream.size() == 0) {
    			Logger.err(this, "Response parameter is empty.");
    			return;
    		}
    		
	    	String [] tResponseParameters = new String(pByteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8).split(DEFAULT_URL_PARAMETER_CONNECTOR);
	        for (int i = 0; i < tResponseParameters.length; i++)
	        {
	            String tResponseParameter = tResponseParameters[i];
	            String[] tParameter = tResponseParameter.split(DEFAULT_URL_PARAMETER_EQUALMARK, 2);
	            if(tParameter.length < 2) {
	            	Logger.err(this, "Response parameter error : " + tParameter.toString());
	            }
	            else {
	            	Logger.info(this, "Get response parameter :  [" + tParameter[0].toString() + "] - " + tParameter[1].toString());
	            }
	        }
    	}
    }
    
    public void doRequest(String pUrl, Map<String, String> pRequestParameters) {
    	getRequestResult(request(pUrl, pRequestParameters));
    }
	
    private ByteArrayOutputStream request(String pUrl, Map<String, String> pRequestParameters) {    	
    	
    	synchronized (SYNC) {
    		
    		ByteArrayOutputStream tOutputstream = null;
    		
    		if(pUrl == null || pUrl.isEmpty()) {
    			Logger.err(this, "URL is invalid.");
    			return tOutputstream;
    		}
    		Logger.info(this, "Request url is " + pUrl);
    		
    		// create url parameters for token request
            StringBuilder tRequestParameters = new StringBuilder();
            
            if(pRequestParameters == null || pRequestParameters.isEmpty()) {
            	Logger.info(this, "Request parameter is empty.");
            }
            else {
            	Set<String> tRequestParametersKeySet = pRequestParameters.keySet();
                Iterator<String> tRequestParametersKeyIterator = tRequestParametersKeySet.iterator();
                while(tRequestParametersKeyIterator.hasNext())
                {   
                    String tKey = tRequestParametersKeyIterator.next();
                    String tVal = pRequestParameters.get(tKey);
                    if(tVal != null && !tVal.isEmpty()) 
                        tRequestParameters.append(DEFAULT_URL_PARAMETER_CONNECTOR + tKey + DEFAULT_URL_PARAMETER_EQUALMARK + tVal); 
                }
                Logger.info(this, "Request parameter is " + tRequestParameters.toString());
            }
    		
    		HttpsURLConnection tConnection = null;
            OutputStream tOutputStream = null;
            Logger.info(this, "Connection start.");
            try
            {            	
                tConnection = (HttpsURLConnection)new URL(pUrl).openConnection();
                tConnection.setSSLSocketFactory(getSSLSocketFactoryUsingTrustManager());
                tConnection.setHostnameVerifier(getHostnameVerifier());
                tConnection.setDoInput(true);
                tConnection.setDoOutput(true);
                tOutputStream = tConnection.getOutputStream();
                tOutputStream.write(tRequestParameters.toString().getBytes());
                tOutputStream.close();
                InputStream tInputStream = tConnection.getInputStream();
                tOutputstream = new ByteArrayOutputStream();
                int tBytesRead = 0;
                byte[] tReadBuffer = new byte[1024];
                while((tBytesRead = tInputStream.read(tReadBuffer)) >= 0)
                {
                    if (tBytesRead > 0)
                    {
                        tOutputstream.write(tReadBuffer, 0, tBytesRead);
                    }
                }
                
            }
            catch(Exception e)
            {
            	tOutputstream = null;
                Logger.err(this, e.getMessage() + " JAVA VERSION : " + System.getProperty("java.version"));
            }
            Logger.info(this, "Connection finished.");
            return tOutputstream;
            
    	}    	
    }
    
	
	// establish SSL Context
    private SSLSocketFactory getSSLSocketFactoryUsingTrustManager() {
        synchronized (SYNC) {
            mTrustManagers = new TrustManager[] { new CustomX509TrustManager() };
            try {
//            	// set default SSL protocal
//            	mSSLContext = SSLContext.getInstance(DEFAULT_PROTOCAL_NAME);
//            	mSSLContext.init(null, null, null);
//            	SSLContext.setDefault(mSSLContext); 
                
                mSSLContext = SSLContext.getInstance(DEFAULT_PROTOCAL_NAME);
                mSSLContext.init(null, mTrustManagers, new java.security.SecureRandom());
                SSLContext.setDefault(mSSLContext);
                
            } catch (NoSuchAlgorithmException e){
            	Logger.err(this, e.getMessage());
            } catch (KeyManagementException e) {
                Logger.err(this, e.getMessage());
            }
            return mSSLContext.getSocketFactory();
        }
    }
    
    private HostnameVerifier getHostnameVerifier() {
        synchronized (SYNC) {
        	return new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }
    }
    
    private class CustomX509TrustManager implements X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }
    }
	
}
