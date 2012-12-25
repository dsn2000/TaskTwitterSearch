package ru.task.ui;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 16.12.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import ru.task.R;


public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String CHECKBOX_DOWNLOAD_IMAGE_PREF = "checkboxDownloadImagePref";
    public static final String CHECKBOX_TWEETS_BROWSER_PREF = "checkboxTweetsBrowserPref";
    public static final String LIST_TWEETS_NUMBEROF_PREF = "listTweetsNumberOfPref";

    private Intent intent;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CHECKBOX_DOWNLOAD_IMAGE_PREF)) {
            intent.putExtra(CHECKBOX_DOWNLOAD_IMAGE_PREF, true);
        }
        else if (key.equals(CHECKBOX_TWEETS_BROWSER_PREF)) {
            intent.putExtra(CHECKBOX_TWEETS_BROWSER_PREF, true);
        }
        else if (key.equals(LIST_TWEETS_NUMBEROF_PREF)) {
            intent.putExtra(LIST_TWEETS_NUMBEROF_PREF, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        intent = new Intent();
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}