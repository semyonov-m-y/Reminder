package ru.semenovmy.learning.reminder;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.semenovmy.learning.reminder.adapter.SpinnerAdapter;
import ru.semenovmy.learning.reminder.data.database.Reminder;
import ru.semenovmy.learning.reminder.data.database.ReminderDatabase;
import ru.semenovmy.learning.reminder.data.model.ReminderItem;
import ru.semenovmy.learning.reminder.receiver.BootReceiver;
import ru.semenovmy.learning.reminder.receiver.NotificationReceiver;

/**
 * Main class for recycler view
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class MainRecyclerViewActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        SearchView.OnQueryTextListener, OnItemClickListener {

    public static int sItemPosition;

    private NotificationReceiver mNotificationReceiver;
    private FilterRecyclerView mFilterRecyclerView;
    private MyAsyncTask myAsyncTask;
    private RecyclerView mList;
    private FilterRecyclerView mAdapter;
    private ReminderDatabase mReminderDatabase;
    private TextView mNoReminderView;
    private BootReceiver mBootReceiver;
    private FloatingActionButton mAddReminderButton;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_main);

        mReminderDatabase = new ReminderDatabase(getApplicationContext());
        mToolbar = findViewById(R.id.toolbar);
        mAddReminderButton = findViewById(R.id.add_reminder);
        mList = findViewById(R.id.reminder_list);
        mNoReminderView = findViewById(R.id.no_reminder_text);

        createRecyclerView();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        // Устанввливаем действие на нажатие кнопки добавления напоминания
        mAddReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
            startActivity(intent);
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        filter.addAction(getString(R.string.boot_completed));
        filter.addAction(getString(R.string.action_boot_completed));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        intentFilter.addAction(getString(R.string.boot_completed));
        intentFilter.addAction(getString(R.string.action_boot_completed));
        mNotificationReceiver = new NotificationReceiver();
        mBootReceiver = new BootReceiver();
        registerReceiver(mNotificationReceiver, filter);
        registerReceiver(mBootReceiver, intentFilter);

        initDisplayModeSpinner();

        setUpDefaultSetting();

        mFilterRecyclerView = new FilterRecyclerView(getApplicationContext(), this);
    }

    /**
     * Метод для создания Recycler View
     */
    public void createRecyclerView() {
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new FilterRecyclerView(getApplicationContext(), this);
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycler_view_main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    /**
     * Метод для установки количества элементов Recycler View
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItemCount(getDefaultItemCount());
    }

    /**
     * Метод для пересоздания Recycler View
     */
    @Override
    public void onResume() {
        super.onResume();

        // Если нет напоминаний, отображаем сообщение с просьбой добавить напоминание
        List<Reminder> mTest = mReminderDatabase.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Метод для установки действия по нажатию на элементы меню
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Метод для реагирования на изменение прав установки цвета страницы
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
        if (backgroundColour != null) {
            if (backgroundColour.equals(getString(R.string.color_green))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            } else if (backgroundColour.equals(getString(R.string.pink_color))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorPink));
            } else if (backgroundColour.equals(getString(R.string.blue_color))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorBlue));
            } else {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.primary_dark));
            }
        }
    }

    /**
     * Метод для установки LayoutManager
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    /**
     * Метод для установки ненулевого количества элементов по умолчанию
     */
    public int getDefaultItemCount() {
        return 100;
    }

    /**
     * Метод для установки цвета страницы
     */
    public void setUpDefaultSetting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
        if (backgroundColour != null) {
            if (backgroundColour.equals(getString(R.string.color_green))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            } else if (backgroundColour.equals(getString(R.string.pink_color))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorPink));
            } else if (backgroundColour.equals(getString(R.string.blue_color))) {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.colorBlue));
            } else {
                findViewById(R.id.main_activity_id).setBackgroundColor(getResources().getColor(R.color.primary_dark));
            }
        }
    }

    /**
     * Метод для действия при нажатии на элемент Recycler View
     */
    @Override
    public void onClick(int id) {
        String mStringClickID = Integer.toString(id);

        Intent intent = new Intent(this, ReminderEditActivity.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNotificationReceiver);
        unregisterReceiver(mBootReceiver);
    }

    private class MyAsyncTask extends AsyncTask<Integer, Integer, List<ReminderItem>> {

        @Override
        protected List<ReminderItem> doInBackground(Integer... voids) {
            return mAdapter.getListData(mAdapter.getItemCount());
        }
    }

    /**
     * Метод для инициализации выпадающего списка
     */
    private void initDisplayModeSpinner() {
        Spinner spinner = findViewById(R.id.title_spinner);
        spinner.setAdapter(new SpinnerAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sItemPosition = 0;
                    createRecyclerView();
                } else if (position == 1) {
                    sItemPosition = 1;
                    createRecyclerView();
                } else if (position == 2) {
                    sItemPosition = 2;
                    createRecyclerView();
                } else if (position == 3) {
                    sItemPosition = 3;
                    createRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
