package ru.semenovmy.learning.reminder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Класс для добавления фрагмента прав
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }
}
