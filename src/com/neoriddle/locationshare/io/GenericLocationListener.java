package com.neoriddle.locationshare.io;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.neoriddle.locationshare.db.LocationsDBAdapter;

public class GenericLocationListener implements LocationListener {

    private static final String DEBUG_TAG = "GenericLocationListener";
    private final Context context;
    private Location bestFix;
    private Location lastFix;
    private long olderTimeDelta = 1000 * 60 * 2; // 2 minutes

    public long getOlderTimeDelta() {
        return olderTimeDelta;
    }

    public void setOlderTimeDelta(long olderTimeDelta) {
        this.olderTimeDelta = olderTimeDelta;
    }

    public GenericLocationListener(Context context) {
        this.context = context;
    }

    public void onLocationChanged(Location location) {
        Toast.makeText(context, "New location found", Toast.LENGTH_SHORT).show();

        Log.d(DEBUG_TAG, "Creating db adapter");
        final LocationsDBAdapter adapter = new LocationsDBAdapter(context);
        Log.d(DEBUG_TAG, "Opening connection");
        adapter.open();
        Log.d(DEBUG_TAG, "Inserting location data");
        adapter.insertLocation(location);
        Log.d(DEBUG_TAG, "Closing connection");
        adapter.close();

        lastFix = location;

        if(isBetterLocation(location, bestFix)) {
            Log.i(DEBUG_TAG, "New best location found");
            Toast.makeText(context, "New best location found", Toast.LENGTH_SHORT).show();
            bestFix = location;
        }
    }

    public Location getBestFix() {
        return bestFix;
    }

    public Location getLastFix() {
        return lastFix;
    }

    public void onProviderDisabled(String provider) {
        // this is called if/when the GPS is disabled in settings
        final String msg = "Provider: " + provider + " was disabled.";
        Log.d(DEBUG_TAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void onProviderEnabled(String provider) {
        final String msg = "Provider: " + provider + " was enabled.";
        Log.d(DEBUG_TAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // This is called when the GPS status alters
        switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
            Log.d(DEBUG_TAG, "Status Changed: Out of Service");
            Toast.makeText(context, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.d(DEBUG_TAG, "Status Changed: Temporarily Unavailable");
            Toast.makeText(context, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.AVAILABLE:
            Log.d(DEBUG_TAG, "Status Changed: Available");
            Toast.makeText(context, "Status Changed: Available", Toast.LENGTH_SHORT).show();
            break;
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null)
           // A new location is always better than no location
           return true;

        // Check whether the new location fix is newer or older
        final long timeDelta = location.getTime() - currentBestLocation.getTime();
        final boolean isSignificantlyNewer = timeDelta > olderTimeDelta;
        final boolean isSignificantlyOlder = timeDelta < -olderTimeDelta;
        final boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if(isSignificantlyNewer)
            return true;
        // If the new location is more than two minutes older, it must be worse
        else if(isSignificantlyOlder)
            return false;

        // Check whether the new location fix is more or less accurate
        final int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        final boolean isLessAccurate = accuracyDelta > 0;
        final boolean isMoreAccurate = accuracyDelta < 0;
        final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        final boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate)
            return true;
        else if (isNewer && !isLessAccurate)
            return true;
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
            return true;
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        return provider1 == null ? provider2 == null : provider1.equals(provider2);
    }

}
