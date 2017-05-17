package com.hong.network;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * http://www.nickoh.com/emacs_files/ssl-examples/ExampleSSLClient.java.txt
 * http://stackoverflow.com/questions/18787419/ssl-socket-connection
 * @author HYao
 *
 */

public class SSLHandshake {

	public static Object SYNC = new Object();
	final public static String DEFAULT_MSG_ERR = "Error";
	final public static String DEFAULT_MSG_INFO = "Info";
	final public static String DEFAULT_USAGE = "java "
			+ SSLHandshake.class.getName()
			+ " <Hostaddress, default www.google.com> <Portnumber, default 443> <Protocal, default SSLv3> "
			+ " -Djavax.net.ssl.keyStore=<file> -Djavax.net.ssl.keyStorePassword=<password> "
			+ " -Djavax.net.ssl.trustStore=<file> ";
			
	public static void main(String args[]) {
		if (args.length < 3) {
			err("Please input correct parameters, e.g.");
			err(DEFAULT_USAGE);
			err("Default parameters would be applied.");
			new SSLConnector().initUsingTrustManager().doHandshake();
		}
		else if (args.length == 3 ) {
			new SSLConnector(args[2], args[0], args[1]).initUsingTrustManager().doHandshake();
		}
		else if (args.length > 3 ) {
			new SSLConnector(args[2], args[0], args[1]).initUsingTrustStore().doHandshake();
		}
	}

	public static void info(String pMsg) {
		synchronized (SYNC) {
			System.out.println("[Info] " + pMsg);
		}
	}

	public static void err(String pMsg) {
		synchronized (SYNC) {
			System.err.println("[Error] " + pMsg);
		}
	}
}

class SSLConnector {

	public static Object SYNC = new Object();

	final private static String DEFAULT_PROTOCAL_NAME = "TLSv1.2";
	final private static String DEFAULT_SERVER_HOST_NAME = "www.google.com";
	final private static int DEFAULT_SERVER_PORT_NUMBER = 443;

	private String mProtocalName = "";
	private String mServerHostName = "";
	private int mServerPortNumber = 443;

	private SSLContext mSSLContext = null;
	private SSLSocketFactory mSSLSocketFactory = null;
	private SSLSocket mSSLSocket = null;
	private TrustManager[] mTrustManagers = null;

	public SSLConnector() {
		this(DEFAULT_PROTOCAL_NAME, DEFAULT_SERVER_HOST_NAME, DEFAULT_SERVER_PORT_NUMBER);
	}

	public SSLConnector(String pHostName, int pPortNumber) {
		this(DEFAULT_PROTOCAL_NAME, pHostName, pPortNumber);
	}

	public SSLConnector(String pHostName, String pPortNumber) {
		this(DEFAULT_PROTOCAL_NAME, pHostName, Integer.valueOf(pPortNumber.trim()));
	}

	public SSLConnector(String pProtocalName, String pHostName, String pPortNumber) {
		this(pProtocalName, pHostName, Integer.valueOf(pPortNumber.trim()));
	}

	public SSLConnector(String pProtocalName, String pHostName, int pPortNumber) {
		setmProtocalName(pProtocalName);
		setmServerHostName(pHostName);
		setmServerPortNumber(pPortNumber);

		System.setProperty("https.protocols", getmProtocalName());
		
		info("Protocal : " + getmProtocalName());
		info("Server address : " + getmServerHostName());
		info("Port number : " + getmServerPortNumber());

	}
	
	public SSLConnector initUsingTrustManager() {
		synchronized (SYNC) {
			info("Initializing (using TrustManager) ...");
			mTrustManagers = new TrustManager[] { new CustomX509TrustManager() };
			try {
				mSSLContext = SSLContext.getInstance(getmProtocalName());
				mSSLContext.init(null, mTrustManagers, new java.security.SecureRandom());				
			} catch (NoSuchAlgorithmException e) {
				err(e.getMessage());
			} catch (KeyManagementException e) {
				err(e.getMessage());
			}
			mSSLSocketFactory = mSSLContext.getSocketFactory();
			return this;
		}
	}
	
	public SSLConnector initUsingTrustStore() {
		synchronized (SYNC) {
			info("Initializing (using TrustStore) ...");
			String trustStoreName = System.getProperty("javax.net.ssl.trustStore");
			if (trustStoreName == null) {
				// Tell them how to set it
				err("You have not specified a truststore, which means that JSSE will look in default locations (see JSSE Reference Guide) to validate any certificates offered by the server.  To specify a truststore, use e.g. :");
				err("java -Djavax.net.ssl.trustStore=\"<file>\" ...");
			}
			mSSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			return this;
		}
	}

