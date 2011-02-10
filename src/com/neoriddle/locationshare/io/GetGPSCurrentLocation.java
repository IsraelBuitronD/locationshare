package com.neoriddle.locationshare.io;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.neoriddle.locationshare.R;
import com.neoriddle.locationshare.security.GenericHttpsClient;
import com.neoriddle.locationshare.utils.AndroidUtils;

public class GetGPSCurrentLocation extends MapActivity {

    /**
     * Tag class por debugging.
     */
    @SuppressWarnings("unused")
    private static final String DEBUG_TAG = "GetGPSCurrentLocation";

    private static final int LAST_LOCATION_DETAIL_DIALOG_ID = 0x01;
    private static final int ABOUT_DIALOG_ID = 0x02;

    private MapView mapView;
    private MyLocationOverlay overlay;
    private SharedPreferences activityPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_gps_current_location);
        activityPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mapView = (MapView) findViewById(R.id.mapView);
        overlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(overlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overlay.enableMyLocation();

        // Read preferences for compass
        if(activityPreferences.getBoolean("compass_control", false))
            overlay.enableCompass();
        else
            overlay.disableCompass();

        // Read preferences for satellite, traffic and streetview overlay
        mapView.setSatellite(activityPreferences.getBoolean("satellite_overlay", false));
        mapView.setTraffic(activityPreferences.getBoolean("traffic_overlay", false));
        mapView.setStreetView(activityPreferences.getBoolean("streetview_overlay", false));

        // Read preferences for map view controls
        mapView.setBuiltInZoomControls(activityPreferences.getBoolean("builtin_zoom_controls", false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(overlay.isMyLocationEnabled())
            overlay.disableMyLocation();
        if(overlay.isCompassEnabled())
            overlay.disableCompass();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.get_gps_current_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refreshMenu:
            Toast.makeText(this, "TODO: Call refresh", Toast.LENGTH_SHORT).show();
            // TODO Implement refreshing location
            return true;
        case R.id.lastLocationDetailMenu:
            if (overlay.getLastFix() == null)
                Toast.makeText(this, R.string.last_location_info_not_available, Toast.LENGTH_SHORT).show();
            else
                showDialog(LAST_LOCATION_DETAIL_DIALOG_ID);
            return true;
        case R.id.sendBySmsMenu:
            sendBySms();
            return true;
        case R.id.sendByEmailMenu:
            sendByEmail();
            return true;
        case R.id.sendByHttpsMenu:
            try {
                sendByHttps();
            } catch(final Exception e) {
                // FIXME Fix this try-catch
                Log.e(DEBUG_TAG, e.getMessage(), e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        case R.id.preferencesMenu:
            startActivity(new Intent(this, Preferences.class));
            return true;
        case R.id.aboutMenu:
            showDialog(ABOUT_DIALOG_ID);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final LayoutInflater factory = LayoutInflater.from(this);

        switch (id) {
        case LAST_LOCATION_DETAIL_DIALOG_ID:
            final View detailView = factory.inflate(R.layout.show_last_location_detail, null);

            return new AlertDialog.Builder(this).
                setTitle(R.string.last_location_detail).
                setView(detailView).
                setPositiveButton(R.string.close, null).
                create();

        case ABOUT_DIALOG_ID :
            final View aboutView = factory.inflate(R.layout.about_dialog, null);

            final TextView versionLabel = (TextView)aboutView.findViewById(R.id.version_label);
            versionLabel.setText(getString(R.string.version_msg, AndroidUtils.getAppVersionName(getApplicationContext())));

            return new AlertDialog.Builder(this).
                setIcon(R.drawable.icon).
                setTitle(R.string.app_name).
                setView(aboutView).
                setPositiveButton(R.string.close, null).
                create();

        default:
            return super.onCreateDialog(id);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case LAST_LOCATION_DETAIL_DIALOG_ID:
            final Location lastLocation = overlay.getLastFix();

            final TextView latitudeText = (TextView)dialog.findViewById(R.id.latitude_value);
            latitudeText.setText(Double.toString(lastLocation.getLatitude()));

            final TextView longitudeText = (TextView)dialog.findViewById(R.id.longitude_value);
            longitudeText.setText(Double.toString(lastLocation.getLongitude()));

            final TextView bearingText = (TextView) dialog.findViewById(R.id.bearing_value);
            bearingText.setText(Float.toString(lastLocation.getBearing()));

            final TextView altitudeText = (TextView) dialog.findViewById(R.id.altitude_value);
            altitudeText.setText(Double.toString(lastLocation.getAltitude()));

            final TextView speedText = (TextView) dialog.findViewById(R.id.speed_value);
            speedText.setText(getString(R.string.speed_value, lastLocation.getSpeed()));

            final TextView timeText = (TextView) dialog.findViewById(R.id.time_value);
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            final Date date = new Date(lastLocation.getTime());
            timeText.setText(df.format(date));

            final TextView providerText = (TextView) dialog.findViewById(R.id.provider_value);
            providerText.setText(lastLocation.getProvider());

            final TextView accuracyText = (TextView) dialog.findViewById(R.id.accuracy_value);
            accuracyText.setText(getString(R.string.accuracy_value, lastLocation.getAccuracy()));

            break;

        default:
            super.onPrepareDialog(id, dialog);
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    protected void sendBySms() {
        final Location lastLocation = overlay.getLastFix();

        if (lastLocation == null)
            Toast.makeText(this, R.string.last_location_info_not_available, Toast.LENGTH_SHORT).show();
        else {
            final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);

            final Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", prepateEmailMessage(preferences, lastLocation));
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        }
    }

    protected void sendByHttps()
        throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException {
        // TODO Send location by HTTPS

        Log.d(DEBUG_TAG, "Creating HTTPS client");
        final DefaultHttpClient client = new GenericHttpsClient(getApplicationContext());

        final HttpPost httppost = new HttpPost("https://myserver/locationshare.php");
        //final HttpPost httppost = new HttpPost("https://201.153.203.204/locationshare.php");
        //final HttpPost httppost = new HttpPost("https://172.16.214.97/locationshare.php");

        Log.d(DEBUG_TAG, "Adding POST parameters");
        final List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("foo", "bar"));
        httppost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

        Log.d(DEBUG_TAG, "Executing POST request");
        final String response = client.execute(httppost, new BasicResponseHandler());

        Log.d(DEBUG_TAG, "POST response\n" + response);

    }

    protected void sendByEmail() {
        final Location lastLocation = overlay.getLastFix();
        if (lastLocation == null)
            Toast.makeText(this, R.string.last_location_info_not_available, Toast.LENGTH_SHORT).show();
        else {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            final String[] recipients = {
                "locationshare4a@gmail.com"
            };

            final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
            final String subject = preferences.getString("default_subject_for_email_alert",
                    getString(R.string.default_subject_for_email_alert));

            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, prepateEmailMessage(preferences, lastLocation));

            startActivity(emailIntent);
        }
    }

    protected String prepateEmailMessage(SharedPreferences preferences, Location lastLocation) {
        return preferences.getString(
                "default_template_for_email_alert",
                getString(R.string.default_template_for_email_alert,
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude(),
                        lastLocation.getAccuracy(),
                        lastLocation.getSpeed(),
                        new SimpleDateFormat("yyyyMMdd_HHmmss_ZZZZ").format(new Date(lastLocation.getTime()))));
    }

}
