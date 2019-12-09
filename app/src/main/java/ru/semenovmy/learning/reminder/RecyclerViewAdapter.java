package ru.semenovmy.learning.reminder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static ru.semenovmy.learning.reminder.MainRecyclerViewActivity.sItemPosition;

/**
 * Класс адаптера для Recycler View
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VerticalItemHolder>
            implements Filterable {

    private final ArrayList<ReminderItem> mItems;
    List<ReminderItem> reminderItemsFull;
    public final LinkedHashMap<Integer, Integer> mIDmap = new LinkedHashMap<>();
    private final MultiSelector mMultiSelector = new MultiSelector();
    private int mTempPost;
    private List<TitleSorter> TitleSortList;
    private List<DateTimeSorter> DateTimeSortList;
    private ReminderDatabase mReminderDatabase;
    MainRecyclerViewActivity mainRecyclerViewActivity;

    RecyclerViewAdapter(Context context) {
        mItems = new ArrayList<>();
        reminderItemsFull = new ArrayList<>(mItems);
        mReminderDatabase = new ReminderDatabase(context);
        mainRecyclerViewActivity = new MainRecyclerViewActivity();
    }

    /**
    * Метод для установки количества элементов Recycler View
    */
    void setItemCount(int count) {
        mItems.clear();
        mItems.addAll(generateListData(count));
        notifyDataSetChanged();
    }

    /**
    * Метод для удаления элементов Recycler View
    */
    void onDeleteItem(int count) {
        mItems.clear();
        mItems.addAll(generateListData(count));
    }

    /**
    * Метод для удаления выбранных элементов Recycler View
    */
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

    /**
    * Класс для UI и данных для Recycler View
    */
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
                //mainRecyclerViewActivity.createRecyclerView();
                mTempPost = mainRecyclerViewActivity.mList
                        .getChildAdapterPosition(v);

                int mReminderClickID = mIDmap.get(mTempPost);
                mainRecyclerViewActivity.selectReminder(mReminderClickID);

            } else if (mMultiSelector.getSelectedPositions().isEmpty()) {
                mAdapter.setItemCount(mainRecyclerViewActivity.getDefaultItemCount());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AppCompatActivity activity = new MainRecyclerViewActivity();
            activity.startSupportActionMode(mainRecyclerViewActivity.mDeleteMode);
            mMultiSelector.setSelected(this, true);
            v.setBackgroundColor(Color.DKGRAY);
            return true;
        }

        /**
        * Метод для установки заголовка списка
        */
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

        /**
        * Метод для установки даты и времени списка
        */
        void setReminderDateTime(String datetime) {
            mDateAndTimeText.setText(datetime);
        }

        /**
        * Метод для установки повторений напоминания в списке
        */
        void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
            if (repeat.equals("true")) {
                mRepeatInfoText.setText(R.string.every + " " + repeatNo + " " + repeatType);
            } else if (repeat.equals("false")) {
                mRepeatInfoText.setText(R.string.repeat_off);
            }
        }

        /**
        * Метод для установки картинки для элемента списка
        */
        void setActiveImage(String active) {
            if (active.equals("true")) {
                mActiveImage.setImageResource(R.drawable.bell);
            } else if (active.equals("false")) {
                mActiveImage.setImageResource(R.drawable.notifications_grey);
            }
        }
    }

    /**
    * Метод для генерации данных списка
    */
   List<ReminderItem> generateListData(int count) {
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
   }

    /**
    * Метод для фильтрации списка
    */
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
}