	public void doHandshake() {
		synchronized (SYNC) {
			try {
				info("Socket connecting...");
				mSSLSocket = (SSLSocket) mSSLSocketFactory.createSocket(getmServerHostName(), getmServerPortNumber());
				String[] tProtocols = { getmProtocalName() };
				mSSLSocket.setEnabledProtocols(tProtocols);
			} catch (IOException e) {
				err(e.getMessage());
			}

			if (mSSLSocket == null) {
				err("Socket has not been initiallized.");
			} else {
				if (mSSLSocket.isClosed()) {
					err("Socket has been closed.");
				}

				mSSLSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
					@Override
					public void handshakeCompleted(HandshakeCompletedEvent event) {
						info("Socket negotiated cipher suite : " + event.getCipherSuite());
						Certificate[] tLocalCertificates = event.getLocalCertificates();
						if (tLocalCertificates != null) {
							info(tLocalCertificates.length + " local certificates used :");
							for (Certificate tLocalCertificate : tLocalCertificates) {
								info("  - " + tLocalCertificate.getType() + " " + tLocalCertificate.getPublicKey());
							}
						} else {
							info("No local certificates used");
						}
						try {
							Certificate[] tPeerCertificates = event.getPeerCertificates();
							if (tPeerCertificates != null) {
								info(tPeerCertificates.length + " peer certificates exists :");
								for (Certificate tPeerCertificate : tPeerCertificates) {
									info("  - " + tPeerCertificate.getType() + " " + tPeerCertificate.getPublicKey());
								}
							}
						} catch (SSLPeerUnverifiedException e) {
							err("Peer's identity has not been verified");
						}
						info("Socket handshake successful!");
					}

				});

				try {
					mSSLSocket.startHandshake();
					if (mSSLSocket.isConnected()) {

						info("Socket connected!");
						info("Socket Session created at : " + new Date(mSSLSocket.getSession().getCreationTime()));

						String[] tSupportedProtocols = mSSLSocket.getSupportedProtocols();
						info("Following Protocols would be supported : ");
						for (String tSupportedProtocol : tSupportedProtocols) {
							info(" - " + tSupportedProtocol);
						}
						String[] tSupportedSuites = mSSLSocket.getSupportedCipherSuites();
						info("Following cipher suites would be supported : ");
						for (String tSupportedSuite : tSupportedSuites) {
							info(" - " + tSupportedSuite);
						}

						if (mSSLSocket.getSession().getPeerHost() != null) {
							info("Socket Peer : " + mSSLSocket.getSession().getProtocol() + " "
									+ mSSLSocket.getRemoteSocketAddress() + " / "
									+ mSSLSocket.getSession().getPeerPort());
						}

					} else {
						err("Socket connection failed!");
					}
				} catch (IOException e) {
					err("Socket connection has error!");
					err(e.getMessage());
				}

				info("Socket closing...");
				try {
					mSSLSocket.close();
				} catch (IOException e) {
					err(e.getMessage());
				}
				if (mSSLSocket.isClosed()) {
					info("Socket closed!");
				} else {
					err("Socket has not been successful closed!");
				}
			}
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

	public String getmProtocalName() {
		return mProtocalName;
	}

	public void setmProtocalName(String mProtocalName) {
		if (mProtocalName == null) {
			this.mProtocalName = DEFAULT_PROTOCAL_NAME;
		}
		this.mProtocalName = mProtocalName;
	}

	public String getmServerHostName() {
		return mServerHostName;
	}

	public void setmServerHostName(String mServerHostName) {
		if (mServerHostName == null) {
			this.mServerHostName = DEFAULT_SERVER_HOST_NAME;
		}
		this.mServerHostName = mServerHostName;
	}

	public int getmServerPortNumber() {
		return mServerPortNumber;
	}

	public void setmServerPortNumber(int mServerPortNumber) {
		if (Integer.valueOf(mServerPortNumber) == null) {
			this.mServerPortNumber = DEFAULT_SERVER_PORT_NUMBER;
		}
		this.mServerPortNumber = mServerPortNumber;
	}

	public static void info(String pMsg) {
		synchronized (SYNC) {
			System.out.println("[Info] " + pMsg);
		}
	}

	public static void err(String pMsg) {
		synchronized (SYNC) {
			System.err.println("[Error] " + pMsg);
		}
	}

}