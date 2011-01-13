package com.neoriddle.localizationshare.io;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.neoriddle.localizationshare.R;

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
