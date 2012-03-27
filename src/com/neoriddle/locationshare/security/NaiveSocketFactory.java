package com.neoriddle.locationshare.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class NaiveSocketFactory
    implements SocketFactory, LayeredSocketFactory {

	/**
     * Tag class for debugging.
     */
    private static final String DEBUG_TAG = "LocationShare";

    private String certKey;
    private SSLContext sslContext;

    public NaiveSocketFactory() {
        // Do nothing
    }

    public NaiveSocketFactory(String certKey) {
        this.certKey = certKey;
    }

    private static SSLContext createNaiveSSLContext(String certKey)
        throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { new NaiveTrustManager(certKey) }, null);
        return context;
    }

    private SSLContext getSSLContext()
        throws NoSuchAlgorithmException, KeyManagementException {
        return sslContext == null ? sslContext = createNaiveSSLContext(certKey) : sslContext;
    }

    public Socket connectSocket(Socket socket, String host, int port,
            InetAddress localAddress, int localPort, HttpParams params)
        throws IOException, UnknownHostException,
            ConnectTimeoutException {

        final SSLSocket sslsock = (SSLSocket)(socket != null ? socket : createSocket());

        if(localAddress != null || localPort > 0) {
            // We need to bind explicitly (zero means "any").
            localPort = localPort<0 ? 0 : localPort;
            sslsock.bind(new InetSocketAddress(localAddress, localPort));
        }

        sslsock.connect(
                new InetSocketAddress(host, port),
                HttpConnectionParams.getConnectionTimeout(params));
        sslsock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        return sslsock;

    }

    public Socket createSocket() throws IOException {
        try {
            return getSSLContext().getSocketFactory().createSocket();
        } catch(final GeneralSecurityException e) {
            Log.e(DEBUG_TAG, e.getMessage(), e);
            return null;
        }
    }

    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        // Its naive because everything is "secure".
        return true;
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
        throws UnknownHostException, IOException {
        try {
            return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        } catch(final GeneralSecurityException e) {
            Log.e(DEBUG_TAG, e.getMessage(), e);
            return null;
        }
    }

}
