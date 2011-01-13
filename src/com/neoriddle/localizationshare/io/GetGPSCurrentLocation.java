package com.neoriddle.localizationshare.io;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.neoriddle.localizationshare.R;
import com.neoriddle.localizationshare.utils.AndroidUtils;

public class GetGPSCurrentLocation extends MapActivity {

    private static final int LAST_LOCATION_DETAIL_DIALOG_ID = 0x01;
    private static final int ABOUT_DIALOG_ID = 0x02;

    private MapView mapView;
    private MyLocationOverlay overlay;

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
            // TODO Implement refresshing location
            return true;
        case R.id.lastLocationDetailMenu:
            if (overlay.getLastFix() == null)
                Toast.makeText(this, R.string.last_location_info_not_available, Toast.LENGTH_SHORT).show();
            else
                showDialog(LAST_LOCATION_DETAIL_DIALOG_ID);
            return true;
        case R.id.sendBySmsMenu:
            Toast.makeText(this, "TODO: Send location by SMS", Toast.LENGTH_SHORT).show();
            // TODO Send location by SMS
            return true;
        case R.id.sendByEmailMenu:
            final Location lastLocation = overlay.getLastFix();
            if (lastLocation == null)
                Toast.makeText(this, R.string.last_location_info_not_available,
                        Toast.LENGTH_SHORT).show();
            else {
                final Intent emailIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                final String[] recipients = {
                    "neoriddle@gmail.com"
                };

                final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                final String subject = preferences.getString("default_subject_for_email_alert",
                        getString(R.string.default_subject_for_email_alert));
                final String message = preferences.getString(
                        "default_template_for_email_alert",
                        getString(R.string.default_template_for_email_alert,
                                lastLocation.getLatitude(),
                                lastLocation.getLongitude(),
                                lastLocation.getAccuracy(),
                                lastLocation.getSpeed(),
                                new SimpleDateFormat("yyyyMMdd_HHmmss_ZZZZ").format(new Date(lastLocation.getTime()))));

                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(emailIntent);
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
        Dialog dialog;
        switch (id) {
        case LAST_LOCATION_DETAIL_DIALOG_ID:
            final Location lastLocation = overlay.getLastFix();

            dialog = new Dialog(this);
            dialog.setContentView(R.layout.show_last_location_detail);
            dialog.setTitle(R.string.last_location_detail);
            dialog.setOwnerActivity(this);

            final TextView latitudeText = (TextView) dialog.findViewById(R.id.latitude_value);
            latitudeText.setText(Double.toString(lastLocation.getLatitude()));

            final TextView longitudeText = (TextView) dialog.findViewById(R.id.longitude_value);
            longitudeText.setText(Double.toString(lastLocation.getLongitude()));

            final TextView bearingText = (TextView) dialog.findViewById(R.id.bearing_value);
            bearingText.setText(Float.toString(lastLocation.getBearing()));

            final TextView altitudeText = (TextView) dialog.findViewById(R.id.altitude_value);
            altitudeText.setText(Double.toString(lastLocation.getAltitude()));

            final TextView speedText = (TextView) dialog.findViewById(R.id.speed_value);
            speedText.setText(getString(R.string.speed_value, lastLocation.getSpeed()));

            final TextView timeText = (TextView) dialog.findViewById(R.id.time_value);
            timeText.setText(new Date(lastLocation.getTime()).toGMTString());

            final TextView providerText = (TextView) dialog.findViewById(R.id.provider_value);
            providerText.setText(lastLocation.getProvider());

            final TextView accuracyText = (TextView) dialog.findViewById(R.id.accuracy_value);
            accuracyText.setText(getString(R.string.accuracy_value, lastLocation.getAccuracy()));

            return dialog;
        case ABOUT_DIALOG_ID :
            final LayoutInflater factory = LayoutInflater.from(this);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_gps_current_location);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        overlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(overlay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overlay.disableMyLocation();
        overlay.disableCompass();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overlay.enableMyLocation();
        overlay.enableCompass();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

}
