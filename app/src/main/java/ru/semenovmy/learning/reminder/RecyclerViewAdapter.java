package ru.semenovmy.learning.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import ru.semenovmy.learning.reminder.comparator.DateTimeComparator;
import ru.semenovmy.learning.reminder.comparator.TitleComparator;
import ru.semenovmy.learning.reminder.data.database.Reminder;
import ru.semenovmy.learning.reminder.data.database.ReminderDatabase;
import ru.semenovmy.learning.reminder.data.model.ReminderItem;
import ru.semenovmy.learning.reminder.sorter.DateTimeSorter;
import ru.semenovmy.learning.reminder.sorter.TitleSorter;

import static ru.semenovmy.learning.reminder.MainRecyclerViewActivity.sItemPosition;

/**
 * Класс адаптера для Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VerticalItemHolder> {

    public final ArrayList<ReminderItem> mItems;
    public List<ReminderItem> mReminderItemsFull;
    public final LinkedHashMap<Integer, Integer> mIDmap = new LinkedHashMap<>();
    public final MultiSelector mSelector = new MultiSelector();
    public int mTempPost;
    public List<TitleSorter> TitleSortList;
    public List<DateTimeSorter> DateTimeSortList;
    public ReminderDatabase mReminderDatabase;
    public MainRecyclerViewActivity mMainRecyclerViewActivity;
    public OnItemClickListener mOnItemClickListener;
    public Context context;
    public int mLastPosition = -1;
    public List<Reminder> mReminders;

    public RecyclerViewAdapter(Context context, OnItemClickListener listener) {
        mItems = new ArrayList<>();
        mReminderItemsFull = new ArrayList<>(mItems);
        mReminderDatabase = new ReminderDatabase(context);
        mMainRecyclerViewActivity = new MainRecyclerViewActivity();
        mOnItemClickListener = listener;
        this.context = context;
    }

    /**
     * Метод для установки количества элементов Recycler View
     *
     * @param count количество элементов
     */
    void setItemCount(int count) {
        mItems.clear();
        mItems.addAll(getListData(count));
        notifyDataSetChanged();
    }

    /**
     * Метод для удаления выбранных элементов Recycler View
     *
     * @param selected выбранный элемент
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
        itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType.substring(0, 1).toLowerCase() + ".");
        itemHolder.setActiveImage(item.mActive);
        itemHolder.bind();

        setAnimation(itemHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Метод анимации сдвига списка слева направо
     *
     * @param viewToAnimate элемент для анимации
     * @param position      позиция элемента
     */
    private void setAnimation(View viewToAnimate, int position) {
        // Если список ранее не был отображен, применится анимация
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    /**
     * Класс для UI и данных для Recycler View
     */
    public class VerticalItemHolder extends SwappingHolder {

        private final TextView mTitleText;
        private final TextView mDateAndTimeText;
        private final TextView mRepeatInfoText;
        private final ImageView mActiveImage;
        private final ImageView mThumbnailImage;
        private final ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;
        private final RecyclerViewAdapter mAdapter;

        VerticalItemHolder(View itemView, RecyclerViewAdapter adapter) {
            super(itemView, mSelector);

            mAdapter = adapter;

            mTitleText = itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = itemView.findViewById(R.id.recycle_date_time);
            mRepeatInfoText = itemView.findViewById(R.id.recycle_repeat_info);
            mActiveImage = itemView.findViewById(R.id.active_image);
            mThumbnailImage = itemView.findViewById(R.id.thumbnail_image);

            itemView.setOnLongClickListener(v -> {
                PopupMenu menu = new PopupMenu(v.getContext(), v);
                menu.inflate(R.menu.menu_delete);
                menu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_delete:
                            mReminderDatabase.deleteReminder(mReminders.get(getAdapterPosition()));
                            removeItemSelected(getAdapterPosition());

                            Toast.makeText(context, context.getString(R.string.reminder_deleted), Toast.LENGTH_SHORT).show();
                        default:
                    }
                    return true;
                });
                menu.show();
                return true;
            });
        }

        void bind() {
            itemView.setOnClickListener(v -> {
                if (!mSelector.tapSelection(this)) {
                    mTempPost = getAdapterPosition();
                    int mReminderClickID = mIDmap.get(mTempPost);

                    mOnItemClickListener.onClick(mReminderClickID);
                }
            });
        }

        /**
         * Метод для установки заголовка списка
         *
         * @param title устанавливаемый заголовок
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
         *
         * @param datetime устанавливаемые дата и время
         */
        void setReminderDateTime(String datetime) {
            mDateAndTimeText.setText(datetime);
        }

        /**
         * Метод для установки повторений напоминания в списке
         *
         * @param repeat     повторять или нет
         * @param repeatNo   количество повторений
         * @param repeatType тип повторений
         */
        void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
            if (repeat.equals("true")) {
                mRepeatInfoText.setText(context.getString(R.string.every) + " " + repeatNo + " " + repeatType);
            } else if (repeat.equals("false")) {
                mRepeatInfoText.setText(R.string.repeat_off);
            }
        }

        /**
         * Метод для установки картинки для элемента списка
         *
         * @param active активно ли напоминание
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
     * * Метод для генерации данных списка
     */
    public List<ReminderItem> getListData(int count) {
        ArrayList<ReminderItem> items = new ArrayList<>();

        mReminders = mReminderDatabase.getAllReminders();

        List<String> Titles = new ArrayList<>();
        List<String> Repeats = new ArrayList<>();
        List<String> RepeatNos = new ArrayList<>();
        List<String> RepeatTypes = new ArrayList<>();
        List<String> Actives = new ArrayList<>();
        List<String> DateAndTime = new ArrayList<>();
        List<Integer> IDList = new ArrayList<>();
        DateTimeSortList = new ArrayList<>();
        TitleSortList = new ArrayList<>();

        for (Reminder r : mReminders) {
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

        listSort(sItemPosition);

        int k = 0;

        // Добавляем данные в каждый элемент списка
        if (sItemPosition == 0 || sItemPosition == 1) {
            for (DateTimeSorter item : DateTimeSortList) {
                int i = item.getIndex();

                if (Actives.get(i).equals("true")) {
                    items.add(new ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                            RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                } else if (Actives.get(i).equals("false")) {
                    mReminderDatabase.deleteReminder(mReminders.get(i));
                }

                mIDmap.put(k, IDList.get(i));
                k++;
            }
            mReminderItemsFull = items;
        } else {
            for (TitleSorter item : TitleSortList) {
                int i = item.getIndex();

                if (Actives.get(i).equals("true")) {
                    items.add(new ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                            RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                } else if (Actives.get(i).equals("false")) {
                    mReminderDatabase.deleteReminder(mReminders.get(i));
                }

                mIDmap.put(k, IDList.get(i));
                k++;
            }
        }
        return items;
    }

    public List<ReminderItem> getmReminderItemsFull() {
        return mReminderItemsFull;
    }

    public ArrayList<ReminderItem> getmItems() {
        return mItems;
    }

    /**
     * Метод для сортировки списка
     * @param position выбор элемента spinner-а
     * @return возвращает номер позиции элемента
     */
    public int listSort(int position) {
        if (position == 0) {
            Collections.sort(DateTimeSortList, new DateTimeComparator());
        } else if (position == 1) {
            Collections.sort(DateTimeSortList, new DateTimeComparator());
            Collections.reverse(DateTimeSortList);
        } else if (position == 2) {
            Collections.sort(TitleSortList, new TitleComparator());
        } else if (position == 3) {
            Collections.sort(TitleSortList, new TitleComparator());
            Collections.reverse(TitleSortList);
        }
        return position;
    }
}