package ru.semenovmy.learning.reminder;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Кдасс для добавления прав
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
