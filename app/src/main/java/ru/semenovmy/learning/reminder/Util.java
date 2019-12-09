package ru.semenovmy.learning.reminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class Util {

    Context context;
    private ReminderDatabase mReminderDatabase = new ReminderDatabase(context);
    private List<DateTimeSorter> DateTimeSortList;
    private List<TitleSorter> TitleSortList;
    private static int sItemPosition;
    private final LinkedHashMap<Integer, Integer> mIDmap = new LinkedHashMap<>();
    private final ArrayList<ReminderItem> mItems = new ArrayList<>();
    List<ReminderItem> reminderItemsFull = new ArrayList<>(mItems);

    /**
     * Метод для генерации данных списка
     */
    public List<ReminderItem> generateListData(int count) {
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
}
