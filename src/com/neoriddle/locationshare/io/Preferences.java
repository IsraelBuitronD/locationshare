package com.neoriddle.locationshare.io;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.neoriddle.locationshare.R;

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

}
