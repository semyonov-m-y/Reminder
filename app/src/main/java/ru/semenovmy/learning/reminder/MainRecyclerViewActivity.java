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
        SearchView.OnQueryTextListener {

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
    private RecyclerViewAdapter mAdapter;
    public ReminderDatabase mReminderDatabase;
    private TextView mNoReminderView;
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
        mAdapter = new RecyclerViewAdapter(getApplicationContext());
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
     * Метод для действия при нажатии на элемент Recycler View
     */
    public void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        Intent intent = new Intent(this, ReminderEditActivity.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(intent, 1);
    }

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

    /**
     * Метод для нажатия на кнопку Назад по умолчанию
     */
/*    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mMultiSelector.clearSelections();
    }*/

    /**
     * Класс адаптера для Recycler View
     */
/*   public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VerticalItemHolder>
            implements Filterable {

        private final ArrayList<ReminderItem> mItems;
        List<ReminderItem> reminderItemsFull;

        RecyclerViewAdapter() {
            mItems = new ArrayList<>();
            reminderItemsFull = new ArrayList<>(mItems);
        }

        *//**
         * Метод для установки количества элементов Recycler View
         *//*
        void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(util.generateListData(count));
            notifyDataSetChanged();
        }

        *//**
         * Метод для удаления элементов Recycler View
         *//*
        void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(util.generateListData(count));
        }

        *//**
         * Метод для удаления выбранных элементов Recycler View
         *//*
        void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        @NonNull
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setActiveImage(item.mActive);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        *//**
         * Класс для UI и данных для Recycler View
         *//*
        public class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private final TextView mTitleText;
            private final TextView mDateAndTimeText;
            private final TextView mRepeatInfoText;
            private final ImageView mActiveImage;
            private final ImageView mThumbnailImage;
            private final ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private final RecyclerViewAdapter mAdapter;

            VerticalItemHolder(View itemView, RecyclerViewAdapter adapter) {
                super(itemView, mMultiSelector);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                mAdapter = adapter;

                mTitleText = itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = itemView.findViewById(R.id.active_image);
                mThumbnailImage = itemView.findViewById(R.id.thumbnail_image);
            }

            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = mIDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if (mMultiSelector.getSelectedPositions().isEmpty()) {
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = MainRecyclerViewActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                v.setBackgroundColor(Color.DKGRAY);
                return true;
            }

            *//**
             * Метод для установки заголовка списка
             *//*
            void setReminderTitle(String title) {
                mTitleText.setText(title);
                String letter = "A";

                if (title != null && !title.isEmpty()) {
                    letter = title.substring(0, 1);
                }

                int color = mColorGenerator.getRandomColor();

                // Создать круглую иконку рандомного цвета с первой буквой заголовка элемента списка
                mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);
                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            *//**
             * Метод для установки даты и времени списка
             *//*
            void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            *//**
             * Метод для установки повторений напоминания в списке
             *//*
            void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if (repeat.equals("true")) {
                    mRepeatInfoText.setText(getString(R.string.every) + " " + repeatNo + " " + repeatType);
                } else if (repeat.equals("false")) {
                    mRepeatInfoText.setText(getString(R.string.repeat_off));
                }
            }

            *//**
             * Метод для установки картинки для элемента списка
             *//*
            void setActiveImage(String active) {
                if (active.equals("true")) {
                    mActiveImage.setImageResource(R.drawable.bell);
                } else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.notifications_grey);
                }
            }
        }

        *//**
         * Метод для генерации данных списка
         *//*
*//*        List<ReminderItem> generateListData(int count) {
            ArrayList<ReminderItem> items = new ArrayList<>();

            List<Reminder> reminders = mReminderDatabase.getAllReminders();

            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList = new ArrayList<>();
            DateTimeSortList = new ArrayList<>();
            TitleSortList = new ArrayList<>();

            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatAmount());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;

            for (int k = 0; k < Titles.size(); k++) {
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                TitleSortList.add(new TitleSorter(key, Titles.get(k)));
                key++;
            }

            if (sItemPosition == 0) {
                Collections.sort(DateTimeSortList, new DateTimeComparator());
            } else if (sItemPosition == 1) {
                Collections.sort(DateTimeSortList, new DateTimeComparator());
                Collections.reverse(DateTimeSortList);
            } else if (sItemPosition == 2) {
                Collections.sort(TitleSortList, new TitleComparator());
            } else if (sItemPosition == 3) {
                Collections.sort(TitleSortList, new TitleComparator());
                Collections.reverse(TitleSortList);
            }

            int k = 0;

            // Добавляем данные в каждый элемент списка
            if (sItemPosition == 0 || sItemPosition == 1) {
                for (DateTimeSorter item : DateTimeSortList) {
                    int i = item.getIndex();

                    if (Actives.get(i).equals("true")) {
                        items.add(new ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                                RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                    } else if (Actives.get(i).equals("false")) {
                        mReminderDatabase.deleteReminder(reminders.get(i));
                    }

                    mIDmap.put(k, IDList.get(i));
                    k++;
                }
                reminderItemsFull = items;
            } else {
                for (TitleSorter item : TitleSortList) {
                    int i = item.getIndex();

                    if (Actives.get(i).equals("true")) {
                        items.add(new ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                                RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                    } else if (Actives.get(i).equals("false")) {
                        mReminderDatabase.deleteReminder(reminders.get(i));
                    }

                    mIDmap.put(k, IDList.get(i));
                    k++;
                }
            }
            return items;
        }*//*

        *//**
         * Метод для фильтрации списка
         *//*
        @Override
        public Filter getFilter() {
            return filter;
        }

        private Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ReminderItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(reminderItemsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ReminderItem item : reminderItemsFull) {
                        if (item.mTitle.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems.clear();
                mItems.addAll((List)results.values);
                notifyDataSetChanged();
            }
        };
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNotificationReceiver);
    }
}
