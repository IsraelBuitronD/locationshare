package com.neoriddle.locationshare.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class NaiveTrustManager implements X509TrustManager {

	/**
     * Tag class for debugging.
     */
    private static final String DEBUG_TAG = "LocationShare";

    private String certKey;
    private static final X509Certificate[] a = new X509Certificate[] {};

    public NaiveTrustManager() {
        super();
    }

    public NaiveTrustManager(String certKey) {
        super();
        this.certKey = certKey;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Do nothing
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Do nothing
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
