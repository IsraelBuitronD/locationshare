package com.neoriddle.locationshare.security;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.util.Log;

public class GenericHttpsClient extends DefaultHttpClient {

	/**
     * Tag class for debugging.
     */
    private static final String DEBUG_TAG = "LocationShare";

    private final Context context;

    public GenericHttpsClient(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        Log.d(DEBUG_TAG, "Creating suported protocols scheme registry");
        final SchemeRegistry registry = new SchemeRegistry();

        Log.d(DEBUG_TAG, "Adding HTTP to scheme registry");
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        Log.d(DEBUG_TAG, "Adding HTTPS to scheme registry");
        registry.register(new Scheme("https", new NaiveSocketFactory(), 443));

        Log.d(DEBUG_TAG, "Setting HTTP protocol parameters");
        final HttpParams params = getParams();
        HttpProtocolParams.setUserAgent(params, "LocationShare");
        HttpProtocolParams.setContentCharset(params, "utf8");

        Log.d(DEBUG_TAG, "Creating connection manager");
        return new SingleClientConnManager(params, registry);
    }

}
