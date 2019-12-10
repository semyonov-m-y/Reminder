package ru.semenovmy.learning.reminder;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Main class for recycler view
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class MainRecyclerViewActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        SearchView.OnQueryTextListener, OnItemClickListener  {

    public final LinkedHashMap<Integer, Integer> mIDmap = new LinkedHashMap<>();
    private final MultiSelector mMultiSelector = new MultiSelector();

    public static int sItemPosition;

    private BootReceiver bootReceiver;
    private FloatingActionButton mAddReminderButton;
    private int mTempPost;
    private List<TitleSorter> TitleSortList;
    private List<DateTimeSorter> DateTimeSortList;
    private NotificationReceiver mNotificationReceiver;
    public RecyclerView mList;
    public RecyclerViewAdapter mAdapter;
    public ReminderDatabase mReminderDatabase;
    private TextView mNoReminderView;
    private Toolbar mToolbar;

    FilterRecyclerView filterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_main);

        mReminderDatabase = new ReminderDatabase(getApplicationContext());
        mToolbar = findViewById(R.id.toolbar);
        mAddReminderButton = findViewById(R.id.add_reminder);
        mList = findViewById(R.id.reminder_list);
        mNoReminderView = findViewById(R.id.no_reminder_text);

        List<Reminder> mReminders = mReminderDatabase.getAllReminders();

        // Если нет напоминаний, отображаем сообщение с просьбой добавить напоминание
        if (mReminders.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

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
        bootReceiver = new BootReceiver();
        registerReceiver(mNotificationReceiver, filter);
        registerReceiver(bootReceiver, intentFilter);

        initDisplayModeSpinner();

        setUpDefaultSetting();

        filterRecyclerView = new FilterRecyclerView(getApplicationContext(), this);
    }

    /**
     * Метод для создания меню при долгом нажатии на элемент Recycler View
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_delete_reminder, menu);
    }

    /**
     * Метод для создания Recycler View
     */
    public void createRecyclerView() {
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new RecyclerViewAdapter(getApplicationContext(), this);
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);
    }

    /**
     * Метод для создания меню
     */
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
                //mAdapter.getFilter().filter(newText);
                filterRecyclerView.getFilter().filter(newText);
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

    /**
     * Метод для реагирования на изменение прав установки цвета страницы
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
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
     * Метод для выбора нескольких элементов в Recycler View
     */
    public final androidx.appcompat.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_delete_reminder, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.discard_reminder:
                    // Закрыть меню
                    actionMode.finish();

                    // Получить id напоминания равному элементу Recycler View
                    for (int i = mIDmap.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            int id = mIDmap.get(i);

                            Reminder temp = mReminderDatabase.getReminder(id);
                            mReminderDatabase.deleteReminder(temp);
                            mAdapter.removeItemSelected(i);
                            mNotificationReceiver.cancelNotification(getApplicationContext(), id);
                        }
                    }

                    mMultiSelector.clearSelections();

                    // Пересоздать Recycler View, чтобы переопределить элементы
                    mAdapter.onDeleteItem(getDefaultItemCount());

                    // Отобразить сообщение о подтверждении удаления элемента
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.reminder_deleted), Toast.LENGTH_SHORT).show();

                    // Если нет напоминаний, отображаем сообщение с просьбой добавить напоминание
                    List<Reminder> mTest = mReminderDatabase.getAllReminders();

                    if (mTest.isEmpty()) {
                        mNoReminderView.setVisibility(View.VISIBLE);
                    } else {
                        mNoReminderView.setVisibility(View.GONE);
                    }

                    return true;

                case R.id.save_reminder:
                    // Закрыть меню
                    actionMode.finish();

                    mMultiSelector.clearSelections();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    /**
     * Метод для установки цвета страницы
     */
    private void setUpDefaultSetting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNotificationReceiver);
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
}
